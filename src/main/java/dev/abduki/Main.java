package dev.abduki;

public class Main {
    public static void main(String[] args) {
        HTTPFileServer server = new HTTPFileServer(8999);

        System.out.println(server.getIPAddress());
        System.out.println(server.getPort());
    }
}