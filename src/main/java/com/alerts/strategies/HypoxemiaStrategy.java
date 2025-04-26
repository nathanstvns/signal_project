package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HypoxemiaStrategy implements AlertStrategy {
    private static final String BLOOD_OXYGEN = "BloodOxygen";
    private static final String SYSTOLIC = "BloodPressure_Systolic";
    private static final double LOW_OXYGEN_THRESHOLD = 92.0;
    private static final double LOW_SYSTOLIC_THRESHOLD = 90.0;

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> oxygenRecords = new ArrayList<>();
        List<PatientRecord> systolicRecords = new ArrayList<>();

        for (PatientRecord record : patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
            if (BLOOD_OXYGEN.equals(record.getRecordType())) {
                oxygenRecords.add(record);
            } else if (SYSTOLIC.equals(record.getRecordType())) {
                systolicRecords.add(record);
            }
        }

        // Sort
        oxygenRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        systolicRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        checkHypotensiveHypoxemia(oxygenRecords, systolicRecords, patient.getPatientId(), generator);
    }

    private void checkHypotensiveHypoxemia(List<PatientRecord> oxygenRecords, List<PatientRecord> systolicRecords, int patientId, AlertGenerator generator) {
        if (oxygenRecords.isEmpty() || systolicRecords.isEmpty()) {
            return;
        }

        // Get the most recent measurements
        PatientRecord latestOxygen = oxygenRecords.get(oxygenRecords.size() - 1);
        PatientRecord latestSystolic = systolicRecords.get(systolicRecords.size() - 1);

        // Check for hypoxemia

        if (latestOxygen.getMeasurementValue() < LOW_OXYGEN_THRESHOLD &&
                latestSystolic.getMeasurementValue() < LOW_SYSTOLIC_THRESHOLD) {

            // Use the most recent timestamp between the two measurements
            long timestamp = Math.max(latestOxygen.getTimestamp(), latestSystolic.getTimestamp());

            generator.triggerAlert(
                    generator.getHypoxemiaFactory().createAlert(
                            String.valueOf(patientId),
                            String.format("Hypotensive Hypoxemia: Blood Oxygen %.1f%% and Systolic BP %.1f mmHg",
                                    latestOxygen.getMeasurementValue(),
                                    latestSystolic.getMeasurementValue()),
                            timestamp
                    )
            );
        }
    }
}
