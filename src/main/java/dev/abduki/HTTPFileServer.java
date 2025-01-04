package dev.abduki;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;

public class HTTPFileServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private RequestParser requestParser;
    private RequestRouter requestRouter;

    public void start(int port) throws IOException, URISyntaxException {
        serverSocket = new ServerSocket(port);

        while (true) {
            clientSocket = serverSocket.accept();

            requestParser = new RequestParser();
            requestParser.start(clientSocket);
            URI requestedPath = requestParser.parse();
            System.out.println(requestedPath);

            Map<String, LocalDate> files = requestRouter.getFiles(requestedPath);

            requestParser.send(files);
            requestParser.flush();
            requestParser.stop();
            clientSocket.close();
            System.out.println("socket and readers have been closeded");
        }

        //serverSocket.close();
    }
}
