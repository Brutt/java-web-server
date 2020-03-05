package petrovskyi.webserver.web.handler;

import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.web.parser.RequestParser;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class RequestHandler implements Runnable {
    private Socket socket;
    private BufferedReader socketReader;
    private BufferedOutputStream socketWriter;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Map<String, ApplicationInfo> appNameToApplicationInfo = ApplicationRegistry.getInstance().getAppNameToApplicationInfo();
            System.out.println(appNameToApplicationInfo);

            socketWriter = new BufferedOutputStream(socket.getOutputStream());
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
            System.out.println("Exception: " + e.getMessage());
        }
    }
}