package satellite.model.aocs;

import satellite.model.aocs.sensors.IEarthSensor;
import satellite.model.aocs.sensors.ISunSensor;
import satellite.model.environment.IEnvironment;

/**
 * Main facade interface for the AOCS subsystem.
 */
public interface IAOCS {

    IAttitudeControl getAttitudeControl();
    IOrbitControl getOrbitControl();
    IPropulsionSystem getPropulsionSystem();
    ISunSensor getSunSensor();
    IEarthSensor getEarthSensor();

    /**
     * Executes the AOCS sub-routines (Attitude control step and Orbit control step).
     */
    void processAOCS(IEnvironment env);

    /**
     * Returns total power consumption of all active AOCS components.
     */
    double getPowerConsumption();

    /**
     * Returns the attitude alignment error for the OBC FDIR task.
     */
    double getAttitudeAlignmentError();

    /**
     * Checks if attitude pointing is within nominal tolerance.
     */
    boolean isAttitudeStable();
}