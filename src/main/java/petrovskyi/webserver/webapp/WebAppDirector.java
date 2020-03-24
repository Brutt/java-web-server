package petrovskyi.webserver.webapp;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.creator.ApplicationInfoCreator;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.webapp.entity.StartupArchiveAndFolder;
import petrovskyi.webserver.webapp.entity.WebXmlDefinition;
import petrovskyi.webserver.webapp.scanner.WarScanner;
import petrovskyi.webserver.webapp.unzip.WarUnzipper;
import petrovskyi.webserver.webapp.webxml.WebXmlHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WebAppDirector {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    public static final String WEBAPPS_DIR_NAME = "webapps";
    public static final String WAR_EXTENSION = ".war";
    private ApplicationInfoCreator applicationInfoCreator = new ApplicationInfoCreator();
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private WebXmlHandler webXmlHandler = new WebXmlHandler();
    private WarUnzipper warUnzipper = new WarUnzipper();

    public void manage() {
        LOG.info("Starting to manage webapps");

        WarScanner warScanner = new WarScanner(this::unzipAndRegisterNewApplication);

        warScanner.scan();
    }

    public void manageAtStartup() {
        LOG.info("Start managing webapps at startup");

        WarScanner warScanner = new WarScanner();

        StartupArchiveAndFolder startupArchiveAndFolder = warScanner.scanAtStartUp();
        guideAtStartup(startupArchiveAndFolder);
    }

    private void unzipAndRegisterNewApplication(String warName) {
        LOG.debug("Start to unzip and register new application based on war archive {}", warName);

        String unzipDir = warUnzipper.unzip(warName);

        registerNewApplication(unzipDir);
    }

    private void registerNewApplication(String unzipDir) {
        LOG.debug("Start to create and register new application based on unzipped directory {}", unzipDir);

        WebXmlDefinition webXmlDefinition = webXmlHandler.handle(unzipDir);
        ApplicationInfo applicationInfo = applicationInfoCreator.create(unzipDir, webXmlDefinition);

        applicationRegistry.register(applicationInfo);
    }

    private void guideAtStartup(StartupArchiveAndFolder startupArchiveAndFolder) {
        LOG.debug("Start to guide scanned data at startup");

        List<String> archivesTemp = new ArrayList<>(startupArchiveAndFolder.getArchives()).stream()
                .map(x -> x.replace(WebAppDirector.WAR_EXTENSION, ""))
                .collect(Collectors.toList());

        Collection<String> needProcess = CollectionUtils.intersection(startupArchiveAndFolder.getFolders(), archivesTemp);
        for (String appName : needProcess) {
            LOG.info("App {} need to be processed", appName);
            registerNewApplication(WebAppDirector.WEBAPPS_DIR_NAME + "/" + appName);
        }

        Collection<String> needUnzip = CollectionUtils.removeAll(archivesTemp, startupArchiveAndFolder.getFolders());
        for (String warName : needUnzip) {
            LOG.info("War {} need to be unzipped", warName);
            unzipAndRegisterNewApplication(warName + WebAppDirector.WAR_EXTENSION);
        }

        Collection<String> needRemove = CollectionUtils.removeAll(startupArchiveAndFolder.getFolders(), archivesTemp);
        for (String folderName : needRemove) {
            try {
                FileUtils.deleteDirectory(new File(WebAppDirector.WEBAPPS_DIR_NAME + "/" + folderName));
                LOG.info("Folder {} was deleted", folderName);
            } catch (IOException e) {
                LOG.error("Error while deleting folder {}", folderName, e);
            }
        }
    }

}

