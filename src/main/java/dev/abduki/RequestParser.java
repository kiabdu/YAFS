package dev.abduki;

import dev.abduki.types.ResponseType;
import dev.abduki.util.FileHandler;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        String decodedRequestPath;

        try {
            requestHeaderParts = httpRequestHeader.split("\\s");
            decodedRequestPath = URLDecoder.decode(requestHeaderParts[1], StandardCharsets.UTF_8);
        } catch (NullPointerException e) {
            throw new NullPointerException("requestHeader was null! \n");
        }

        String requestMethod = requestHeaderParts[0];
        Path requestPath = Path.of(decodedRequestPath);
        //String httpVersion = requestHeaderParts[2];

        tmpCurrentPath = requestPath.toString();
        return requestPath;
    }

    public void send(Map<String, LocalDate> files) throws IOException {
        String httpResponse;
        // single element? -> probably a file -> get path -> check file status -> second cases
        // condition

        System.out.println("files is null? " + (files == null));

        String singleFilePath = files.size() == 1 ? files.entrySet().iterator().next().getKey() : "";
        File singleFile = !singleFilePath.isEmpty() ? new File(singleFilePath) : null;

        // in RequestRouter we checked if the filepath exists, else null is returned
        if (files == null || (singleFile != null && !singleFile.exists())) {
            httpResponse = generateHttpResponseBody(ResponseType.NOT_FOUND, null);
            out.write(httpResponse);
            out.flush();
            return;
        }

        if (files.size() == 1 && (singleFile != null && singleFile.isFile())) {

            String contentType = Files.probeContentType(singleFile.toPath());
            if(contentType == null){
                contentType = "application/octet-stream";
            }

            long contentLength = singleFile.length();
            String contentDisposition = "attachment; filename=\"" + singleFile.getName() + "\"";

            String httpHeaders = String.format("""
                    HTTP/1.1 200 OK
                    Content-Type: %s
                    Content-Length: %d
                    Content-Disposition: %s\r\n
                    \r\n""", contentType, contentLength, contentDisposition);

            out.write(httpHeaders);
            out.flush();

            // stream file content to outputstream of clientsocket
            try(BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(singleFile))){

                // to read 8KB chunks instead of single bytes
                byte[] buffer = new byte[8192];
                int bytesRead;

                while((bytesRead = fileIn.read(buffer)) != -1){
                    fileOut.write(buffer, 0, bytesRead);
                }

                fileOut.flush();
            } catch (IOException e){
                throw new IOException("Error streaming file: " + e.getMessage());
            }
        } else {
            httpResponse = generateHttpResponseBody(ResponseType.OK, files);
            out.write(httpResponse);
            out.flush();
        }
    }

    public void start(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
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
