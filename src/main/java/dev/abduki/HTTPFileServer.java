package dev.abduki;

import dev.abduki.util.FileHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class HTTPFileServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private RequestParser requestParser;
    private FileHandler fileHandler;

    public static Path baseFilePath;

    public void start(int port) throws IOException, URISyntaxException {
        serverSocket = new ServerSocket(port);

        while (true) {
            clientSocket = serverSocket.accept();

            requestParser = new RequestParser();
            requestParser.start(clientSocket);
            Path requestedPath = requestParser.parse();
            fileHandler = new FileHandler();
            System.out.println(baseFilePath.toString() + requestedPath);
            Map<String, LocalDate> files = fileHandler.getFiles(Path.of(baseFilePath.toString() + requestedPath.toString()));
            requestParser.send(files);
            requestParser.stop();
            clientSocket.close();
        }

        //serverSocket.close();
    }
}
