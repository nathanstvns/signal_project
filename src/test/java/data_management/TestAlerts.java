package data_management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.strategies.*;
import com.data_management.Patient;


@ExtendWith(MockitoExtension.class)
public class TestAlerts {

    @Mock
    private AlertGenerator mockGenerator;

    private BloodPressureStrategy bloodPressureStrategy = new BloodPressureStrategy();
    private BloodOxygenStrategy bloodOxygenStrategy = new BloodOxygenStrategy();
    private HypoxemiaStrategy hypoxemiaStrategy = new HypoxemiaStrategy();
    private ECGStrategy ecgStrategy = new ECGStrategy();
    private TriggeredAlertStrategy triggeredAlertStrategy = new TriggeredAlertStrategy();


    @Test
    void testCriticalSystolicAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(185, "BloodPressure_Systolic", System.currentTimeMillis());

        bloodPressureStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testCriticalDiastolicAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(55, "BloodPressure_Diastolic", System.currentTimeMillis());

        bloodPressureStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testIncreasingTrendAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(100, "BloodPressure_Systolic", time);
        patient.addRecord(115, "BloodPressure_Systolic", time + 1000);
        patient.addRecord(130, "BloodPressure_Systolic", time + 2000);

        bloodPressureStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testDecreasingTrendAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(110, "BloodPressure_Diastolic", time);
        patient.addRecord(95, "BloodPressure_Diastolic", time + 1000);
        patient.addRecord(80, "BloodPressure_Diastolic", time + 2000);

        bloodPressureStrategy.checkAlert(patient, mockGenerator);

        // verify that 1 alert is triggerd (only the  downtrend)
        verify(mockGenerator, times(1)).triggerAlert(any(Alert.class));
    }
    @Test
    void testLowOxygenAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(91.0, "BloodOxygen", System.currentTimeMillis());

        bloodOxygenStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testNoAlertForNormalOxygen() {
        Patient patient = new Patient(1);
        patient.addRecord(95.0, "BloodOxygen", System.currentTimeMillis());

        bloodOxygenStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }

    @Test
    void testRapidDropAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(98.0, "BloodOxygen", time);
        patient.addRecord(92.5, "BloodOxygen", time + 300_000); // 5 mins later with a drop of 5.5%

        bloodOxygenStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testNoAlertForSlowDrop() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(98.0, "BloodOxygen", time);
        patient.addRecord(94.0, "BloodOxygen", time + 300_000); // 5 minutes later, drop of 4% (shouldnt tirgger)

        bloodOxygenStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }

    @Test
    void testNoAlertForDropAfterTenMinutes() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();
        patient.addRecord(98.0, "BloodOxygen", time);
        patient.addRecord(92.0, "BloodOxygen", time + 700_000); // More than 10 minutes later

        bloodOxygenStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }
    @Test
    void testHypotensiveHypoxemiaAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();

        //  both low BP and low oxygen
        patient.addRecord(88.0, "BloodPressure_Systolic", time);
        patient.addRecord(91.0, "BloodOxygen", time + 1000);

        hypoxemiaStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }
    @Test
    void testNoAlertWithNormalBloodPressure() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();

        // Normal BP but low oxygen, shouldnt trigger
        patient.addRecord(95.0, "BloodPressure_Systolic", time);
        patient.addRecord(91.0, "BloodOxygen", time + 1000);

        hypoxemiaStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }
    @Test
    void testNoAlertWithNormalOxygen() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();

        // Low BP but normal oxygen, shouldnt trigger
        patient.addRecord(88.0, "BloodPressure_Systolic", time);
        patient.addRecord(94.0, "BloodOxygen", time + 1000);

        hypoxemiaStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }
    @Test
    void testECGPeakAlert() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();

        // normal readings (80-90)
        for (int i = 0; i < 10; i++) {
            patient.addRecord(80 + Math.random() * 10, "ECG", time + (i * 1000));
        }

        //  abnormally high reading
        patient.addRecord(150, "ECG", time + 11000);

        ecgStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }

    @Test
    void testNoAlertForNormalECG() {
        Patient patient = new Patient(1);
        long time = System.currentTimeMillis();

        // normal ECG readings (around 80-90)
        for (int i = 0; i < 11; i++) {
            patient.addRecord(80 + Math.random() * 10, "ECG", time + (i * 1000));
        }

        ecgStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator, never()).triggerAlert(any(Alert.class));
    }
    @Test
    void testTriggeredAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(1.0, "TriggeredAlert", System.currentTimeMillis());

        triggeredAlertStrategy.checkAlert(patient, mockGenerator);

        verify(mockGenerator).triggerAlert(any(Alert.class));
    }
}
