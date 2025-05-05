package com.alerts.decorators;

import com.alerts.AlertInterface;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;

    public RepeatedAlertDecorator(AlertInterface alert, int repeatCount) {
        super(alert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " (repeated " + repeatCount + "x)";
    }
}
