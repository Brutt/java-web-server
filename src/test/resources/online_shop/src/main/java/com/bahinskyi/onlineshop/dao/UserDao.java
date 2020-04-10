package com.bahinskyi.onlineshop.dao;

import com.bahinskyi.onlineshop.entity.User;

public interface UserDao {

    User getUserByLogin(String login);
}
