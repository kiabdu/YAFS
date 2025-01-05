package dev.abduki;

import dev.abduki.types.ResponseType;
import dev.abduki.util.FileHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class RequestParser {
    private BufferedReader in;
    private PrintWriter out;

    private final String INDEX = "Index of ";
    private final String RESPONSE_OK_FILEPATH = "src/main/resources/response/200_ok.html";
    private final String RESPONSE_NOT_FOUND_FILEPATH = "src/main/resources/response/404_not_found.html";

    public Path parse() throws IOException {
        // http request header format: (0) <HTTP_METHOD> (1) <REQUEST_URI> (2) <HTTP_VERSION>
        String httpRequestHeader = in.readLine();
        String[] requestHeaderParts;

        try {
            requestHeaderParts = httpRequestHeader.split("\\s+");
        } catch (NullPointerException e) {
            throw new NullPointerException("requestHeader was null! \n");
        }

        String requestMethod = requestHeaderParts[0];
        Path requestPath = Path.of(requestHeaderParts[1]);
        // I dont really need the http version currently but might use it in the future
        // String httpVersion = requestHeaderParts[2];

        return requestPath;
    }

    public void send(Map<String, LocalDate> files) throws IOException {
        String httpResponse;

        // in RequestRouter we checked if the filepath exists, else null is returned
        if (files == null) {
            httpResponse = generateHttpResponseBody(ResponseType.NOT_FOUND, null);
        } else {
            httpResponse = generateHttpResponseBody(ResponseType.OK, files);
        }

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

    public String generateHttpResponseBody(ResponseType responseType, Map<String, LocalDate> files) throws IOException {
        String httpResponse = "";
        String htmlBody;
        int contentLength;

        String httpResponseUnformatted = """
                HTTP/1.1 %s
                Content-Type: text/html
                Content-Length: %s\r\n
                \r\n
                %s""";

        switch (responseType) {
            case OK:
                StringBuilder htmlURIList = new StringBuilder();
                String htmlBodyNotFormatted = FileHandler.serializeResponseFile(RESPONSE_OK_FILEPATH);

                // dynamically creates a html div element for every entry of the map
                System.out.print("// ----- request entries:\t");
                for (var entry : files.entrySet()) {
                    System.out.println(String.format("%s:%s", entry.getKey(), entry.getValue()));
                    String filePathWithoutBasePath = entry.getKey().replace(HTTPFileServer.baseFilePath.toString(), "");
                    htmlURIList.append(String.format("""
                            <div>
                                <a href="%s">%s</a>
                                %s
                            </div>""", filePathWithoutBasePath, entry.getKey(), entry.getValue()));
                }

                htmlBody = String.format(htmlBodyNotFormatted, "ok man", "ok", htmlURIList);
                contentLength = FileHandler.getFileContentLength(htmlBody);
                httpResponse = String.format(httpResponseUnformatted, "200 OK", contentLength, htmlBody);
                break;

            case NOT_FOUND:
                htmlBody = FileHandler.serializeResponseFile(RESPONSE_NOT_FOUND_FILEPATH);
                contentLength = FileHandler.getFileContentLength(htmlBody);
                httpResponse = String.format(httpResponseUnformatted, "404 Not Found", contentLength, htmlBody);
                break;
        }

        return httpResponse;
    }
}
