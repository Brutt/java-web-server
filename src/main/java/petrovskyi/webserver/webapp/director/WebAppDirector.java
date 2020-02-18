package petrovskyi.webserver.webapp.director;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.scanner.WarScanner;
import petrovskyi.webserver.webapp.unzip.WarUnzipper;
import petrovskyi.webserver.webapp.webxml.WebXmlHandler;

public class WebAppDirector {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public void manage(){
        LOG.info("Starting to manage webapps");

        WebXmlHandler webXmlHandler = new WebXmlHandler();
        WarUnzipper warUnzipper = new WarUnzipper(webXmlHandler);
        WarScanner warScanner = new WarScanner(warUnzipper);
        warScanner.scan();
    }
}

