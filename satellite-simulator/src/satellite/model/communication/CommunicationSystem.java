package satellite.model.communication;

import satellite.model.environment.IEnvironment;

/**
 * Implementation of the Satellite Communication System (TT&C).
 * Manages ground station visibility, command buffering, and power consumption.
 */
public class CommunicationSystem implements ICommunicationSystem {

    public static final double POWER_STANDBY = 1.0;     // Power consumption when listening (W)
    public static final double POWER_TRANSMIT = 20.0;   // Power consumption when transmitting (W)

    private boolean active;
    private boolean communicationAvailable;
    private boolean transmitting;
    private String lastReceivedCommand;

    /**
     * Constructs a CommunicationSystem initialized in standby mode.
     */
    public CommunicationSystem() {
        this.active = true;
        this.communicationAvailable = false;
        this.transmitting = false;
        this.lastReceivedCommand = "NONE";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.communicationAvailable = false;
            this.transmitting = false;
        }
    }

    @Override
    public boolean isCommunicationAvailable() {
        return active && communicationAvailable;
    }

    @Override
    public void receiveCommand(String command) {
        if (!isCommunicationAvailable() || command == null) {
            return;
        }
        this.lastReceivedCommand = command;
    }

    @Override
    public String getLastReceivedCommand() {
        return lastReceivedCommand;
    }

    @Override
    public void sendData(String data) {
        if (!isCommunicationAvailable() || data == null) {
            return;
        }
        // Active radio downlink simulation
        this.transmitting = true;
    }

    @Override
    public double getPowerConsumption() {
        if (!active) {
            return 0.0;
        }
        return transmitting ? POWER_TRANSMIT : POWER_STANDBY;
    }

    @Override
    public void processCommunication(IEnvironment env) {

        if (!active) {
            return;
        }

        // Start of a new simulation tick:
        // no transmission is active by default.
        this.transmitting = false;

        // Check whether communication with the ground station
        // is currently possible.
        this.communicationAvailable =
                env.isGroundStationInSight();
    }
    
}