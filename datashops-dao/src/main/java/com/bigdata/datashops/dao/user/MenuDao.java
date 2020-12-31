package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.Menu;

public interface MenuDao extends PagingAndSortingRepository<Menu, String> {
}
