package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ECGStrategy implements AlertStrategy {
    private static final String ECG = "ECG";
    private static final int WINDOW_SIZE = 10; // Number of readings to consider for the sliding window
    private static final double THRESHOLD_MULTIPLIER = 1.5; // Multiplier to detect significant peaks

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> ecgRecords = new ArrayList<>();

        // Collect all ECG records
        for (PatientRecord record : patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
            if (ECG.equals(record.getRecordType())) {
                ecgRecords.add(record);
            }
        }

        // Sort by timestamp
        ecgRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        // Check for abnormal peaks using sliding window
        checkAbnormalPeaks(ecgRecords, patient.getPatientId(), generator);
    }

    private void checkAbnormalPeaks(List<PatientRecord> records, int patientId, AlertGenerator generator) {
        // Need at least WINDOW_SIZE+1 records to detect peaks beyond the average
        if (records.size() <= WINDOW_SIZE) {
            return;
        }

        for (int i = WINDOW_SIZE; i < records.size(); i++) {
            // Calculate the average of the previous WINDOW_SIZE readings
            double sum = 0;
            for (int j = i - WINDOW_SIZE; j < i; j++) {
                sum += records.get(j).getMeasurementValue();
            }
            double average = sum / WINDOW_SIZE;

            // Check if current reading is significantly higher than the average
            PatientRecord current = records.get(i);
            double currentValue = current.getMeasurementValue();

            if (currentValue > average * THRESHOLD_MULTIPLIER) {
                generator.triggerAlert(
                        generator.getECGFactory().createAlert(
                                String.valueOf(patientId),
                                String.format("Abnormal ECG Peak: %.1f (%.1f times above average of %.1f)",
                                        currentValue,
                                        currentValue / average,
                                        average),
                                current.getTimestamp()
                        )
                );
            }
        }
    }
}
