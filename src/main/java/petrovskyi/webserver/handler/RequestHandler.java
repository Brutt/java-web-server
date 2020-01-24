package petrovskyi.webserver.handler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
            socketWriter = new BufferedOutputStream(socket.getOutputStream());
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//            RequestParser requestParser = new RequestParser();
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