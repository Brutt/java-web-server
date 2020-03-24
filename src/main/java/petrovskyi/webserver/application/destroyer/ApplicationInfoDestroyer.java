package petrovskyi.webserver.application.destroyer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.http.HttpServlet;
import java.util.Map;

public class ApplicationInfoDestroyer {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public void destroyAllApplications(Map<String, ApplicationInfo> appNameToApplicationInfo) {
        LOG.info("Destroying applications");
        for (String appNameKey : appNameToApplicationInfo.keySet()) {
            ApplicationInfo applicationInfo = appNameToApplicationInfo.get(appNameKey);
            destroyApplication(applicationInfo);
        }
    }

    public void destroyApplication(ApplicationInfo applicationInfo) {
        LOG.info("Destroying application {}", applicationInfo.getName());

        Map<String, HttpServlet> urlToServlet = applicationInfo.getUrlToServlet();
        for (String urlKey : urlToServlet.keySet()) {
            HttpServlet httpServlet = urlToServlet.get(urlKey);
            httpServlet.destroy();
            LOG.debug("Servlet {} was destroyed", httpServlet);
        }
    }
}
