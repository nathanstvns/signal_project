package com.alerts.factories;

import com.alerts.*;

public class TriggeredAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "MANUAL: " + condition, timestamp);
    }
}