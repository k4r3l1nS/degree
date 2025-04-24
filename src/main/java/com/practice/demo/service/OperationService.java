package com.practice.demo.service;

import com.practice.demo.components.event.publishers.OperationProceededPublisher;
import com.practice.demo.custom_annotations.DtoCorrectnessCheck;
import com.practice.demo.dto.entity_dto.OperationDto;
import com.practice.demo.dto.paging_and_sotring_dto.AbstractPagingAndSortingDto;
import com.practice.demo.dto.specification_dto.models.OperationSpecificationDto;
import com.practice.demo.exceptions.models.ResourceNotFoundException;
import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.specification.Condition;
import com.practice.demo.models.specification.SpecificationBuilder;
import com.practice.demo.repos.entity_repos.AccountRepository;
import com.practice.demo.repos.entity_repos.OperationRepository;
import com.practice.demo.repos.db_view_repos.OperationViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationService {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final OperationViewRepository operationViewRepository;

    private final CurrencyUnit currencyUnit;

    private final OperationProceededPublisher operationProceededPublisher;

    @DtoCorrectnessCheck(filled = true)
    public void addOperation(OperationDto operationDto, Long accountId) {

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id = " + accountId + " not found"));

        var operation = operationDto.toEntity();
        account.performOperation(operation, currencyUnit.convert(operation.getCurrencyFrom(),
                account.getCurrency(), operation.getTransactionSum()));

        operationRepository.save(operation);

        operationProceededPublisher.publishEvent(operation);
    }

    public Operation findById(Long operationId) {

        return operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation with id = " + operationId + " not found"));
    }

    public OperationView findViewById(Long operationId) {

        return operationRepository.findViewById(operationId);
    }

    public OperationView findOneOperationView(Long accountId) {

        var specification = new SpecificationBuilder<OperationView>()
                .with(Condition.builder()
                        .fieldName("accountId").operation(Condition.OperationType.EQUALS)
                        .value(accountId).logicalOperator(Condition.LogicalOperatorType.AND)
                        .build())
                .build();

        List<OperationView> operationView =  operationViewRepository.findAll(specification);

        return operationView.getFirst();
    }

    public Page<OperationView> fetchNextPageByAccountId(AbstractPagingAndSortingDto abstractPagingAndSortingDto,
                                                        OperationSpecificationDto operationSpecificationDto, Long accountId) {

        var conditions = operationSpecificationDto.toConditions(accountId);

        var specification = new SpecificationBuilder<OperationView>().with(conditions).build();
        var pageRequest = abstractPagingAndSortingDto.toPageRequest();

        return operationViewRepository.findAll(specification, pageRequest);
    }

    public List<OperationView> fetchOperationViewsByUsername(String username) {
        return operationRepository.fetchOperationViewsByUsername(username);
    }
}
