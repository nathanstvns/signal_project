package com.alerts;

public abstract class AlertFactory {
    public abstract AlertInterface createAlert(String patientId, String condition, long timestamp);
}

