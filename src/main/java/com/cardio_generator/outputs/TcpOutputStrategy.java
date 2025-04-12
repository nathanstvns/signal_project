package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs patient health data over a TCP (Transmission Control Protocol) connection.
 *
 * <p>This class implements the {@link OutputStrategy} interface and allows
 * patient health data to be sent to a connected TCP client.
 */

public class TcpOutputStrategy implements OutputStrategy {
    /**
     * The server socket that lisntens for incoming client connections.
     */
    private ServerSocket serverSocket;
    /**
     * The client socket representing the connected client.
     */
    private Socket clientSocket;
    /**
     * The writer used to send data to the connected client.
     */
    private PrintWriter out;

    /**
     * Constructs a TcpOutputStrategy that listens on the specified port.
     *
     * <p>The server will start listening for incoming connections on the given port.
     * If a client connects, it makes a communication channel for sending health data.
     *
     * @param port the port number on which the server will listen for the connections
     */

    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs patient health data to the connected TCP client.
     *
     * <p>The method turns the provided health data into a string and
     * sends it over the TCP connection to the connected client.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label a label for the type of data
     * @param data the data to be output
     */

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
