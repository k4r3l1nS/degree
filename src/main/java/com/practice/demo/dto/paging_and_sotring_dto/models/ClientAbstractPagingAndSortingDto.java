package com.practice.demo.dto.paging_and_sotring_dto.models;

import com.practice.demo.dto.paging_and_sotring_dto.AbstractPagingAndSortingDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientAbstractPagingAndSortingDto extends AbstractPagingAndSortingDto {

    @Override
    public void initializeDefaultValues() {

        DEFAULT_PAGE = 0;
        DEFAULT_SIZE = 3;
        DEFAULT_SORT_BY = "numberOfAccounts";
        DEFAULT_ORDER = "DESC";
    }
}
