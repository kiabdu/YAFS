package dev.abduki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class RequestParser {
    private BufferedReader in;
    private PrintWriter out;
    private final String h1 = "Index of ";

    public URI parse() throws URISyntaxException, IOException {
        System.out.println("message readed succesly");

        // http request header format: (0) <HTTP_METHOD> (1) <REQUEST_URI> (2) <HTTP_VERSION>
        String httpRequestHeader = in.readLine();
        String[] requestHeaderParts;

        try {
            requestHeaderParts = httpRequestHeader.split("\\s+");
        } catch (NullPointerException e) {
            throw new NullPointerException("requestHeader was null! \n");
        }

        String requestMethod = requestHeaderParts[0];
        URI requestURI = new URI(requestHeaderParts[1]);
        // I dont really need the http version currently but might use it in the future
        // String httpVersion = requestHeaderParts[2];

        switch (requestMethod) {
            case "GET":
                System.out.println("request was getted");
                break;
            default:
                System.out.println("request was not knowed");
        }

        switch (requestURI.getPath()) {
            case "/":
                System.out.println(String.format("request for %s was getted", requestURI));
                break;
            default:
                System.out.println("request definitely was NOt getted");
        }

        return requestURI;
    }

    public void send(Map<String, LocalDate> files) throws IOException {

        StringBuilder htmlURIList = new StringBuilder();

        // dynamically creates a html div element for every entry of the map
        for (var entry : files.entrySet()) {
            htmlURIList.append(String.format("""
                    <div style="text-align: left;">
                        <a href="%s">%s</a>
                        %s
                    </div>""", entry.getKey(), entry.getKey(), entry.getValue()));
        }

        /*
         * response according to:
         * https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
         *
         * status line: HTTP/<HTTP_VERSION> <STATUS_CODE> <STATUS_PHRASE> \r\n
         * entity header: Content-Type: <CONTENT_TYPE> \r\n
         * entity header: Content-Length: <RESPONSE_CONTENT_BYTES_LENGTH> \r\n
         * CRLF: \r\n
         * message body: my htmlBody below in this case
         */
        String fromFile = Files.readString(Path.of("src/main/resources/response/200_ok.html"));
        System.out.println(fromFile);

        // replace %s with dynamic values, calculate html body byte length, replace %s for content length with actual length
        String httpResponse = String.format(fromFile, "%s", "ok", "ok", htmlURIList);
        int contentLength = fromFile.substring(fromFile.indexOf('<')).getBytes().length;
        httpResponse = String.format(httpResponse, contentLength);

        System.out.println(" // --------------------" +
                "response: \n" + httpResponse);
        out.write(httpResponse);
        out.flush();
    }

    public void start(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void stop() throws IOException {
        in.close();
        out.close();
    }

    public void flush() {
        out.flush();
    }
}
