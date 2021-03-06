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
import petrovskyi.webserver.webapp.webxml.exception.WebXmlNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
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
    private WebXmlHandler webXmlHandler = new WebXmlHandler();
    private WarUnzipper warUnzipper = new WarUnzipper();

    private ApplicationRegistry applicationRegistry;

    public WebAppDirector(ApplicationRegistry applicationRegistry) {
        this.applicationRegistry = applicationRegistry;
    }

    public void manage() {
        LOG.info("Starting to manage webapps");

        WarScanner warScanner = new WarScanner(this::preRunFlow);

        warScanner.scan();
    }

    public void manageAtStartup() {
        LOG.info("Start managing webapps at startup");

        WarScanner warScanner = new WarScanner();

        StartupArchiveAndFolder startupArchiveAndFolder = warScanner.scanAtStartUp();
        guideAtStartup(startupArchiveAndFolder);
    }

    private void preRunFlow(String warName, boolean needDelete) {
        if (needDelete) {
            LOG.debug("Deleting war {} and removing an application based on this war", warName);

            runDeleteFlow(warName);
        } else {
            LOG.debug("Start to unzip and register new application based on war archive {}", warName);

            File unzipDir = warUnzipper.unzip(warName);

            runMainFlow(unzipDir);
        }
    }

    private void runMainFlow(File unzipDir) {
        LOG.debug("Start to create and register new application based on unzipped directory {}", unzipDir);

        WebXmlDefinition webXmlDefinition = getWebXmlDefinition(unzipDir);

        ApplicationInfo applicationInfo = applicationInfoCreator.create(unzipDir.getPath(), webXmlDefinition);

        applicationRegistry.register(applicationInfo);
    }

    private void runDeleteFlow(String dirName) {
        LOG.debug("Removing everything related to {}", dirName);

        String appName;
        boolean isWar = true;
        if (dirName.endsWith(WAR_EXTENSION)) {
            appName = dirName.replace(WAR_EXTENSION, "");
        } else {
            isWar = false;
            appName = dirName + WAR_EXTENSION;
        }

        applicationRegistry.remove(isWar ? appName : dirName);

        try {
            File file = new File(WEBAPPS_DIR_NAME, appName);
            if (isWar) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
            }

            LOG.debug("{} {} was deleted and all related to it", isWar ? "War-archive" : "Folder", dirName);
        } catch (IOException e) {
            LOG.error("Error while deleting {}", dirName, e);
        }
    }

    private WebXmlDefinition getWebXmlDefinition(File unzipDir) {
        try {
            return webXmlHandler.handle(unzipDir);
        } catch (FileNotFoundException e) {
            LOG.error("Cannot find web.xml in {}", unzipDir, e);
            throw new WebXmlNotFoundException("Cannot find web.xml in " + unzipDir, e);
        }
    }

    private void guideAtStartup(StartupArchiveAndFolder startupArchiveAndFolder) {
        LOG.debug("Start to guide scanned data at startup");

        List<String> archivesWOExtension = new ArrayList<>(startupArchiveAndFolder.getArchives()).stream()
                .map(x -> x.replace(WAR_EXTENSION, ""))
                .collect(Collectors.toList());

        Collection<String> needProcess = CollectionUtils.intersection(startupArchiveAndFolder.getFolders(), archivesWOExtension);
        for (String appName : needProcess) {
            LOG.debug("App {} need to be processed", appName);
            runMainFlow(new File(appName));
        }

        Collection<String> needUnzip = CollectionUtils.removeAll(archivesWOExtension, startupArchiveAndFolder.getFolders());
        for (String warName : needUnzip) {
            String trueWarName = warName.replace(WEBAPPS_DIR_NAME + File.separator, "") + WAR_EXTENSION;
            LOG.debug("War {} need to be unzipped", trueWarName);
            preRunFlow(trueWarName, false);
        }

        Collection<String> needRemove = CollectionUtils.removeAll(startupArchiveAndFolder.getFolders(), archivesWOExtension);
        for (String folderName : needRemove) {
            try {
                FileUtils.deleteDirectory(new File(folderName));
                LOG.debug("Folder {} was deleted", folderName);
            } catch (IOException e) {
                LOG.error("Error while deleting folder {}", folderName, e);
            }
        }
    }

}

