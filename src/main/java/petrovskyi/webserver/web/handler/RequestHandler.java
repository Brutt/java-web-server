package petrovskyi.webserver.web.handler;

import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.web.http.WebServerServletRequest;
import petrovskyi.webserver.web.http.WebServerServletResponse;
import petrovskyi.webserver.web.parser.RequestParser;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.http.HttpServlet;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket socket;
    private BufferedReader socketReader;
    private BufferedOutputStream socketWriter;
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            socketWriter = new BufferedOutputStream(socket.getOutputStream());
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            RequestParser requestParser = new RequestParser();
            WebServerServletRequest webServerServletRequest = requestParser.parseRequest(socketReader);


            //hardcode
            WebServerServletResponse webServerServletResponse = new WebServerServletResponse(new WebServerOutputStream(socket.getOutputStream()));

            ApplicationInfo simple = applicationRegistry.getApplication("simple");

            HttpServlet httpServlet = simple.getUrlToServlet().get("/");
            httpServlet.service(webServerServletRequest, webServerServletResponse);
            //hardcode end



//            RequestParser requestParser = new RequestParser();
//            requestParser.parseRequest(socketReader);
//            Request request = requestParser.parseRequest(socketReader);
//
//            ResourceReader resourceReader = new ResourceReader();
//            ByteArrayOutputStream resource = resourceReader.getResource(request.getUri());
//
//            ResponseWriter responseWriter = new ResponseWriter();
//            responseWriter.writeSuccessResponse(socketWriter, resource);

            socketWriter.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}