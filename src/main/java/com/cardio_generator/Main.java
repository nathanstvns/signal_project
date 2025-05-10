package com.cardio_generator;

import com.data_management.*;
import com.alerts.AlertGenerator;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataStorage storage = DataStorage.getInstance();
        DataReader reader = new WebSocketDataReader("ws://localhost:8080");

        // try to connect to the server until it works
        boolean connected = false;
        while (!connected) {
            try {
                reader.readData(storage);
                connected = true;
            } catch (Exception e) {
                System.err.println("WebSocket server unreachable, trying again in 2 seconds...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        while (true) {
            try {
                Thread.sleep(1000);
                for (Patient patient : storage.getAllPatients()) {
                    List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);
                    if (!records.isEmpty()) {
                        PatientRecord last = records.get(records.size() - 1);
                        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                                last.getPatientId(),
                                last.getTimestamp(),
                                last.getRecordType(),
                                last.getMeasurementValue());
                    }
                    alertGenerator.evaluateData(patient);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }


    }
}
