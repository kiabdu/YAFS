package dev.abduki;

import dev.abduki.types.ResponseType;
import dev.abduki.util.FileHandler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class RequestParser {
    private BufferedReader in;
    private PrintWriter out;
    private BufferedOutputStream fileOut;

    private final String INDEX = "Index of " + HTTPFileServer.baseFilePath;
    private final String RESPONSE_OK_FILEPATH = "src/main/resources/response/200_ok.html";
    private final String RESPONSE_NOT_FOUND_FILEPATH = "src/main/resources/response/404_not_found.html";

    private String tmpCurrentPath;

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

        tmpCurrentPath = requestPath.toString();
        return requestPath;
    }

    public void send(Map<String, LocalDate> files) throws IOException {
        String httpResponse;
        // single element? -> probably a file -> get path -> check file status -> second cases
        // condition
        String singleFilePath = files.size() == 1 ? files.entrySet().iterator().next().getKey() : "";
        File singleFile = singleFilePath.length() >= 1 ? new File(singleFilePath) : null;

        // in RequestRouter we checked if the filepath exists, else null is returned
        if (files == null) {
            httpResponse = generateHttpResponseBody(ResponseType.NOT_FOUND, null);
        } else if (files.size() == 1 && (singleFile != null && singleFile.isFile())) {

            byte[] fileAsByteStream = new byte[(int) singleFile.length()];

            // read file as stream, write it to fileoutputstream which uses clientSockets outputstream
            FileInputStream inputStream = new FileInputStream(singleFile);
            DataInputStream fileIn = new DataInputStream(new BufferedInputStream(singleFile));
            int fileContent;
            while ((fileContent = inputStream.read()) != -1) {
                fileOut.write(fileContent);
            }
            fileOut.flush();
            return;
        } else {
            httpResponse = generateHttpResponseBody(ResponseType.OK, files);
        }

        out.write(httpResponse);
        out.flush();
    }

    public void start(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        fileOut = new BufferedOutputStream(clientSocket.getOutputStream());
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

                htmlBody = String.format(htmlBodyNotFormatted, INDEX + tmpCurrentPath, INDEX + tmpCurrentPath, htmlURIList);
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
