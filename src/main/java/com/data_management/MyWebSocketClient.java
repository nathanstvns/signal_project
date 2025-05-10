package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
/**
 * Concrete WebSocket client that connects to the server and gives
 * connection and message events to a handler.
 */
public class MyWebSocketClient extends WebSocketClient {
    private final MyWebSocketMessageHandler handler;
    /**
     * Constructs a new WebSocket client.
     * @param serverUri URI of the WebSocket server
     * @param handler Callback handler for connection and message events
     */
    public MyWebSocketClient(URI serverUri, MyWebSocketMessageHandler handler) {
        super(serverUri);
        this.handler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        handler.onOpen();
    }

    @Override
    public void onMessage(String message) {
        handler.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        handler.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        handler.onError(ex);
    }
}
