package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public class TriggeredAlertStrategy implements AlertStrategy {
    private static final String TRIGGERED_ALERT = "TriggeredAlert";

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);
        for (PatientRecord record : records) {
            if (TRIGGERED_ALERT.equals(record.getRecordType())) {
                generator.triggerAlert(
                        generator.getTriggeredAlertFactory().createAlert(
                                String.valueOf(patient.getPatientId()),
                                "Triggered Alert: " + record.getMeasurementValue(),
                                record.getTimestamp()
                        )
                );
            }
        }
    }
}
