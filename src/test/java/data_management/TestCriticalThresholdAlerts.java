package data_management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.strategies.BloodPressureStrategy;
import com.data_management.Patient;


@ExtendWith(MockitoExtension.class)
public class TestCriticalThresholdAlerts {

    @Mock
    private AlertGenerator mockGenerator;

    private BloodPressureStrategy strategy = new BloodPressureStrategy();

    @Test
    void testCriticalSystolicAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(185, "BloodPressure_Systolic", System.currentTimeMillis());

        strategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testCriticalDiastolicAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(55, "BloodPressure_Diastolic", System.currentTimeMillis());

        strategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testIncreasingTrendAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(100, "BloodPressure_Systolic", time);
        patient.addRecord(115, "BloodPressure_Systolic", time + 1000);
        patient.addRecord(130, "BloodPressure_Systolic", time + 2000);

        strategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testDecreasingTrendAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(110, "BloodPressure_Diastolic", time);
        patient.addRecord(95, "BloodPressure_Diastolic", time + 1000);
        patient.addRecord(80, "BloodPressure_Diastolic", time + 2000);

        strategy.checkAlert(patient, mockGenerator);

        // verify that 1 alert is triggerd (only the  downtrend)
        verify(mockGenerator, times(1)).triggerAlert(any(Alert.class));
    }
}
