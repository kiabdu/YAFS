package dev.abduki;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class RequestRouter {

    /*
    public RequestRouter(URI filePath) {
        this.filePath = filePath;
        files = new ArrayList<>();
    }
    */

    public Map<String, LocalDate> getFiles(URI filePath) {
        System.out.println("path: " + filePath);
        File folder = new File(filePath);
        HashMap<String, LocalDate> fileInformationMap = new HashMap<>();

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

        URI filePath;
        try {
            filePath = new URI(file.getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI! \n" + e);
        }

        LocalDate lastModified = LocalDate.ofInstant(
                Instant.ofEpochMilli(file.lastModified()),
                ZoneId.systemDefault());

        information[0] = filePath.getPath();
        information[1] = lastModified.toString();

        return information;
    }
}
