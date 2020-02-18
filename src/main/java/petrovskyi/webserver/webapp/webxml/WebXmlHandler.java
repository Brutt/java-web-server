package petrovskyi.webserver.webapp.webxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebXmlHandler {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public void parse(String dir){
        LOG.info("Starting to parse dir {} to find web.xml", dir);

        try (Stream<Path> walk = Files.walk(Paths.get(dir))) {
            List<String> result = walk.map(x -> x.toString())
                    .filter(x -> x.endsWith("web.xml"))
                    .collect(Collectors.toList());

            if (result.size() == 1) {
                LOG.info("Found web.xml in {}", result.get(0));
            }else{
                LOG.info("Could not find web.xml in {}", dir);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
