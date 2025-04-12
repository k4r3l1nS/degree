package com.practice.demo.dto.entity_dto;

import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountListDto {

    private CommonInfo commonInfo;
    private Page<AccountInfo> accounts;

    @Getter
    @Setter
    @Builder
    private static final class CommonInfo {

        private String fullName;
        private Long clientId;
    }

    @Getter
    @Setter
    @Builder
    private static final class AccountInfo {

        private Long accountId;

        private String accountName;

        private Double balance;

        private Currency currency;

        private Integer numberOfOperations;

        private LocalDateTime latestOperation;

        private Account.AccountKind accountKind;
    }

    public boolean isEmpty() {

        return accounts.isEmpty() || accounts.get().findFirst().get().accountId == null;
    }

    public static AccountListDto valueFrom(Page<AccountView> accountViews, AccountView representativeView) {

        AccountListDto accountListDto = new AccountListDto();

        accountListDto.setCommonInfo(CommonInfo.builder()
                        .clientId(representativeView.getClientId()).fullName(representativeView.getFullName())
                .build());

        Page<AccountInfo> accountInfoPage = accountViews.isEmpty() ? Page.empty() :
                accountViews.map(accountView ->
                AccountInfo.builder()
                        .accountId(accountView.getAccountId()).accountName(accountView.getAccountName())
                        .balance(accountView.getBalance()).currency(accountView.getCurrency())
                        .numberOfOperations(accountView.getNumberOfOperations())
                        .latestOperation(accountView.getLatestOperation())
                        .accountKind(accountView.getAccountKind())
                        .build());

        accountListDto.setAccounts(accountInfoPage);

        return accountListDto;
    }
}
