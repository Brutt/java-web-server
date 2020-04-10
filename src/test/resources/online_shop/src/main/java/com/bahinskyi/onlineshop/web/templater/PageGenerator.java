package com.bahinskyi.onlineshop.web.templater;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
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

    public void process(String template, Map<String, Object> paramsMap, Writer writer) {
        Context context = new Context();
        context.setVariables(paramsMap);

        templateEngine.process(template, context, writer);
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
