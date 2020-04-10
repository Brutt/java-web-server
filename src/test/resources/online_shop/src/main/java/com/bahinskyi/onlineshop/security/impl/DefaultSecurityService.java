package com.bahinskyi.onlineshop.security.impl;

import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.exception.LoginPasswordInvalidException;
import com.bahinskyi.onlineshop.service.UserService;
import com.bahinskyi.onlineshop.security.SecurityService;
import com.bahinskyi.onlineshop.web.ServiceLocator;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DefaultSecurityService implements SecurityService {
    private UserService userService = ServiceLocator.getService(UserService.class);
    private List<Session> sessionList = new ArrayList<>();

    @Override
    public synchronized Session getUserSession(String token) {
        Iterator<Session> iterator = sessionList.iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (token.equals(session.getToken())) {
                if (session.getExpireDate().isAfter(LocalDateTime.now())) {
                    return session;
                } else {
                    iterator.remove();
                }
                break;
            }
        }
        return null;
    }

    @Override
    public synchronized Session login(String login, String password) {
        String hashedPassword = getHashedPassword(login, password);
        User user = userService.getByLogin(login);
        String actualPassword = user.getPassword();
        if (!hashedPassword.equals(actualPassword)) {
            throw new LoginPasswordInvalidException("Login/password invalid for user " + login);
        }
        Session session = new Session();
        session.setUser(user);
        String userToken = UUID.randomUUID().toString();
        session.setToken(userToken);

        session.setExpireDate(LocalDateTime.now().plusDays(1));

        sessionList.add(session);
        return session;

    }

    @Override
    public synchronized void logout(Session session) {
        if (session != null) {
            sessionList.remove(session);
        }
    }

    private synchronized String getHashedPassword(String login, String password) {
        User user = userService.getByLogin(login);
        String salt = user.getSalt();
        String saltPassword = password + salt;
        byte[] originalString = saltPassword.getBytes();
        return DigestUtils.sha256Hex(originalString);
    }
}
