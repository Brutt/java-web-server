package com.bahinskyi.onlineshop.dao.jdbc;

import com.bahinskyi.onlineshop.dao.UserDao;
import com.bahinskyi.onlineshop.dao.jdbc.mapper.UserRowMapper;
import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.exception.LoginPasswordInvalidException;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUserDao implements UserDao {
    private static final String GET_USER_BY_LOGIN = "SELECT id, login, password, role, salt FROM users WHERE login=?";

    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private DataSource dataSource = ServiceLocator.getService(DataSource.class);


    @Override
    public User getUserByLogin(String login) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new LoginPasswordInvalidException("No user with login =  " + login + " found");
                }
                User user = USER_ROW_MAPPER.mapRow(resultSet);
                if (resultSet.next()) {
                    throw new RuntimeException("More then one user found");
                }
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get user by login error", e);
        }
    }
}
