package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.User;

public interface UserDao extends PagingAndSortingRepository<User, String> {
}
