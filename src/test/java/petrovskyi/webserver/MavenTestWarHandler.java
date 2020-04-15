package petrovskyi.webserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class MavenTestWarHandler {
    //private String appName = "simple";
    //private String appName = "simple_post_and_filters";
    private String appName = "online_shop";
    //private String appName = "simple_post_multipart";

    private void createTestWar() throws MavenInvocationException {
        log.info("Start to create test war");
        Path pomXmlPath = Paths.get("src", "test", "resources", appName, "pom.xml");

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomXmlPath.toFile());
        request.setGoals(Arrays.asList("clean", "install"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
        log.info("Test war was created");
    }

    private void copyTestWarToWebapps() {
        log.info("Start to copy test war file");
        Path targetPath = Paths.get("src", "test", "resources", appName, "target");

        File warFile;
        try (Stream<Path> walk = Files.walk(targetPath, 1)) {
            Optional<File> warFileOptional = walk.map(Path::toFile)
                    .filter(x -> x.getName().startsWith(appName))
                    .filter(x -> x.getName().endsWith(".war"))
                    .findAny();

            warFile = warFileOptional.orElseThrow(() -> new RuntimeException("Cannot find test war file"));
        } catch (IOException e) {
            throw new RuntimeException("Error while walking through " + targetPath + " to find test war file", e);
        }

        try {
            File webappsFolder = new File("webapps");
            if (!webappsFolder.exists()) {
                webappsFolder.mkdir();
            }
            Files.copy(warFile.toPath(), Paths.get("webapps", appName + ".war"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error copying test war file to webapps", e);
        }
        log.info("Test war file was copied to webapps folder");
    }

    // uncomment to build test app
    @Test
    public void startServerWithTestWar() throws MavenInvocationException, IOException {
        createTestWar();
        copyTestWarToWebapps();
        FileUtils.deleteDirectory(new File("webapps", appName));

        log.info("Start the main method to start the server");
        Starter.main(new String[0]);
    }

}
