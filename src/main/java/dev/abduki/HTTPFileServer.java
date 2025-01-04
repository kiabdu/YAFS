package dev.abduki;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HTTPFileServer {
    private final int port;
    private boolean isServerRunning = false;

    public HTTPFileServer(int port) {
        this.port = port;
        isServerRunning = true;
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
