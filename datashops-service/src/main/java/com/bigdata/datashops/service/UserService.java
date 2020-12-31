package com.bigdata.datashops.service;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.User;

@Service
public class UserService extends AbstractMysqlPagingAndSortingQueryService<User, String> {
    @Resource
    private PasswordEncoder passwordEncoder;

    public User getUserInfo(Integer uid) {
        return findById(String.valueOf(uid));
    }

    public User getUser(String filter) {
        return findOneByQuery(filter);
    }

    public void register(User user) {
        if (StringUtils.isNoneBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }
        save(user);
    }

    public boolean verifyPassword(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
