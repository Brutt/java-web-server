package petrovskyi.webserver.application.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.http.HttpServlet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationRegistry {
    private static ApplicationRegistry applicationRegistry;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<String, ApplicationInfo> appNameToApplicationInfo = new ConcurrentHashMap<>();

    private ApplicationRegistry() {
    }

    public static ApplicationRegistry getInstance() {
        if (applicationRegistry == null) {
            applicationRegistry = new ApplicationRegistry();
        }
        return applicationRegistry;
    }

    public void register(String appName, ApplicationInfo applicationInfo) {
        LOG.info("Application {} was registered", appName);
        appNameToApplicationInfo.putIfAbsent(appName, applicationInfo);
    }

    public ApplicationInfo getApplication(String appName) {
        return appNameToApplicationInfo.get(appName);
    }

    public void destroyAllApplications() {
        LOG.info("Destroying applications");
        for (String appNameKey : appNameToApplicationInfo.keySet()) {
            ApplicationInfo applicationInfo = appNameToApplicationInfo.get(appNameKey);
            Map<String, HttpServlet> urlToServlet = applicationInfo.getUrlToServlet();
            for (String urlKey : urlToServlet.keySet()) {
                HttpServlet httpServlet = urlToServlet.get(urlKey);
                httpServlet.destroy();
                LOG.info("Servlet {} was destroyed", httpServlet);
            }
        }
    }
}
