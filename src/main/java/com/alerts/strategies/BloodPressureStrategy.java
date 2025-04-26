package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        //collect the patient data for systolic and diastolic blood pressure
        List<PatientRecord> systolic = new ArrayList<>();
        List<PatientRecord> diastolic = new ArrayList<>();

        for (PatientRecord record : patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
            switch (record.getRecordType()) {
                case "BloodPressure_Systolic":
                    systolic.add(record);
                    break;
                case "BloodPressure_Diastolic":
                    diastolic.add(record);
                    break;
            }
        }

        // critical threshold checks
        checkCriticalThreshold(systolic, "Systolic", 90, 180, patient.getPatientId(), generator);
        checkCriticalThreshold(diastolic, "Diastolic", 60, 120, patient.getPatientId(), generator);

        // Trend checks
        checkTrend(systolic, "Systolic", patient.getPatientId(), generator);
        checkTrend(diastolic, "Diastolic", patient.getPatientId(), generator);
    }

    private void checkCriticalThreshold(List<PatientRecord> records, String type, double min, double max, int patientId, AlertGenerator generator) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            if (value > max || value < min) {
                generator.triggerAlert(
                        generator.getBloodPressureFactory().createAlert(
                                String.valueOf(patientId),
                                "Critical " + type + " Blood Pressure: " + value,
                                record.getTimestamp()
                        )
                );
            }
        }
    }

    private void checkTrend(List<PatientRecord> records, String type, int patientId, AlertGenerator generator) {
        // sort records by timestamp to ensure chronological order
        records.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));

        // Check each set of three consecutive records
        for (int i = 0; i < records.size() - 2; i++) {
            PatientRecord r1 = records.get(i);
            PatientRecord r2 = records.get(i + 1);
            PatientRecord r3 = records.get(i + 2);

            double v1 = r1.getMeasurementValue();
            double v2 = r2.getMeasurementValue();
            double v3 = r3.getMeasurementValue();

            // Check for increasing trend (each value > previous by more than 10)
            if ((v2 - v1 > 10) && (v3 - v2 > 10)) {
                String condition = String.format(
                        "Increasing %s Blood Pressure Trend: %.1f → %.1f → %.1f",
                        type, v1, v2, v3
                );
                generator.triggerAlert(
                        generator.getBloodPressureFactory().createAlert(
                                String.valueOf(patientId),
                                condition,
                                r3.getTimestamp()
                        )
                );
            }
            // Check for decreasing trend (each value < previous by more than 10)
            else if ((v1 - v2 > 10) && (v2 - v3 > 10)) {
                String condition = String.format(
                        "Decreasing %s Blood Pressure Trend: %.1f → %.1f → %.1f",
                        type, v1, v2, v3
                );
                generator.triggerAlert(
                        generator.getBloodPressureFactory().createAlert(
                                String.valueOf(patientId),
                                condition,
                                r3.getTimestamp()
                        )
                );
            }
        }
    }

}
