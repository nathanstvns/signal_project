package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class WebSocketDataReader implements DataReader {
    private CustomWebSocketClient client;
    private final String serverUrl;
    private DataStorage dataStorage;
    private boolean isConnected = false;

    public WebSocketDataReader(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;

        try {
            URI serverUri = new URI(serverUrl);
            client = new CustomWebSocketClient(serverUri);
            client.connect();


            while (!isConnected) {
                Thread.sleep(100);
            }

            System.out.println("Connected to WebSocket server and reading data...");

        } catch (URISyntaxException e) {
            throw new IOException("Invalid server URL: " + serverUrl, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection interrupted", e);
        }
    }

    private class CustomWebSocketClient extends WebSocketClient {
        public CustomWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connection established to: " + serverUrl);
            isConnected = true;
        }

        @Override
        public void onMessage(String message) {
            //parsing the message
            String[] parts = message.split(",");
            if (parts.length == 4) {
                try {
                    int patientId = Integer.parseInt(parts[0]);
                    long timestamp = Long.parseLong(parts[1]);
                    String recordType = parts[2];
                    double measurementValue = Double.parseDouble(parts[3]);

                    //save data in datastorage
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
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
        }
    }
}
