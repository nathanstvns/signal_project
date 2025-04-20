package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BloodOxygenStrategy implements AlertStrategy {
    private static final String BLOOD_OXYGEN = "BloodOxygen";
    private static final double LOW_OXYGEN_THRESHOLD = 92.0;
    private static final double RAPID_DROP_THRESHOLD = 5.0;
    private static final long TEN_MINUTES_MS = 600_000;

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> oxygenRecords = new ArrayList<>();

        // Collect all blood oxygen records
        for (PatientRecord record : patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
            if (BLOOD_OXYGEN.equals(record.getRecordType())) {
                oxygenRecords.add(record);
            }
        }

        // Sort by timestamp
        oxygenRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        // Check for low oxygen levels
        checkLowOxygen(oxygenRecords, patient.getPatientId(), generator);

        // Check for rapid drops
        checkRapidDrop(oxygenRecords, patient.getPatientId(), generator);
    }

    private void checkLowOxygen(List<PatientRecord> records, int patientId, AlertGenerator generator) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            if (value < LOW_OXYGEN_THRESHOLD) {
                generator.triggerAlert(new Alert(
                        String.valueOf(patientId),
                        "Low Blood Oxygen: " + value + "%",
                        record.getTimestamp()
                ));
            }
        }
    }

    private void checkRapidDrop(List<PatientRecord> records, int patientId, AlertGenerator generator) {
        // Need 2 records to calc decrease
        if (records.size() < 2) {
            return;
        }

        for (int i = 0; i < records.size() - 1; i++) {
            PatientRecord current = records.get(i);

            // Check all other records within 10 minutes (600ms(
            for (int j = i + 1; j < records.size(); j++) {
                PatientRecord next = records.get(j);
                long timeDiff = next.getTimestamp() - current.getTimestamp();

                // Only check records within 10 minutes of the current record
                if (timeDiff <= TEN_MINUTES_MS) {
                    double valueDiff = current.getMeasurementValue() - next.getMeasurementValue();

                    // Check if there's a drop < 5 %
                    if (valueDiff >= RAPID_DROP_THRESHOLD) {
                        generator.triggerAlert(new Alert(
                                String.valueOf(patientId),
                                String.format("Rapid Blood Oxygen Drop: %.1f%% → %.1f%% (%.1f%% drop in %d seconds)",
                                        current.getMeasurementValue(),
                                        next.getMeasurementValue(),
                                        valueDiff,
                                        timeDiff / 1000),
                                next.getTimestamp()
                        ));
                        break;
                    }
                } else {
                    //10 mins are over
                    break;
                }
            }
        }
    }
}
