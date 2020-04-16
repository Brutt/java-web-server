package petrovskyi.webserver.webapp.webxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import petrovskyi.webserver.webapp.entity.WebXmlDefinition;
import petrovskyi.webserver.webapp.entity.WebXmlObjectDefinition;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebXmlHandler {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final String WEB_INF = "WEB-INF";
    private final String WEB_XML = "web.xml";
    private final String SERVLET_TAG = "servlet";
    private final String NAME_TAG = "-name";
    private final String CLASS_TAG = "-class";
    private final String MAPPING_TAG = "-mapping";
    private final String FILTER_TAG = "filter";
    private final String URL_PATTERN_TAG = "url-pattern";
    private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    public WebXmlDefinition handle(File dir) throws FileNotFoundException {
        String webXmlPath = find(dir);

        return parse(webXmlPath);
    }

    String find(File dir) throws FileNotFoundException {
        LOG.info("Starting to search dir {} to find {}", dir, WEB_XML);

        File webXmlFile = Paths.get(dir.getPath(), WEB_INF, WEB_XML).toFile();
        boolean exists = webXmlFile.exists();

        if (exists) {
            LOG.info("Found {} in {}", WEB_XML, webXmlFile);

            return webXmlFile.getPath();

        } else {
            LOG.error("Could not find {} in {}", WEB_XML, dir);
            throw new FileNotFoundException("Could not find " + WEB_XML + " in " + dir);
        }
    }

    private WebXmlDefinition parse(String webXmlPath) throws FileNotFoundException {
        LOG.info("Starting to parse {}", webXmlPath);

        return getWebXmlDefinition(new FileInputStream(webXmlPath));
    }

    WebXmlDefinition getWebXmlDefinition(InputStream inputStream) {
        LOG.debug("Start to fill webXmlDefinition based on inputStream {}", inputStream);

        Map<String, List<String>> urlToServletClassName = new HashMap<>();
        Map<String, List<String>> urlToFilterClassName = new HashMap<>();
        Map<String, WebXmlObjectDefinition> servletNameToDefinition = new HashMap<>();
        Map<String, WebXmlObjectDefinition> filterNameToDefinition = new HashMap<>();

        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element rootElement = document.getDocumentElement();

            NodeList nodes = rootElement.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node instanceof Element) {
                    Element element = (Element) node;

                    if (SERVLET_TAG.equals(element.getTagName())) {
                        WebXmlObjectDefinition webXmlObjectDefinition = extractWebXmlObjectDefinition(SERVLET_TAG, element);
                        servletNameToDefinition.put(webXmlObjectDefinition.getName(), webXmlObjectDefinition);

                    } else if ((SERVLET_TAG + MAPPING_TAG).equals(element.getTagName())) {
                        updateMap(SERVLET_TAG, element, servletNameToDefinition, urlToServletClassName);

                    } else if (FILTER_TAG.equals(element.getTagName())) {
                        WebXmlObjectDefinition webXmlObjectDefinition = extractWebXmlObjectDefinition(FILTER_TAG, element);
                        filterNameToDefinition.put(webXmlObjectDefinition.getName(), webXmlObjectDefinition);

                    } else if ((FILTER_TAG + MAPPING_TAG).equals(element.getTagName())) {
                        updateMap(FILTER_TAG, element, filterNameToDefinition, urlToFilterClassName);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("Error while parsing {}", inputStream, e);
            throw new RuntimeException("Error while parsing " + inputStream, e);
        }

        return new WebXmlDefinition(urlToServletClassName, urlToFilterClassName);
    }

    private WebXmlObjectDefinition extractWebXmlObjectDefinition(String prefix, Element element) {
        String objectName = getElementValueByName(element, prefix + NAME_TAG).get(0);
        String objectClassName = getElementValueByName(element, prefix + CLASS_TAG).get(0);

        LOG.debug("Found {} {} , class {}", prefix, objectName, objectClassName);

        return new WebXmlObjectDefinition(objectName, objectClassName);
    }

    private void updateMap(String prefix,
                           Element element,
                           Map<String, WebXmlObjectDefinition> objectNameToDefinition,
                           Map<String, List<String>> urlToObjectClassName) {
        String objectName = getElementValueByName(element, prefix + NAME_TAG).get(0);

        WebXmlObjectDefinition webXmlObjectDefinition = objectNameToDefinition.get(objectName);

        List<String> objectUrls = getElementValueByName(element, URL_PATTERN_TAG);
        for (String objectUrl : objectUrls) {
            List<String> stringList = urlToObjectClassName.get(objectUrl);
            if (stringList != null) {
                stringList.add(webXmlObjectDefinition.getClassName());
            } else {
                stringList = new ArrayList<>();
                stringList.add(webXmlObjectDefinition.getClassName());
                urlToObjectClassName.put(objectUrl, stringList);
            }
        }

        LOG.debug("Found mapping for {} {} , url {}", prefix, objectName, objectUrls);
    }

    List<String> getElementValueByName(Element element, String name) {
        List<String> values = new ArrayList<>();

        if (name != null) {
            NodeList nodeList = element.getElementsByTagName(name);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node != null) {
                    values.add(node.getTextContent().trim());
                }
            }
        }
        return values;
    }

}
