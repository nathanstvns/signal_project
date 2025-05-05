package com.alerts.factories;

import com.alerts.*;

public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public AlertInterface createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BP: " + condition, timestamp);
    }
}
