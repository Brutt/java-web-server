package petrovskyi.webserver.web.filter.config;

import petrovskyi.webserver.web.filter.config.adapter.FilterConfigAdapter;
import petrovskyi.webserver.web.servlet.context.WebServletContext;

import javax.servlet.ServletContext;

public class WebServerFilterConfig extends FilterConfigAdapter {
    private WebServletContext webServletContext;

    public WebServerFilterConfig(WebServletContext webServletContext) {
        this.webServletContext = webServletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return webServletContext;
    }
}
