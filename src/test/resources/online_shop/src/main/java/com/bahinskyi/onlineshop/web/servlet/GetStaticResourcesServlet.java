package com.bahinskyi.onlineshop.web.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetStaticResourcesServlet extends HttpServlet {

    private static final int BUFFER_SIZE = 8192;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String stylePath = request.getRequestURI().replace(request.getContextPath(), "").substring(1);
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(stylePath);

        if (resourceAsStream == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (BufferedInputStream styleStream = new BufferedInputStream(resourceAsStream)) {

            ServletOutputStream outputStream = response.getOutputStream();

            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = styleStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading static resources error", e);
        }
    }
}
