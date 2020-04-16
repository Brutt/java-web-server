package com.bahinskyi.onlineshop.web;

import com.bahinskyi.onlineshop.config.DataSourceConfig;
import com.bahinskyi.onlineshop.dao.ProductDao;
import com.bahinskyi.onlineshop.dao.UserDao;
import com.bahinskyi.onlineshop.dao.jdbc.JdbcProductDao;
import com.bahinskyi.onlineshop.dao.jdbc.JdbcUserDao;
import com.bahinskyi.onlineshop.security.SecurityService;
import com.bahinskyi.onlineshop.security.impl.DefaultSecurityService;
import com.bahinskyi.onlineshop.service.CartItemService;
import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.service.UserService;
import com.bahinskyi.onlineshop.service.impl.DefaultCartItemService;
import com.bahinskyi.onlineshop.service.impl.DefaultProductService;
import com.bahinskyi.onlineshop.service.impl.DefaultUserService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class ServiceLocator {

    private static final Map<Class<?>, Object> SERVICES = new HashMap<>();

    static {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DataSource dataSource = dataSourceConfig.getDataSource();
        register(DataSource.class, dataSource);

        ProductDao productDao = new JdbcProductDao();
        register(ProductDao.class, productDao);

        UserDao userDao = new JdbcUserDao();
        register(UserDao.class, userDao);

        ProductService defaultProductService = new DefaultProductService();
        register(ProductService.class, defaultProductService);

        UserService userService = new DefaultUserService();
        register(UserService.class, userService);

        SecurityService securityService = new DefaultSecurityService();
        register(SecurityService.class, securityService);

        CartItemService cartItemService = new DefaultCartItemService();
        register(CartItemService.class, cartItemService);
    }

    public static void register(Class<?> serviceClass, Object service) {
        SERVICES.put(serviceClass, service);
    }

    public static <T> T getService(Class<T> serviceClass) {
        T service = serviceClass.cast(SERVICES.get(serviceClass));
        return service;
    }
}
