package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.alerts.strategies.*;
import java.util.List;
import java.util.ArrayList;
import com.alerts.factories.*;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();
    private AlertFactory bloodOxygenFactory = new BloodOxygenAlertFactory();
    private AlertFactory ecgFactory = new ECGAlertFactory();
    private AlertFactory hypoxemiaFactory = new HypoxemiaAlertFactory();
    private AlertFactory triggeredAlertFactory = new TriggeredAlertFactory();

    private DataStorage dataStorage;
    private List<AlertStrategy> strategies;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.strategies = new ArrayList<>();
        this.strategies.add(new BloodPressureStrategy());
        this.strategies.add(new BloodOxygenStrategy());
        this.strategies.add(new HypoxemiaStrategy());
        this.strategies.add(new ECGStrategy());
        this.strategies.add(new TriggeredAlertStrategy());


    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered. (I implemented this logic in the specific alert strategy classes for each alert)
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : strategies) {
            strategy.checkAlert(patient, this);
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    public void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.println("ALERT: " + alert.getCondition() +
                " for patient " + alert.getPatientId() +
                " at time " + alert.getTimestamp());
    }
    public AlertFactory getBloodPressureFactory() {
        return bloodPressureFactory;
    }

    public AlertFactory getBloodOxygenFactory() {
        return bloodOxygenFactory;
    }

    public AlertFactory getECGFactory() {
        return ecgFactory;
    }

    public AlertFactory getHypoxemiaFactory() {
        return hypoxemiaFactory;
    }

    public AlertFactory getTriggeredAlertFactory() {
        return triggeredAlertFactory;
    }

}
