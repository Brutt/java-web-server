package petrovskyi.webserver.webapp.unzip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.webapp.webxml.WebXmlHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WarUnzipper {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private WebXmlHandler webXmlHandler;

    public WarUnzipper(WebXmlHandler webXmlHandler) {
        this.webXmlHandler = webXmlHandler;
    }

    public void unzip(String warName) {
        String zipFileDir = WebAppDirector.WEBAPPS_DIR_NAME;
        String zipFilePath = zipFileDir + File.separator + warName;
        String unzipDir = zipFileDir + File.separator + warName.replace(WebAppDirector.WAR_EXTENSION, "");

        LOG.info("Try to unzip file: " + zipFilePath);

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                String destPath = unzipDir + File.separator + entry.getName();

                if (!isValidDestinationPath(unzipDir, destPath)) {
                    throw new IOException("Final " + (entry.isDirectory() ? "directory" : "file") + " output path is invalid: " + destPath);
                }

                LOG.debug("{} => {}", (entry.isDirectory() ? "directory: " : "file: ") + entry.getName(), destPath);

                if (entry.isDirectory()) {
                    File file = new File(destPath);
                    file.mkdirs();

                } else {
                    try (InputStream inputStream = zipFile.getInputStream(entry);
                         FileOutputStream outputStream = new FileOutputStream(destPath);) {
                        int data = inputStream.read();
                        while (data != -1) {
                            outputStream.write(data);
                            data = inputStream.read();
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Cannot unzip war file {}", zipFilePath, e);
            throw new RuntimeException("Error unzipping file " + zipFilePath, e);
        }

        LOG.info("File: " + zipFilePath + " was unzipped");

        webXmlHandler.handle(unzipDir);
    }

    // check Zip Slip attack
    private boolean isValidDestinationPath(String targetDir, String destPathStr) {
        Path destPath = Paths.get(destPathStr);
        Path destPathNormalized = destPath.normalize();

        return destPathNormalized.toString().startsWith(targetDir + File.separator);
    }
}    
