package dev.abduki;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;

public class HTTPFileServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private RequestParser requestParser;
    private RequestRouter requestRouter;

    public void start(int port) throws IOException, URISyntaxException {
        while (true) {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

            requestParser = new RequestParser();
            requestParser.start(clientSocket);
            requestParser.parse();
            requestParser.stop();

            serverSocket.close();
            clientSocket.close();

            System.out.println("socket and readers have been closeded");
        }
    }
}
