package com.alerts.factories;

import com.alerts.*;

public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BP: " + condition, timestamp);
    }
}
