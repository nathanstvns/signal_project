package com.alerts.factories;

import com.alerts.*;

public class ECGAlertFactory extends AlertFactory {
    @Override
    public AlertInterface createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "ECG: " + condition, timestamp);
    }
}
