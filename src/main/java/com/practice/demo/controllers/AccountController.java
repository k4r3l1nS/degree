package com.practice.demo.controllers;

import com.practice.demo.dto.entity_dto.AccountDto;
import com.practice.demo.dto.entity_dto.AccountListDto;
import com.practice.demo.dto.entity_dto.TransferBetweenAccountsDto;
import com.practice.demo.dto.paging_and_sotring_dto.models.AccountAbstractPagingAndSortingDto;
import com.practice.demo.dto.specification_dto.models.AccountSpecificationDto;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import com.practice.demo.service.AccountService;
import com.practice.demo.uri_handler.UriHandler;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/clients/{id}")
    public String clientById(@ModelAttribute AccountAbstractPagingAndSortingDto accountPagingAndSortingDto,
                             @ModelAttribute AccountSpecificationDto accountSpecificationDto,
                             @PathVariable(value = "id") Long clientId,
                             Model model, HttpServletRequest httpServletRequest) {

        model.addAttribute("uriPairList", UriHandler.parse(httpServletRequest.getRequestURI()));

        accountPagingAndSortingDto.fillEmptyFields();
        accountSpecificationDto.fillEmptyFields();

        model.addAttribute("PASdto", accountPagingAndSortingDto);
        model.addAttribute("ASdto", accountSpecificationDto);

        AccountListDto accountListDto = AccountListDto.valueFrom(
                accountService.fetchNextPageByClientId(accountPagingAndSortingDto, accountSpecificationDto, clientId),
                accountService.findOneAccountView(clientId));

        model.addAttribute("accountListDto", accountListDto);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("accountKinds", Account.AccountKind.values());

        return "accounts";
    }

    @GetMapping("/clients/{id}/new-account")
    public String newAccount(@PathVariable(value = "id") Long clientId, Model model) {

        model.addAttribute("client_id", clientId);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("accountKinds", Account.AccountKind.values());
        model.addAttribute("accountDto", AccountDto.builder().build());

        return "new-account";
    }

    @PostMapping("/clients/{id}/new-account")
    public String addAccount(@PathVariable(value = "id") Long clientId, @ModelAttribute AccountDto accountDto) {

        accountService.addAccount(accountDto, clientId);

        return "redirect:/clients/{id}";
    }

    @GetMapping("/clients/{client_id}/account-{account_id}/edit")
    public String editAccount(@PathVariable(value = "client_id") Long clientId,
                              @PathVariable(value = "account_id") Long accountId, Model model) {

        var accountView = accountService.findAccountViewById(accountId);

        model.addAttribute("account", accountView);
        model.addAttribute("currencies", Currency.values());
//        model.addAttribute("rates", new CurrencyRatesOld().getRates());
        model.addAttribute("accountKinds", Account.AccountKind.values());
        model.addAttribute("accountDto", AccountDto.builder().build());

        return "edit-account";
    }

    @PutMapping("/clients/{client_id}/account-{account_id}/edit")
    public String updateAccount(@PathVariable(value = "client_id") Long clientId,
                                @PathVariable(value = "account_id") Long accountId,
                                @ModelAttribute AccountDto accountDto) {

        accountService.updateAccount(accountDto, accountId);

        return "redirect:/clients/{client_id}";
    }

    @GetMapping("/clients/{client_id}/account-{account_id}/deactivate")
    public String deactivateAccount(@PathVariable(value = "client_id") Long clientId,
                                 @PathVariable(value = "account_id") Long accountId) {

        accountService.deactivateAccountById(accountId);

        return "redirect:/clients/{client_id}";
    }

    @GetMapping("/clients/{client_id}/transfer")
    public String newTransfer(@PathVariable(value = "client_id") Long clientId, Model model) {

        model.addAttribute("currencies", Currency.values());
        model.addAttribute("transferDto", TransferBetweenAccountsDto.builder().build());


        return "transfer-between-accounts";
    }

    @PostMapping("/clients/{client_id}/transfer")
    public String transferBetweenAccounts(@PathVariable(value = "client_id") Long clientId,
                                          @ModelAttribute TransferBetweenAccountsDto transferBetweenAccountsDto,
                                          Model model) {

        accountService.transferBetweenAccounts(transferBetweenAccountsDto, clientId);

        return "redirect:/clients/{client_id}";
    }
}
