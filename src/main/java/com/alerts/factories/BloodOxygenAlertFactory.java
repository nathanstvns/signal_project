package com.alerts.factories;

import com.alerts.*;

public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "O2: " + condition, timestamp);
    }
}