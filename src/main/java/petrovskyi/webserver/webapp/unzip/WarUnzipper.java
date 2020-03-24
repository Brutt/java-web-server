package petrovskyi.webserver.webapp.unzip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static petrovskyi.webserver.webapp.WebAppDirector.*;

public class WarUnzipper {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public String unzip(String warName) {
        File zipFilePath = new File(WEBAPPS_DIR_NAME, warName);
        File unzipDir = new File(WEBAPPS_DIR_NAME, warName.replace(WAR_EXTENSION, ""));

        LOG.info("Try to unzip file: " + zipFilePath);

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                File destPath = new File(unzipDir, entry.getName());

                if (!isValidDestinationPath(unzipDir.getPath(), destPath.getPath())) {
                    throw new IOException("Final " + (entry.isDirectory() ? "directory" : "file") + " output path is invalid: " + destPath);
                }

                LOG.debug("{} => {}", (entry.isDirectory() ? "directory: " : "file: ") + entry.getName(), destPath);

                if (entry.isDirectory()) {
                    destPath.mkdirs();
                } else {
                    Files.copy(zipFile.getInputStream(entry), destPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            LOG.error("Cannot unzip war file {}", zipFilePath, e);
            throw new RuntimeException("Error unzipping file " + zipFilePath, e);
        }

        LOG.info("File: " + zipFilePath + " was unzipped");

        return unzipDir.getPath();
    }

    // check Zip Slip attack
    private boolean isValidDestinationPath(String targetDir, String destPathStr) {
        Path destPath = Paths.get(destPathStr);
        Path destPathNormalized = destPath.normalize();

        return destPathNormalized.startsWith(targetDir + File.separator);
    }
}    
