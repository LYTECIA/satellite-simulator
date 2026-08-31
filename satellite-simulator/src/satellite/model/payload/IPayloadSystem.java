package satellite.model.payload;

import satellite.model.environment.IEnvironment;

/**
 * Interface representing the Satellite Payload System.
 * Responsible for mission data acquisition (e.g., Earth observation camera/sensors).
 */
public interface IPayloadSystem {

    /**
     * Checks if the payload subsystem is powered ON.
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Powers ON or OFF the payload subsystem.
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);

    /**
     * Checks if the payload is currently capturing/processing mission data.
     */
    /*@ pure @*/
    boolean isOperating();

    /**
     * Sets whether the payload should actively acquire data during valid conditions.
     */
    /*@ ensures isOperating() == operating; @*/
    void setOperating(boolean operating);

    /**
     * Returns the total payload mission data currently stored in memory (in MB).
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getStoredDataSize();

    /**
     * Clears or offloads a specific amount of payload data from memory.
     * 
     * @param amountMB Data amount in MB to remove
     */
    /*@ requires amountMB >= 0.0; @*/
    void clearData(double amountMB);

    /**
     * Returns the current power consumption of the payload system in Watts.
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getPowerConsumption();

    /**
     * Processes one simulation tick for mission data acquisition.
     * 
     * @param env the current space environment state
     */
    /*@ requires env != null; @*/
    void processPayload(IEnvironment env);
}