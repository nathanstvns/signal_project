package com.data_management;
/*interface for handling WebSocket events and messages.
*/
public interface MyWebSocketMessageHandler {
    void onOpen();
    void onMessage(String message);
    void onClose(int code, String reason, boolean remote);
    void onError(Exception ex);
}
