package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outputs patient health data to files in a directory.
 *
 * <p>This class implements the {@link OutputStrategy} interface and provides
 * functionality for writing patient health data to text files. Each type of
 * data is written to a separate file, with filenames based on the data label.
 */

public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to camelCase
    /**
     * The base directory where output files are stored.
     */
    private String baseDirectory;

    /**
     * map with file paths for each data label.
     */
    // Changed variable name to camelCase and added generic type parameters
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a FileOutputStrategy with the specified base directory.
     *
     * @param baseDirectory the directory where output files will be stored
     */

    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs patient health data to a file.
     *
     * <p>The method writes the provided health data to a text file in the
     * specified base directory. If the file doesnt exist, it is created accordingly.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label a label for the type of data
     * @param data the data to be output
     */

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name to camelCase
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}