package com.cardio_generator.outputs;

/**
 * Creates the contract for outputting patient health data.
 *Implementations of this interface are responsible for processing
 * and delivering health data for patients
 */

public interface OutputStrategy {
    /**
     * Outputs patient health data.
     *This method processes the provided health data for patient and delivers it using the output strategy.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label a label for the type of data
     * @param data the data to be output
     */
    void output(int patientId, long timestamp, String label, String data);
}
