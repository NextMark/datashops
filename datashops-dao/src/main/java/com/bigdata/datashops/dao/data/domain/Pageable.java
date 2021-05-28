package com.bigdata.datashops.dao.data.domain;

public interface Pageable extends org.springframework.data.domain.Pageable {

    String getFilters();

}
