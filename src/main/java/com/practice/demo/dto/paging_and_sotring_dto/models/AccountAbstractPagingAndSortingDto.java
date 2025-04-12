package com.practice.demo.dto.paging_and_sotring_dto.models;

import com.practice.demo.dto.paging_and_sotring_dto.AbstractPagingAndSortingDto;

public class AccountAbstractPagingAndSortingDto extends AbstractPagingAndSortingDto {


    @Override
    public void initializeDefaultValues() {

        DEFAULT_PAGE = 0;
        DEFAULT_SIZE = 5;
        DEFAULT_SORT_BY = "accountName";
        DEFAULT_ORDER = "ASC";
    }
}
