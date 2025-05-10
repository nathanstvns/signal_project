package com.data_management;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
/**
 * DataReader implementation for real-time data via WebSocket.
 * Connects to a WebSocket server, receives messages, parses them,
 * and stores valid patient data in DataStorage.
 */
public class WebSocketDataReader implements DataReader, MyWebSocketMessageHandler {
    private MyWebSocketClient client;
    private final String serverUrl;
    private DataStorage dataStorage;
    private volatile boolean isConnected = false;

    public WebSocketDataReader(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Connects to the WebSocket server and starts receiving data.
     * Retries connection if the server is unreachable.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            if (client != null) {
                client.close();
            }
            URI serverUri = new URI(serverUrl);
            client = new MyWebSocketClient(serverUri, this);
            client.connect();

            // Wait max 10 seconds for connection
            int waited = 0;
            while (!isConnected && waited < 100) {
                Thread.sleep(100);
                waited++;
            }
            if (!isConnected) {
                throw new IOException("WebSocket server not reachable");
            }
            System.out.println("Connected to WebSocket server and reading data...");
        } catch (URISyntaxException e) {
            throw new IOException("Invalid server URL: " + serverUrl, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection interrupted", e);
        }
    }

    // Handler methods
    // WebSocket event handlers

    @Override
    public void onOpen() {
        System.out.println("Connection established to: " + serverUrl);
        isConnected = true;
    }

    @Override
    public void onMessage(String message) {
        // Parse incoming message and store in DataStorage
        String[] parts = message.split(",");
        // Only proceed if the message has exactly 4 parts.
        if (parts.length == 4) {
            try {
                // Parse patient ID from the first field.
                int patientId = Integer.parseInt(parts[0]);
                // Parse timestamp from the second field.
                long timestamp = Long.parseLong(parts[1]);
                // The third field is the type of record
                String recordType = parts[2];
                String valueStr = parts[3].trim();
                // If the value ends with a percent sign remove it before parsing.
                if (valueStr.endsWith("%")) {
                    valueStr = valueStr.substring(0, valueStr.length() - 1);
                }
                // Only process numeric measurement records, not alerts.
                if (!recordType.equals("Alert")) {
                    double measurementValue = Double.parseDouble(valueStr);
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing message: " + message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code: " + code + ", Reason: " + reason);
        isConnected = false;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        isConnected = false;
    }
}
