package data_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.DataReader;

import java.util.List;

class DataStorageTest {

    private DataReader reader; // Mock van DataReader
    private DataStorage storage;

    @BeforeEach
    void setUp() {

        reader = mock(DataReader.class);


        storage = new DataStorage();


        try {
            doNothing().when(reader).readData(storage);
            reader.readData(storage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddAndGetRecords() {

        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);


        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);


        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
    }
}
