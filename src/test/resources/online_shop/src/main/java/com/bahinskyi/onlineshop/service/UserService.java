package com.bahinskyi.onlineshop.service;

import com.bahinskyi.onlineshop.entity.User;

public interface UserService {
    User getByLogin(String login);
}
