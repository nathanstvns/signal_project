package com.alerts.factories;

import com.alerts.*;

public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public AlertInterface createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "O2: " + condition, timestamp);
    }
}