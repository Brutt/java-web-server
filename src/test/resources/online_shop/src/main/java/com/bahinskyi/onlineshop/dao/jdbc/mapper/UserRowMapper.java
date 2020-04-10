package com.bahinskyi.onlineshop.dao.jdbc.mapper;

import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.entity.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {
    public User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("password"));
        user.setUserRole(UserRole.getUserRole(resultSet.getString("role")));
        user.setSalt(resultSet.getString("salt"));

        return user;
    }

}
