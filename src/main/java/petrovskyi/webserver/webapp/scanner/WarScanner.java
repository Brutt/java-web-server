package petrovskyi.webserver.webapp.scanner;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.webapp.unzip.WarUnzipper;
import petrovskyi.webserver.webapp.webxml.WebXmlHandler;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarScanner {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String WAR_EXTENSION = ".war";
    private WarUnzipper warUnzipper;

    public WarScanner(WarUnzipper warUnzipper) {
        this.warUnzipper = warUnzipper;
    }

    public void scan() {
        LOG.info("Start scanning");

        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get("webapps");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    String warName = event.context().toString();
                    if (warName.endsWith(WAR_EXTENSION)) {
                        LOG.info("Catch war: " + event.context());

                        warUnzipper.unzip(warName);
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void scanAtStartUp(WebXmlHandler webXmlHandler) {
        LOG.info("Start scanning at startup");

        LOG.info("Looking for wars");
        List<String> archives;
        try (Stream<Path> walk = Files.walk(Paths.get(WebAppDirector.WEBAPPS_DIR_NAME))) {
            archives = walk.map(x -> x.toString())
                    .filter(x -> x.endsWith(WAR_EXTENSION))
                    .map(x -> x.replace(WebAppDirector.WEBAPPS_DIR_NAME + "/", ""))
                    .collect(Collectors.toList());

            if (archives.size() > 0) {
                for (String archiveName : archives) {
                    LOG.info("Found war {}", archiveName);
                }
            } else {
                LOG.info("No wars were found at startup");
            }
        } catch (IOException e) {
            LOG.error("Error while searching for wars at startup", e);
            throw new RuntimeException("Error while searching for wars at startup", e);
        }

        LOG.info("Looking for app folders");
        List<String> folders;
        try (Stream<Path> walk = Files.walk(Paths.get(WebAppDirector.WEBAPPS_DIR_NAME), 1)) {
            folders = walk.filter(x -> Files.isDirectory(x))
                    .map(x -> x.toString())
                    .filter(x -> !x.equals(WebAppDirector.WEBAPPS_DIR_NAME))
                    .map(x -> x.replace(WebAppDirector.WEBAPPS_DIR_NAME + "/", ""))
                    .collect(Collectors.toList());

            if (folders.size() > 0) {
                for (String appFolderName : folders) {
                    LOG.info("Found app folder {}", appFolderName);
                }
            } else {
                LOG.info("No app folders were found at startup");
            }
        } catch (IOException e) {
            LOG.error("Error while searching for app folders at startup", e);
            throw new RuntimeException("Error while searching for app folders at startup", e);
        }

        forward(webXmlHandler, folders, archives);
    }

    private void forward(WebXmlHandler webXmlHandler, List<String> folders, List<String> archives) {
        LOG.info("Start to forward scanned data at startup");

        List<String> archivesTemp = new ArrayList<>(archives).stream()
                .map(x -> x.replace(WAR_EXTENSION, ""))
                .collect(Collectors.toList());

        Collection<String> needProcess = CollectionUtils.intersection(folders, archivesTemp);
        for (String appName : needProcess) {
            LOG.info("App {} need to be processed", appName);
            webXmlHandler.handle(WebAppDirector.WEBAPPS_DIR_NAME + "/" + appName);
        }

        Collection<String> needUnzip = CollectionUtils.removeAll(archivesTemp, folders);
        for (String warName : needUnzip) {
            LOG.info("War {} need to be unzipped", warName);
            warUnzipper.unzip(warName + WAR_EXTENSION);
        }

//        Collection<String> needRemove = CollectionUtils.removeAll(folders, archivesTemp);
//        System.out.println("need to be removed: " + needRemove);
    }

}
