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

import java.util.List;

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
}