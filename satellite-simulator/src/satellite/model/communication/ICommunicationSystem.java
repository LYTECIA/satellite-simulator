package satellite.model.communication;

import satellite.model.environment.IEnvironment;

/**
 * Interface representing the Satellite Communication System (TT&C).
 * Handles command uplink, telemetry downlink, and radio power states.
 */
public interface ICommunicationSystem {

    /**
     * Checks if the communication system is powered ON.
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Sets the active state of the communication system.
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);

    /**
     * Checks if a radio link with the ground station is currently available.
     */
    /*@ pure @*/
    boolean isCommunicationAvailable();

    /**
     * Receives a command sent from the ground station.
     */
    /*@ requires command != null; @*/
    void receiveCommand(String command);

    /**
     * Returns the last command received from the ground station.
     */
    /*@ pure @*/
    String getLastReceivedCommand();

    /**
     * Transmits data or telemetry to the ground station.
     */
    /*@ requires data != null; @*/
    void sendData(String data);

    /**
     * Returns the current power consumption in Watts.
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getPowerConsumption();

    /**
     * Updates the communication subsystem state based on the environment.
     */
    /*@ requires env != null; @*/
    void processCommunication(IEnvironment env);
}
