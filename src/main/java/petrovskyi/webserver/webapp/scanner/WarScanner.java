package petrovskyi.webserver.webapp.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.webapp.entity.StartupArchiveAndFolder;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarScanner {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Consumer<String> nextStepConsumer;

    public WarScanner(Consumer<String> nextStepConsumer) {
        this.nextStepConsumer = nextStepConsumer;
    }

    /* constructor for scanAtStartUp run */
    public WarScanner() {
    }

    public void scan() {
        LOG.info("Start scanning");

        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(WebAppDirector.WEBAPPS_DIR_NAME);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            LOG.error("Error while registering watch service", e);
            throw new RuntimeException("Error while registering watch service", e);
        }

        WatchKey key;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {

                    String warName = event.context().toString();
                    if (warName.endsWith(WebAppDirector.WAR_EXTENSION)) {
                        LOG.info("Catch war: " + event.context());

                        nextStepConsumer.accept(warName);
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                LOG.info("Scan was interrupted");

                try {
                    watchService.close();
                } catch (IOException ioe) {
                    LOG.error("Error while closing watch service", ioe);
                    throw new RuntimeException("Error while closing watch service", ioe);
                }

                break;
            }
        }

        LOG.info("Scan over");
    }

    public StartupArchiveAndFolder scanAtStartUp() {
        LOG.info("Start scanning at startup");

        LOG.info("Looking for wars");
        List<String> archives;
        try (Stream<Path> walk = Files.walk(Paths.get(WebAppDirector.WEBAPPS_DIR_NAME))) {
            archives = walk.map(x -> x.toString())
                    .filter(x -> x.endsWith(WebAppDirector.WAR_EXTENSION))
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

        return new StartupArchiveAndFolder(archives, folders);
    }

}
