package petrovskyi.webserver.web.servlet.config;

import petrovskyi.webserver.web.servlet.config.adapter.ServletConfigAdapter;
import petrovskyi.webserver.web.servlet.context.WebServletContext;

import javax.servlet.ServletContext;

public class WebServletConfig extends ServletConfigAdapter {
    private WebServletContext webServletContext;

    public WebServletConfig(WebServletContext webServletContext) {
        this.webServletContext = webServletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return webServletContext;
    }
}
