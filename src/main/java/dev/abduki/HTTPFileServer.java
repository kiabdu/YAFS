package dev.abduki;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPFileServer {
    private boolean isServerRunning = false;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private RequestParser requestParser;
    private RequestRouter requestRouter;

    public void start(int port) throws IOException {
        isServerRunning = true;

        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();

        requestParser = new RequestParser();
        requestRouter = new RequestRouter();
    }

    public void stop() {
        isServerRunning = false;
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    public String getIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("The hosts IP address could not be fetched! \n" + e);
        }
    }
}
