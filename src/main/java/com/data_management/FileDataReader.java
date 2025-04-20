package com.data_management;

import java.io.*;
import java.nio.file.*;

public class FileDataReader implements DataReader {
    private String directoryPath;

    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Files.list(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length == 4) {
                                int patientId = Integer.parseInt(parts[0]);
                                long timestamp = Long.parseLong(parts[1]);
                                String recordType = parts[2];
                                double measurementValue = Double.parseDouble(parts[3]);
                                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
