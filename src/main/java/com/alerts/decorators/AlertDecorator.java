package com.alerts.decorators;

import com.alerts.AlertInterface;

public abstract class AlertDecorator implements AlertInterface {
    protected AlertInterface decoratedAlert;

    public AlertDecorator(AlertInterface alert) {
        this.decoratedAlert = alert;
    }

    @Override
    public String getPatientId() { return decoratedAlert.getPatientId(); }
    @Override
    public String getCondition() { return decoratedAlert.getCondition(); }
    @Override
    public long getTimestamp() { return decoratedAlert.getTimestamp(); }
}
