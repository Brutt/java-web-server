package com.bahinskyi.onlineshop.web.templater;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class PageGenerator {
    private static PageGenerator pageGenerator;
    private TemplateEngine templateEngine;

    public static PageGenerator instance() {
        if (pageGenerator == null) {
            pageGenerator = new PageGenerator();
        }
        return pageGenerator;
    }

    public void process(HttpServletRequest request, HttpServletResponse response, String template, Map<String, Object> paramsMap) throws IOException {
        IContext context = new WebContext(request, response, request.getServletContext(), Locale.getDefault(), paramsMap);
        templateEngine.process(template, context, response.getWriter());
    }

    public PageGenerator() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("template/");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
    }
}
