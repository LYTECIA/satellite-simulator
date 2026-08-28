package satellite.model.aocs;

import satellite.model.aocs.sensors.IEarthSensor;
import satellite.model.aocs.sensors.ISunSensor;
import satellite.model.environment.IEnvironment;

public interface IAttitudeControl {

    /**
     * Returns true if the satellite is currently correctly aligned.
     */
    /*@ pure @*/
    boolean isAligned();

    /**
     * Returns true if the attitude control system is active.
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Returns the attitude alignment error calculated during the last tick.
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getAlignmentError();

    /**
     * Returns the power consumed by the attitude control system
     * during the last simulation tick.
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getPowerConsumption();
    
    double getCurrentSunAlignment();

    double getCurrentEarthAlignment();

    /**
     * Turns the attitude control system ON or OFF.
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);

    /**
     * Processes one discrete simulation tick.
     *
     * The attitude control system reads the sensors,
     * compares the measured orientation with the desired orientation,
     * and performs a correction if necessary.
     */
    /*@ requires sunSensor != null;
      @ requires earthSensor != null;
      @ requires env != null;
      @*/
    void processAttitude(
        ISunSensor sunSensor,
        IEarthSensor earthSensor,
        IEnvironment env
    );
}
