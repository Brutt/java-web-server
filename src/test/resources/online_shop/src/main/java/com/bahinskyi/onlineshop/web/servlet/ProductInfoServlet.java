package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.entity.Product;
import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.web.ServiceLocator;
import com.bahinskyi.onlineshop.web.templater.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductInfoServlet extends HttpServlet {
    private static final String PRODUCT_BY_ID_TEMPLATE_HTML = "product-by-id.html";
    private ProductService defaultProductService = ServiceLocator.getService(ProductService.class);
    private PageGenerator pageGenerator = PageGenerator.instance();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uri = request.getRequestURI();
            String[] partsOfUri = uri.split("/");
            int id = Integer.parseInt(partsOfUri[partsOfUri.length - 1]);

            Product product = defaultProductService.getById(id);

            Map<String, Object> paramsMap = new HashMap<>();

            User user = (User) request.getAttribute("user");
            if (user != null) {
                paramsMap.put("login", user.getLogin());
                paramsMap.put("userRole", user.getUserRole().getUserRoleName());
            }

            paramsMap.put("product", product);

            pageGenerator.process(request, response, PRODUCT_BY_ID_TEMPLATE_HTML, paramsMap);

        } catch (IOException e) {
            throw new RuntimeException("AllProductsServlet error", e);
        }
    }
}
