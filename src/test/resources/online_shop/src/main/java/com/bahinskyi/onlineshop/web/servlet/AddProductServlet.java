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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AddProductServlet extends HttpServlet {

    private static final String ADD_PRODUCT_TEMPLATE_HTML = "add-product.html";
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
            }

            pageGenerator.process(ADD_PRODUCT_TEMPLATE_HTML, paramsMap, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException("AddProductServlet doGet error", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Product product = new Product();

            product.setProductName(request.getParameter("productName"));
            product.setPrice(Double.parseDouble(request.getParameter("price")));
            product.setDescription(request.getParameter("description"));
            product.setRating(Integer.parseInt(request.getParameter("rating")));
            product.setPathToImage(request.getParameter("pathToImage"));
            product.setCreationDate(LocalDateTime.now());

            defaultProductService.add(product);

            response.sendRedirect(request.getContextPath() + "/products");
        } catch (IOException e) {
            throw new RuntimeException("AddProductServlet doPost error", e);
        }

    }
}
