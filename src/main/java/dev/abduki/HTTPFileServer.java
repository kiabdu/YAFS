package dev.abduki;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class HTTPFileServer {
    private final int port;
    private boolean isServerRunning = false;

    private RequestParser parser;
    private RequestRouter router;

    public HTTPFileServer(int port, URI filePath) {
        this.port = port;
        isServerRunning = true;

        router = new RequestRouter(filePath);
    }

    public void start() {
        isServerRunning = true;
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

    public int getPort() {
        return this.port;
    }
}
