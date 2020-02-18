package petrovskyi.webserver.webapp.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.unzip.WarUnzipper;

import java.io.IOException;
import java.nio.file.*;

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

                    if (event.context().toString().endsWith(WAR_EXTENSION)) {
                        LOG.info("Catch war: " + event.context());

                        warUnzipper.unzip(event.context().toString());
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
