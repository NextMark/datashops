package com.bigdata.datashops.dao.data.domain;

import org.springframework.data.domain.Sort;

public class PageRequest extends org.springframework.data.domain.PageRequest implements Pageable {

    private final String filters;

    public PageRequest(int page, int size, String filters) {
        this(page, size, filters, null);
    }

    public PageRequest(int page, int size, String filters, Sort.Direction direction, String... properties) {
        this(page, size, filters, Sort.by(direction, properties));
    }

    public PageRequest(int page, int size, String filters, Sort sort) {
        super(page, size, sort);
        this.filters = filters;
    }

    @Override
    public String getFilters() {
        return filters;
    }

}
