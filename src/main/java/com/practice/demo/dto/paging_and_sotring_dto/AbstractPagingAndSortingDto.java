package com.practice.demo.dto.paging_and_sotring_dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public abstract class AbstractPagingAndSortingDto {

    protected static int DEFAULT_PAGE;
    protected static int DEFAULT_SIZE;
    protected static String DEFAULT_SORT_BY;
    protected static String DEFAULT_ORDER;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String order;

    public abstract void initializeDefaultValues();

    public PageRequest toPageRequest() {

        Sort.Direction sortDirection = "ASC".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }

    public void fillEmptyFields() {

        initializeDefaultValues();

        if (page == null) {
            page = DEFAULT_PAGE;
        }

        if (size == null) {
            size = DEFAULT_SIZE;
        }

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = DEFAULT_SORT_BY;
        }

        if (order == null || order.isEmpty()) {
            order = DEFAULT_ORDER;
        }
    }
}
