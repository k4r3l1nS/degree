package com.practice.demo.service;

import com.practice.demo.components.event.publishers.OperationProceededPublisher;
import com.practice.demo.custom_annotations.DtoCorrectnessCheck;
import com.practice.demo.dto.entity_dto.AccountDto;
import com.practice.demo.dto.entity_dto.TransferBetweenAccountsDto;
import com.practice.demo.dto.specification_dto.models.AccountSpecificationDto;
import com.practice.demo.dto.paging_and_sotring_dto.AbstractPagingAndSortingDto;
import com.practice.demo.exceptions.models.*;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.entities.Client;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.models.specification.Condition;
import com.practice.demo.models.specification.SpecificationBuilder;
import com.practice.demo.repos.entity_repos.AccountRepository;
import com.practice.demo.repos.entity_repos.ClientRepository;
import com.practice.demo.repos.db_view_repos.AccountViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;
    private final AccountViewRepository accountViewRepository;

    private final CurrencyUnit currencyUnit;

    private final OperationProceededPublisher operationProceededPublisher;

    @DtoCorrectnessCheck(filled = true)
    public void addAccount(AccountDto accountDto, Long clientId) {
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id = " + clientId + " not found"));
        var account = accountDto.toEntity();

        Operation firstDeposit = Operation.getOperation(Operation.OperationKind.DEPOSIT,
                accountDto.getBalance(), account.getCurrency());

        account.performOperation(firstDeposit, currencyUnit.convert(Currency.resolveByName(accountDto.getCurrency()),
                        account.getCurrency(), accountDto.getBalance()));

        client.addAccount(account);
        accountRepository.save(account);

        operationProceededPublisher.publishEvent(firstDeposit);
    }

    @DtoCorrectnessCheck
    public void updateAccount(AccountDto accountDto, Long accountId) {

        if (accountRepository.existsByName(accountDto.getAccountName())
                && accountRepository.findByName(accountDto.getAccountName()).isActive()) {

            throw new AccountNameAlreadyTakenException("This account name is already taken");
        }

        if (accountDto.getCurrency() != null && !accountDto.getCurrency().isEmpty() &&
                !currencyUnit.isCorrect(accountDto.getCurrency())) {

            throw new CurrencyNotSupportedException("Currency with name " +
                    accountDto.getCurrency() + " is not supported");
        }

        var accountEntity = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id = " + accountId + " not found"));

        accountDto.setBalance(currencyUnit.convert(accountEntity.getCurrency(),
                Currency.resolveByName(accountDto.getCurrency()), accountEntity.getBalance()));
        accountDto.mapTo(accountEntity);
    }

    public Account findById(Long accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id = " + accountId + " not found"));
    }

    @Transactional(readOnly = true)
    public AccountView findAccountViewById(Long accountId) {

        return accountViewRepository.findAccountViewById(accountId);
    }

    @Transactional
    public void deactivateAccountById(Long accountId) {

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id = " + accountId + " not found"));

        account.setActive(false);
        if (BigDecimal.ZERO.compareTo(account.getBalance()) < 0) {
            account.performOperation(
                    Operation.getOperation(
                            Operation.OperationKind.WITHDRAWAL,
                            account.getBalance(),
                            account.getCurrency()
                    ),
                    account.getBalance()
            );
            //Вывод средств куда-либо
        }
        // Закрытие счёта
        account.performOperation(
                Operation.getOperation(
                        Operation.OperationKind.WITHDRAWAL,
                        account.getBalance(),
                        account.getCurrency()
                ),
                account.getBalance()
        );

    }

    @Transactional
    public Page<AccountView> fetchNextPageByClientId(AbstractPagingAndSortingDto abstractPagingAndSortingDto,
                                                     AccountSpecificationDto accountSpecificationDto, Long clientId) {

        var conditions = accountSpecificationDto.toConditions(clientId);

        var specification = new SpecificationBuilder<AccountView>().with(conditions).build();
        var pageRequest = abstractPagingAndSortingDto.toPageRequest();

        return accountViewRepository.findAll(specification, pageRequest);
    }

    @Transactional
    public List<AccountView> fetchAccountViewsByUsername(String username) {
        Client client = clientRepository.findByUsername(username).orElseThrow(
                () -> new NoSuchElementException("No such username " + username)
        );
        return accountRepository.findByClientId(client.getId())
                .stream()
                .map(account -> accountViewRepository.findAccountViewById(account.getId()))
                .filter(accountView -> accountView != null && accountView.getAccountId() != null && accountView.getIsActive())
                .toList();
    }

    public AccountView findOneAccountView(Long clientId) {

        Specification<AccountView> specification = new SpecificationBuilder<AccountView>()
                .with(Condition.builder()
                        .fieldName("clientId").operation(Condition.OperationType.EQUALS)
                        .value(clientId).logicalOperator(Condition.LogicalOperatorType.AND)
                        .build())
                .with(Condition.builder()
                        .fieldName("isActive").operation(Condition.OperationType.IN)
                        .values(Arrays.asList(true, null)).logicalOperator(Condition.LogicalOperatorType.END)
                        .build())
                .build();

        List<AccountView> accountView = accountViewRepository.findAll(specification);

        return accountView.getFirst();
    }

    @Transactional
    public void transferBetweenAccounts(TransferBetweenAccountsDto transferBetweenAccountsDto, Long clientId)
            throws EmptyFieldException, ResourceNotFoundException {

        transferBetweenAccountsDto.throwIfNotFilled(false);
        currencyUnit.throwIfNotSupported(transferBetweenAccountsDto.getCurrency());

        Account accountFrom = accountRepository
                .findByNameAndClientId(transferBetweenAccountsDto.getAccountFromName(), clientId);
        Account accountTo = accountRepository.findByName(transferBetweenAccountsDto.getAccountToName());

        if (accountFrom == null) {
            throw accountRepository.existsByName(transferBetweenAccountsDto.getAccountFromName()) ?
                    new ForbiddenResourceException("This account does not belong to client (id = " + clientId + ")") :
                    new ResourceNotFoundException("Счёт, с которого переводятся деньги, не найден");
        }

        if (accountTo == null) {
            throw new ResourceNotFoundException("Счёт, на который переводятся деньги, не найден");
        }

        accountFrom.throwIfNotEnoughMoney(currencyUnit
                .convert(Currency.resolveByName(transferBetweenAccountsDto.getCurrency()),
                        accountFrom.getCurrency(), transferBetweenAccountsDto.getTransactionSum()));

        if (!accountFrom.getId().equals(accountTo.getId())) {

            var withdrawalOperation = Operation.getOperation(Operation.OperationKind.WITHDRAWAL,
                    transferBetweenAccountsDto.getTransactionSum(),
                    Currency.resolveByName(transferBetweenAccountsDto.getCurrency()));
            var depositOperation = Operation.getOperation(Operation.OperationKind.DEPOSIT,
                    transferBetweenAccountsDto.getTransactionSum(),
                    Currency.resolveByName(transferBetweenAccountsDto.getCurrency()));

            accountFrom.performOperation(withdrawalOperation,
                    currencyUnit.convert(Currency.resolveByName(transferBetweenAccountsDto.getCurrency()),
                            accountFrom.getCurrency(), transferBetweenAccountsDto.getTransactionSum()));
            accountTo.performOperation(depositOperation,
                    currencyUnit.convert(Currency.resolveByName(transferBetweenAccountsDto.getCurrency()),
                            accountTo.getCurrency(), transferBetweenAccountsDto.getTransactionSum()));

            operationProceededPublisher.publishEvent(withdrawalOperation);
            operationProceededPublisher.publishEvent(depositOperation);
        }
    }

    @Transactional
    public void addMoneyToAccount(TransferBetweenAccountsDto transferBetweenAccountsDto, Long clientId) {
        transferBetweenAccountsDto.throwIfNotFilled(true);
        Account accountTo = Optional.ofNullable(
                accountRepository.findByName(transferBetweenAccountsDto.getAccountToName())
        ).orElseThrow(() -> new ResourceNotFoundException("Счёт, на который переводятся деньги, не найден"));
        Operation depositOperation = Operation.getOperation(
                Operation.OperationKind.DEPOSIT,
                transferBetweenAccountsDto.getTransactionSum(),
                Currency.resolveByName(transferBetweenAccountsDto.getCurrency())
        );
        accountTo.performOperation(
                depositOperation,
                currencyUnit.convert(
                        Currency.resolveByName(transferBetweenAccountsDto.getCurrency()),
                        accountTo.getCurrency(),
                        transferBetweenAccountsDto.getTransactionSum()
                )
        );
    }

    @Transactional
    public void deactivateAccountWithTransfer(Long deletedAccountId, Long transferToAccountId, Long clientId) {
        Account accountToDelete = accountRepository.findById(deletedAccountId).orElseThrow(
                () -> new ResourceNotFoundException("Удаляемый счёт не найден")
        );
        Account accountToTransfer = accountRepository.findById(transferToAccountId).orElseThrow(
                () -> new ResourceNotFoundException("Счёт, на который переводятся деньги, не найден")
        );

        transferBetweenAccounts(
                TransferBetweenAccountsDto.builder()
                        .accountFromName(accountToDelete.getName())
                        .accountToName(accountToTransfer.getName())
                        .currency(accountToDelete.getCurrency().getName())
                        .transactionSum(accountToDelete.getBalance())
                        .build(),
                clientId
        );
        deactivateAccountById(deletedAccountId);
    }

    @Transactional
    public void changeAccountCurrency(Long accountId, Currency newCurrency) {
        Account modifiedAccount = accountRepository.findById(accountId).orElseThrow(
                () -> new ResourceNotFoundException("Счёт, валюта которого изменяется, не найден")
        );
        modifiedAccount.setBalance(currencyUnit.convert(
                modifiedAccount.getCurrency(), newCurrency, modifiedAccount.getBalance()
        ));
        modifiedAccount.setCurrency(newCurrency);
    }

    @Transactional
    public Optional<Operation> extractLatestOperation(Long id) {
        List<Operation> operations = accountRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Счёт не найден")
        ).getOperations();
        return operations.isEmpty() ? Optional.empty() : Optional.of(operations.getLast());
    }
}
