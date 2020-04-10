package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.entity.UserRole;
import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.web.ServiceLocator;
import com.bahinskyi.onlineshop.web.templater.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AllProductsServlet extends HttpServlet {
    private static final String ALL_PRODUCTS_TEMPLATE_HTML = "all-products.html";
    private ProductService defaultProductService = ServiceLocator.getService(ProductService.class);
    private PageGenerator pageGenerator = PageGenerator.instance();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> paramsMap = new HashMap<>();

            User user = (User) request.getAttribute("user");
            if (user != null) {
                paramsMap.put("login", user.getLogin());
                paramsMap.put("userRole", user.getUserRole().getUserRoleName());
            } else {
                paramsMap.put("login", "GUEST");
                paramsMap.put("userRole", UserRole.GUEST.getUserRoleName());
            }

            paramsMap.put("products", defaultProductService.getAll());

            pageGenerator.process(ALL_PRODUCTS_TEMPLATE_HTML, paramsMap, response.getWriter());

        } catch (IOException e) {
            throw new RuntimeException("AllProductsServlet error", e);
        }
    }

}
