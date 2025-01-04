package dev.abduki;

import java.net.Socket;

public class RequestParser {

    public void parse(String request) {

        // example GET-request:
        //
        String[] requestParts = request.split("\\s+");
        String requestType = requestParts[0];
        String requestPath = requestParts[0];
        String requestProtocol = requestParts[0];

    }

    private void readRequestData(Socket clientSocket) {

    }
}
