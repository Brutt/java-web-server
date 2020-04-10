package com.bahinskyi.onlineshop.service.impl;

import com.bahinskyi.onlineshop.dao.UserDao;
import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.service.UserService;
import com.bahinskyi.onlineshop.web.ServiceLocator;

public class DefaultUserService implements UserService {
    private UserDao userDao = ServiceLocator.getService(UserDao.class);

    @Override
    public User getByLogin(String login) {
        return userDao.getUserByLogin(login);
    }
}
