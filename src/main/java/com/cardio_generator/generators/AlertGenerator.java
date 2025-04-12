package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert data for patients.
 *
 * <p>This class implements the {@link PatientDataGenerator} interface and is responsible
 * for generating alerts for patients.
 */

public class AlertGenerator implements PatientDataGenerator {

    /**
     * A random number generator used to simulate alert probabilities.
     */
    public static final Random randomGenerator = new Random();
    // Changed variable name to camelCase
    /**
     * Has track of the current alert state for each patient
     */
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator
     *
     * <p>Each patient's initial alert state is set to ({@code false}).
     *
     * @param patientCount the number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }


    /**
     * Generates simulated alert data for a specific patient.
     *
     * <p>If an alert is already active for the patient, there is a 90% chancce that it will
     * be resolved. If there is no alert already active, there is a small probability (based on a lambda value) that
     * a new alert will be triggered. The generated alert data is sent to the output strategy.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to output the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to camelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
