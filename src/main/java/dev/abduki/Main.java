package dev.abduki;

import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        HTTPFileServer server;

        try {
            server = new HTTPFileServer(8999, new URI("halomielz"));
        } catch (URISyntaxException e) {
            throw new RuntimeException("The specified URI is invalid! \n" + e);
        }

        System.out.println(server.getIPAddress());
        System.out.println(server.getPort());
    }
}