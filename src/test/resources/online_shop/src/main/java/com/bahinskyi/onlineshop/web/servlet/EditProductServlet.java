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

public class EditProductServlet extends HttpServlet {
    private static final String EDIT_PRODUCT_TEMPLATE_HTML = "edit-product.html";
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

            pageGenerator.process(EDIT_PRODUCT_TEMPLATE_HTML, paramsMap, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException("EditProductServlet doGet error", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Product product = new Product();

            String uri = request.getRequestURI();
            String[] partsOfUri = uri.split("/");
            int id = Integer.parseInt(partsOfUri[partsOfUri.length - 1]);

            product.setId(id);
            product.setProductName(request.getParameter("productName"));
            product.setPrice(Double.parseDouble(request.getParameter("price")));
            product.setDescription(request.getParameter("description"));
            product.setRating(Integer.parseInt(request.getParameter("rating")));
            product.setPathToImage(request.getParameter("pathToImage"));

            defaultProductService.edit(product);
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (IOException e) {
            throw new RuntimeException("EditProductServlet doPost error", e);
        }
    }
}
