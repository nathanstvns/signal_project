import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;

import com.data_management.MyWebSocketClient;
import com.data_management.MyWebSocketMessageHandler;

public class MyWebSocketClientTest {

    private MyWebSocketClient client;
    private MyWebSocketMessageHandler handler;

    @BeforeEach
    public void setup() throws URISyntaxException {
        handler = mock(MyWebSocketMessageHandler.class);
        client = new MyWebSocketClient(new URI("ws://localhost:8080"), handler);
    }

    @Test
    public void testOnOpenCallsHandler() {
        // Should pass] onOpen event to the handler
        client.onOpen(null);
        verify(handler, times(1)).onOpen();
    }

    @Test
    public void testOnMessageCallsHandler() {
        // Should pass onMessage event to the handler
        String message = "test message";
        client.onMessage(message);
        verify(handler, times(1)).onMessage(message);
    }

    @Test
    public void testOnCloseCallsHandler() {
        // Should pass onClose event to the handler
        client.onClose(1000, "Normal closure", true);
        verify(handler, times(1)).onClose(1000, "Normal closure", true);
    }

    @Test
    public void testOnErrorCallsHandler() {
        // Should pass onError event to the handler
        Exception ex = new Exception("Test error");
        client.onError(ex);
        verify(handler, times(1)).onError(ex);
    }
}
