package dev.abduki;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

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
            requestParser.send("halo mielzkorpf");
            requestParser.stop();

            System.out.println("requested path: " + requestedPath);
            clientSocket.close();

            System.out.println("socket and readers have been closeded");
        }

        //serverSocket.close();
    }
}
