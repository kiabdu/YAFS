package dev.abduki.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {

    /*
    public RequestRouter(URI filePath) {
        this.filePath = filePath;
        files = new ArrayList<>();
    }
    */

    public Map<String, LocalDate> getFiles(Path filePath) {
        File folder = new File(filePath.toString());
        HashMap<String, LocalDate> fileInformationMap = new HashMap<>();

        if (!Files.exists(filePath)) {
            return null;
        }

        if (folder.isFile()) {
            String[] fileInformation = extractInformationFromFile(folder);
            fileInformationMap.put(fileInformation[0], LocalDate.parse(fileInformation[1]));
            return fileInformationMap;
        }

        File[] files = folder.listFiles();
        for (File f : files) {
            String[] fileInformation = extractInformationFromFile(f);
            fileInformationMap.put(fileInformation[0], LocalDate.parse(fileInformation[1]));
        }

        return fileInformationMap;
    }

    private String[] extractInformationFromFile(File file) {
        String[] information = new String[2];

        Path filePath = Path.of(file.getPath());
        LocalDate lastModified = LocalDate.ofInstant(
                Instant.ofEpochMilli(file.lastModified()),
                ZoneId.systemDefault());

        information[0] = filePath.toString();
        information[1] = lastModified.toString();

        return information;
    }

    public static String serializeResponseFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    public static int getFileContentLength(String fileContent) {
        return fileContent.getBytes().length;
    }
}
