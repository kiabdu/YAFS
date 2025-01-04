package dev.abduki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RequestParser {
    private BufferedReader in;
    private PrintWriter out;

    public URI parse() throws URISyntaxException, IOException {
        System.out.println("message readed succesly");

        // client request
        List<String> clientSocketRequestLines = in.lines().toList();

        System.out.println("liest size: " + clientSocketRequestLines.size());

        for (String s : clientSocketRequestLines) {
            System.out.println("mielz");
            System.out.println(s);
        }

        System.out.println("jielz");
        // server response
        return new URI("tehst");
    }

    public void start(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
    }
}
