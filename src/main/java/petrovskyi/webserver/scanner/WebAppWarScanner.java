package petrovskyi.webserver.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WebAppWarScanner implements Runnable {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String WAR_EXTENSION = ".war";

    public void scan() throws IOException, InterruptedException {
        LOG.info("Start scanning");
        List<String> wars = new ArrayList<>();

        WatchService watchService = FileSystems.getDefault().newWatchService();

        Path path = Paths.get("webapps");

        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {

                if (event.context().toString().endsWith(WAR_EXTENSION)) {
                    LOG.info("Catch war: " + event.context());
                    wars.add(event.context().toString());
                }
            }
            key.reset();
        }
    }

    @Override
    public void run() {
        try {
            scan();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
