package petrovskyi.webserver.webapp.webxml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import petrovskyi.webserver.application.creator.ApplicationInfoCreator;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class WebXmlHandlerTest {
    @Mock
    Element element;

    @Mock
    NodeList nodeList;

    @Mock
    Node node;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetElementValueByName() {
        when(element.getElementsByTagName(Mockito.anyString())).thenReturn(nodeList);
        when(nodeList.getLength()).thenReturn(1);
        when(nodeList.item(Mockito.anyInt())).thenReturn(node);
        when(node.getTextContent()).thenReturn("test");

        WebXmlHandler webXmlHandler = new WebXmlHandler();
        List<String> stringList = webXmlHandler.getElementValueByName(element, "test_tag");

        assertEquals("test", stringList.get(0));
    }

    @Test
    void getUrlToClassName() {
        String mockWebXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<web-app xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                "         xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n" +
                "         version=\"3.0\">\n" +
                "\n" +
                "    <servlet>\n" +
                "        <servlet-name>HelloServlet</servlet-name>\n" +
                "        <servlet-class>petrovskyi.web.HelloServlet</servlet-class>\n" +
                "    </servlet>\n" +
                "\n" +
                "    <servlet-mapping>\n" +
                "        <servlet-name>HelloServlet</servlet-name>\n" +
                "        <url-pattern>/</url-pattern>\n" +
                "        <url-pattern>/hello</url-pattern>\n" +
                "    </servlet-mapping>\n" +
                "\n" +
                "</web-app>";

        WebXmlHandler webXmlHandler = new WebXmlHandler();

        Map<String, String> urlToClassName = webXmlHandler.getUrlToClassName(new ByteArrayInputStream(mockWebXml.getBytes()));

        assertEquals(2, urlToClassName.size());
        assertEquals("petrovskyi.web.HelloServlet", urlToClassName.get("/"));
        assertEquals("petrovskyi.web.HelloServlet", urlToClassName.get("/hello"));
    }
}