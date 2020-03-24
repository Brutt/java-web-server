package petrovskyi.webserver.application.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.destroyer.ApplicationInfoDestroyer;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationRegistry {
    private static ApplicationRegistry applicationRegistry;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<String, ApplicationInfo> appNameToApplicationInfo = new ConcurrentHashMap<>();
    private ApplicationInfoDestroyer applicationInfoDestroyer = new ApplicationInfoDestroyer();

    private ApplicationRegistry() {
    }

    public static ApplicationRegistry getInstance() {
        if (applicationRegistry == null) {
            applicationRegistry = new ApplicationRegistry();
        }
        return applicationRegistry;
    }

    public void register(ApplicationInfo applicationInfo) {
        LOG.info("Application {} was registered", applicationInfo.getName());
        appNameToApplicationInfo.put(applicationInfo.getName(), applicationInfo);
    }

    public ApplicationInfo remove(String appName) {
        LOG.info("Try to remove application {}", appName);

        ApplicationInfo removed = appNameToApplicationInfo.remove(appName);

        if (removed != null) {
            LOG.info("Application {} was removed", removed.getName());
            applicationInfoDestroyer.destroyApplication(removed);
        } else {
            LOG.info("Cannot find an application {} to remove", appName);
        }

        return removed;
    }

    public ApplicationInfo getApplication(String appName) {
        return appNameToApplicationInfo.get(appName);
    }

    public void cleanAll(){
        applicationInfoDestroyer.destroyAllApplications(appNameToApplicationInfo);
    }
}
