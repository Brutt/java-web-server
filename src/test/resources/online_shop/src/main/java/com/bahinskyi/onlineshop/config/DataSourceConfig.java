package com.bahinskyi.onlineshop.config;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class DataSourceConfig {
    private static final String JDBC_URL = "jdbc.url";

    public DataSource getDataSource() {
        PropertiesFactory propertiesFactory = new PropertiesFactory("application.properties");

        Properties applicationProperties = propertiesFactory.getProperties();

        String url = applicationProperties.getProperty(JDBC_URL);

        MysqlConnectionPoolDataSource mysqlConnectionPoolDataSource = new MysqlConnectionPoolDataSource();

        mysqlConnectionPoolDataSource.setUrl(url);

        return mysqlConnectionPoolDataSource;
    }
}
