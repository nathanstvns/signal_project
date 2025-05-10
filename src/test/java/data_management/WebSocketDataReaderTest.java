package data_management;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.WebSocketDataReader;
import com.data_management.DataStorage;

public class WebSocketDataReaderTest {

    private WebSocketDataReader reader;
    private DataStorage dataStorage;

    @BeforeEach
    public void setup() {
        dataStorage = mock(DataStorage.class);
        reader = new WebSocketDataReader("ws://localhost:8080");
        reader.dataStorage = dataStorage;
    }

    @Test
    public void testOnMessageValidData() {
        // Should parse and store a normal numeric message
        String message = "1,1620000000000,HeartRate,98.6";
        reader.onMessage(message);
        // Verify that addPatientData was called with correct parameters
        verify(dataStorage, times(1)).addPatientData(1, 98.6, "HeartRate", 1620000000000L);
    }

    @Test
    public void testOnMessagePercentData() {
        // Should correctly hande and parse a percent value with the 97%
        String message = "2,1620000001000,Saturation,97.0%";
        reader.onMessage(message);
        verify(dataStorage, times(1)).addPatientData(2, 97.0, "Saturation", 1620000001000L);
    }

    @Test
    public void testOnMessageAlertData() {
        // Should ignore alert messages and not store them as patient data
        String message = "3,1620000002000,Alert,triggered";
        reader.onMessage(message);
        // Should not call addPatientData for alerts
        verify(dataStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    public void testOnMessageInvalidData() {
        // Should handle malformed messages wihtout exception or data stored
        String message = "invalid,message,format";
        assertDoesNotThrow(() -> reader.onMessage(message));
        verify(dataStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }
}
