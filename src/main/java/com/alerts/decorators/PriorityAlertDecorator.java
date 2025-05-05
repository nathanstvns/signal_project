package com.alerts.decorators;

import com.alerts.AlertInterface;

public class PriorityAlertDecorator extends AlertDecorator {
    private int priority;

    public PriorityAlertDecorator(AlertInterface alert, int priority) {
        super(alert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return "[PRIORITY " + priority + "] " + decoratedAlert.getCondition();
    }
}
