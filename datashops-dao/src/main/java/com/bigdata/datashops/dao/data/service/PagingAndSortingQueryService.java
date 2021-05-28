package com.bigdata.datashops.dao.data.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.bigdata.datashops.dao.data.domain.Pageable;

public interface PagingAndSortingQueryService<T, ID extends Serializable> extends PagingAndSortingService<T, ID> {

    T findOneByQuery(String filters);

    List<T> findByQuery(String filters);

    List<T> findByQuery(String filters, Sort sort);

    Page<T> pageByQuery(Pageable pageable);

    void deleteByQuery(String filters);

}
