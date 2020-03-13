package petrovskyi.webserver.webapp.webxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import petrovskyi.webserver.application.creator.ApplicationInfoCreator;
import petrovskyi.webserver.webapp.entity.ServletDefinition;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebXmlHandler {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String WEB_XML = "web.xml";
    private final String SERVLET_NAME_TAG = "servlet-name";
    private final String SERVLET_CLASS_TAG = "servlet-class";
    private final String SERVLET_MAPPING_TAG = "servlet-mapping";
    private final String URL_PATTERN_TAG = "url-pattern";
    private ApplicationInfoCreator applicationInfoCreator;

    public WebXmlHandler(ApplicationInfoCreator applicationInfoCreator) {
        this.applicationInfoCreator = applicationInfoCreator;
    }

    public void handle(String dir) {
        String webXmlPath = find(dir);

        if (webXmlPath == null) {
            return;
        }

        Map<String, String> urlToClassName = parse(webXmlPath);
        applicationInfoCreator.create(dir, urlToClassName);
    }

    String find(String dir) {
        LOG.info("Starting to search dir {} to find {}", dir, WEB_XML);

        try (Stream<Path> walk = Files.walk(Paths.get(dir))) {
            List<String> result = walk.map(x -> x.toString())
                    .filter(x -> x.endsWith(WEB_XML))
                    .collect(Collectors.toList());

            if (result.size() == 1) {
                LOG.info("Found {} in {}", WEB_XML, result.get(0));

                return result.get(0);

            } else {
                LOG.info("Could not find {} in {}", WEB_XML, dir);
            }

        } catch (IOException e) {
            LOG.error("Error while searching for {} in {}", WEB_XML, dir, e);
            throw new RuntimeException("Error while searching for " + WEB_XML + " in " + dir, e);
        }

        return null;
    }

    Map<String, String> parse(String webXmlPath) {
        LOG.info("Starting to parse {}", webXmlPath);

        Map<String, String> urlToClassName = new HashMap<>();
        Map<String, ServletDefinition> servletNameToDefinition = new HashMap<>();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new File(webXmlPath));

            Element rootElement = document.getDocumentElement();

            NodeList nodes = rootElement.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node instanceof Element) {
                    Element element = (Element) node;

                    if ("servlet".equals(element.getTagName())) {
                        ServletDefinition servletDefinition = new ServletDefinition();

                        String servletName = getElementValueByName(element, SERVLET_NAME_TAG).get(0);
                        String servletClassName = getElementValueByName(element, SERVLET_CLASS_TAG).get(0);

                        servletDefinition.setName(servletName);
                        servletDefinition.setClassName(servletClassName);

                        servletNameToDefinition.put(servletDefinition.getName(), servletDefinition);

                        LOG.debug("Found servlet {} , class {}", servletName, servletClassName);
                    } else if (SERVLET_MAPPING_TAG.equals(element.getTagName())) {
                        String servletName = getElementValueByName(element, SERVLET_NAME_TAG).get(0);

                        ServletDefinition servletDefinition = servletNameToDefinition.get(servletName);

                        List<String> servletUrls = getElementValueByName(element, URL_PATTERN_TAG);
                        for (String servletUrl : servletUrls) {
                            urlToClassName.put(servletUrl, servletDefinition.getClassName());
                        }

                        LOG.debug("Found mapping for servlet {} , url {}", servletName, servletUrls);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("Error while parsing {}", WEB_XML, e);
            throw new RuntimeException("Error while parsing " + WEB_XML, e);
        }

        return urlToClassName;
    }

    List<String> getElementValueByName(Element element, String name) {
        List<String> values = new ArrayList<>();

        if (name != null) {
            NodeList nodeList = element.getElementsByTagName(name);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node != null) {
                    values.add(node.getTextContent());
                }
            }
        }
        return values;
    }

}
