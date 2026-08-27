package satellite.model.aocs.sensors;

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