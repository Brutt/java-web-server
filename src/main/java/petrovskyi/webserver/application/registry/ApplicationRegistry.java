package petrovskyi.webserver.application.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationRegistry {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<String, ApplicationInfo> appNameToApplicationInfo = new ConcurrentHashMap<>();

    public void register(ApplicationInfo applicationInfo) {
        LOG.info("Application {} was registered", applicationInfo.getName());
        appNameToApplicationInfo.put(applicationInfo.getName(), applicationInfo);
    }

    public ApplicationInfo remove(String appName) {
        LOG.info("Try to remove application {}", appName);

        ApplicationInfo removed = appNameToApplicationInfo.remove(appName);

        if (removed != null) {
            LOG.info("Application {} was removed", removed.getName());
            destroyServletsAndFilters(removed);
        } else {
            LOG.info("Cannot find an application {} to remove", appName);
        }

        return removed;
    }

    public ApplicationInfo getApplication(String appName) {
        return appName == null ? null : appNameToApplicationInfo.get(appName);
    }

    public void cleanAll() {
        Set<String> keys = appNameToApplicationInfo.keySet();
        for (String key : keys) {
            remove(key);
        }
    }

    private void destroyServletsAndFilters(ApplicationInfo applicationInfo) {
        LOG.info("Destroying application`s servlets and filters {}", applicationInfo.getName());

        Map<String, HttpServlet> urlToServlet = applicationInfo.getUrlToServlet();
        for (String urlKey : urlToServlet.keySet()) {
            HttpServlet httpServlet = urlToServlet.get(urlKey);
            httpServlet.destroy();
            LOG.debug("Servlet {} was destroyed", httpServlet);
        }

        Map<String, List<Filter>> urlToFilter = applicationInfo.getUrlToFilters();
        for (String urlKey : urlToFilter.keySet()) {
            for (Filter filter : urlToFilter.get(urlKey)) {
                filter.destroy();
                LOG.debug("Filter {} was destroyed", filter);
            }
        }
    }
}
