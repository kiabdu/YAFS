package dev.abduki;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {

        // server loop
        HTTPFileServer server = new HTTPFileServer();

        try {
            server.start(9899);
        } catch (IOException e) {
            throw new RuntimeException("Server could not be started! \n" + e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("The URI has invalid formatting! \n" + e);
        }

        System.out.println("server runnded succesly");
    }
}