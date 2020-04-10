package com.bahinskyi.onlineshop.dao.jdbc;

import com.bahinskyi.onlineshop.dao.ProductDao;
import com.bahinskyi.onlineshop.dao.jdbc.mapper.ProductRowMapper;
import com.bahinskyi.onlineshop.entity.Product;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {

    private static final String GET_ALL_SQL = "SELECT id, productName, price, description, rating, pathToImage, creationDate FROM products ORDER BY id";
    private static final String INSERT_SQL = "INSERT INTO products (productName, price, description, rating, pathToImage, creationDate) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE products SET productName=?, price=?, description=?, rating=?, pathToImage=? WHERE id=?";
    private static final String GET_BY_ID_SQL = "SELECT id, productName, price, description, rating, pathToImage, creationDate FROM products WHERE id=?";
    private static final String DELETE_SQL = "DELETE FROM products WHERE id=?";

    private static final ProductRowMapper PRODUCT_ROW_MAPPER = new ProductRowMapper();
    private DataSource dataSource = ServiceLocator.getService(DataSource.class);

    public List<Product> getAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                Product product = PRODUCT_ROW_MAPPER.mapRow(resultSet);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error during get all products", e);
        }
    }

    public void addProduct(Product product) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setInt(4, product.getRating());
            preparedStatement.setString(5, product.getPathToImage());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(product.getCreationDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert user error", e);
        }
    }

    public void editProduct(Product product) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setInt(4, product.getRating());
            preparedStatement.setString(5, product.getPathToImage());
            preparedStatement.setInt(6, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update user error", e);
        }
    }

    public Product getProductById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RuntimeException("No product with id " + id + " found");
                }
                Product product = PRODUCT_ROW_MAPPER.mapRow(resultSet);
                if (resultSet.next()) {
                    throw new RuntimeException("More then one product found");
                }
                return product;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get product by id error", e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete product error", e);
        }
    }
}
