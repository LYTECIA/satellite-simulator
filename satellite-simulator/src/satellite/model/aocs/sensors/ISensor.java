package satellite.model.aocs.sensors;

/**
 * Generic interface representing an onboard sensor in the AOCS subsystem.
 * 
 * @invariant getType() != null
 * 
 * @cons
 * $DESC$ Creates a generic sensor, powered and operational by default.
 * $ARGS$ None
 * $PRE$ None
 * $POST$ isActive()
 */

public interface ISensor {

    /**
     * Returns the type of the sensor.
     */
    /*@ pure @*/
    SensorType getType();

    /**
     * Returns true if the sensor is currently active.
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Turns the sensor ON or OFF.
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);
}