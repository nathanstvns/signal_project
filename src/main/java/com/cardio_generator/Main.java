package com.cardio_generator;

import com.alerts.AlertGenerator;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketDataReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            com.data_management.DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(new String[]{});
        }

    try {

        DataReader reader = new WebSocketDataReader("ws://localhost:8080");
        DataStorage storage = DataStorage.getInstance();

        // read data fro m WebSocket
        reader.readData(storage);


        AlertGenerator alertGenerator = new AlertGenerator(storage);

        while (true) {
            Thread.sleep(1000);
            // Evaluate data for all patiënts
            for (Patient patient : storage.getAllPatients()) {
                alertGenerator.evaluateData(patient);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }}
}
