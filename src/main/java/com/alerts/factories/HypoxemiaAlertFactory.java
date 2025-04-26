package com.alerts.factories;

import com.alerts.*;

public class HypoxemiaAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "URGENT Hypoxemia: " + condition, timestamp);
    }
}
