package petrovskyi.webserver.application.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ApplicationInfoCreator {
    private static final String CLASSES_FOLDER_PATH = "/WEB-INF/classes";
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();

    public void create(String appDir, Map<String, String> urlToClassName) {
        String appName = appDir.substring(appDir.lastIndexOf("/") + 1);
        LOG.info("Start to create new application with name {}", appName);
        ClassLoader classLoader = getClassLoader(appDir);

        Map<String, HttpServlet> urlToServlet = transform(urlToClassName, classLoader);

        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setUrlToServlet(urlToServlet);

        LOG.info("Application {} was successfully created", appName);

        applicationRegistry.register(appName, applicationInfo);
    }

    Map<String, HttpServlet> transform(Map<String, String> urlToClassName, ClassLoader classLoader) {
        LOG.info("Start to transform urlToClassName map into urlToServlet");
        Map<String, HttpServlet> servletMap = new HashMap<>();

        for (String url : urlToClassName.keySet()) {
            try {
                Class<?> aClass = classLoader.loadClass(urlToClassName.get(url));
                HttpServlet httpServlet = (HttpServlet) aClass.getDeclaredConstructor().newInstance();

                servletMap.put(url, httpServlet);
                LOG.info("Servlet for class {} was successfully instantiated", aClass);
            } catch (Exception e) {
                LOG.error("Error while trying to get servlet", e);
                throw new RuntimeException("Error while trying to get servlet", e);
            }
        }

        return servletMap;
    }

    ClassLoader getClassLoader(String appDir) {
        LOG.info("Start to get class loader in folder {}", appDir);
        File file = new File(appDir + CLASSES_FOLDER_PATH);

        URL url;
        try {
            url = file.toURI().toURL();
        } catch (Exception e) {
            LOG.error("Error while trying to transform file {} into URL", file, e);
            throw new RuntimeException("Error while trying to transform file " + file + " into URL", e);
        }

        URL[] urls = new URL[]{url};

        return new URLClassLoader(urls);
    }
}
