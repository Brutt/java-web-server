package petrovskyi.webserver.web.servlet.context;

import petrovskyi.webserver.web.servlet.context.adapter.ServletContextAdapter;

public class WebServletContext extends ServletContextAdapter {
    private ClassLoader classLoader;

    public WebServletContext(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

}
