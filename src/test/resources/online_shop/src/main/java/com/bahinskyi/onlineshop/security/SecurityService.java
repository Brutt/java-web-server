package com.bahinskyi.onlineshop.security;

import com.bahinskyi.onlineshop.security.entity.Session;

public interface SecurityService {

    Session getUserSession(String token);

    Session login(String login, String password);

    void logout(Session session);
}
