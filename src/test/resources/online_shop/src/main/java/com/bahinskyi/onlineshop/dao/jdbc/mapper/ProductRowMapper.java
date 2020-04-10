package com.bahinskyi.onlineshop.dao.jdbc.mapper;

import com.bahinskyi.onlineshop.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper {
    public Product mapRow(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setProductName(resultSet.getString("productName"));
        product.setPrice(resultSet.getDouble("price"));
        product.setDescription(resultSet.getString("description"));
        product.setRating(resultSet.getInt("rating"));
        product.setPathToImage(resultSet.getString("pathToImage"));
        product.setCreationDate(resultSet.getTimestamp("creationDate").toLocalDateTime());

        return product;
    }
}
