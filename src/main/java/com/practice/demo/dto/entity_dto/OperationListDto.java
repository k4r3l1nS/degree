package com.practice.demo.dto.entity_dto;

import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.currency_enum.Currency;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationListDto {

    private CommonInfo commonInfo;
    private Page<OperationInfo> operations;

    @Getter
    @Setter
    @Builder
    private static final class CommonInfo {

        private Long clientId;
        private Long accountId;
        private String ownerFullName;
        private String accountName;
        private Currency currency;
        private Double balance;
    }

    @Getter
    @Setter
    @Builder
    public static final class OperationInfo {

        private Long operationId;

        private Operation.OperationKind operationKind;

        private Double transactionSum;

        private Currency currencyFrom;

        private LocalDateTime operationDateTime;
    }

    public boolean isEmpty() {

        return operations.isEmpty();
    }

    public static OperationListDto valueFrom(Page<OperationView> operationViews, OperationView representativeView) {

        OperationListDto operationListDto = new OperationListDto();

        operationListDto.setCommonInfo(CommonInfo.builder()
                        .accountId(representativeView.getAccountId()).accountName(representativeView.getAccountName())
                        .balance(representativeView.getBalance()).clientId(representativeView.getClientId())
                        .currency(representativeView.getAccountCurrency()).ownerFullName(representativeView.getOwnerFullName())
                .build());

        Page<OperationInfo> operationInfoPage = operationViews.isEmpty() ? Page.empty() :
                operationViews.map(operationView ->
                        OperationInfo.builder()
                                .operationId(operationView.getOperationId()).operationKind(operationView.getOperationKind())
                                .transactionSum(operationView.getTransactionSum()).currencyFrom(operationView.getCurrencyFrom())
                                .operationDateTime(operationView.getOperationDateTime())
                                .build());

        operationListDto.setOperations(operationInfoPage);

        return operationListDto;
    }
}
