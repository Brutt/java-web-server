package petrovskyi.webserver.webapp.unzip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static petrovskyi.webserver.webapp.WebAppDirector.WAR_EXTENSION;
import static petrovskyi.webserver.webapp.WebAppDirector.WEBAPPS_DIR_NAME;

public class WarUnzipper {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public File unzip(String warName) {
        File zipFilePath = new File(WEBAPPS_DIR_NAME, warName);
        File unzipDir = new File(WEBAPPS_DIR_NAME, warName.replace(WAR_EXTENSION, ""));

        LOG.info("Try to unzip file: " + zipFilePath);

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                File destPath = new File(unzipDir, entry.getName());

                if (!isValidDestinationPath(unzipDir.getPath(), destPath.getPath())) {
                    throw new IOException("Final " + (entry.isDirectory() ? "directory" : "file") + " output path is invalid: " + destPath);
                }

                LOG.debug("{} => {}", (entry.isDirectory() ? "directory: " : "file: ") + entry.getName(), destPath);

                if (entry.isDirectory()) {
                    destPath.mkdirs();
                } else {
                    Files.copy(zipIn, destPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            LOG.error("Cannot unzip war file {}", zipFilePath, e);
            throw new RuntimeException("Error unzipping file " + zipFilePath, e);
        }

        LOG.info("File: " + zipFilePath + " was unzipped");

        return unzipDir;
    }

    // check Zip Slip attack
    private boolean isValidDestinationPath(String targetDir, String destPathStr) {
        Path destPath = Paths.get(destPathStr);
        Path destPathNormalized = destPath.normalize();

        return destPathNormalized.startsWith(targetDir + File.separator);
    }
}    
