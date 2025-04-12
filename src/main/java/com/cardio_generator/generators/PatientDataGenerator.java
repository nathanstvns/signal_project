package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Creates the contract for patient health data.
 *Implementations of this interface are responsible for generating
 * health data for a  patient and sending it to the  output strategy.
 */

public interface PatientDataGenerator {

    /**
     * Generates health data for a patient.
     *This method creates health data for the patient and send it to the provided output strategy.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to output the generated data
     */

    void generate(int patientId, OutputStrategy outputStrategy);
}
