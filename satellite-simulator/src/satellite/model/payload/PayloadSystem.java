package satellite.model.payload;

import satellite.model.environment.IEnvironment;

/**
 * Implementation of the Satellite Payload System.
 * Simulates an Earth Observation payload collecting data into memory.
 */
public class PayloadSystem implements IPayloadSystem {

    public static final double POWER_STANDBY = 2.0;    // Idle power draw (W)
    public static final double POWER_OPERATING = 40.0; // Active instrument power draw (W)
    public static final double DATA_GEN_RATE = 10.0;   // Data produced per tick when operating (MB)

    private boolean active;
    private boolean operating;
    private double storedDataSize;

    /**
     * Constructs a PayloadSystem initialized in standby mode with empty data storage.
     */
    public PayloadSystem() {
        this.active = true;
        this.operating = false;
        this.storedDataSize = 0.0;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.operating = false;
        }
    }

    @Override
    public boolean isOperating() {
        return active && operating;
    }

    @Override
    public void setOperating(boolean operating) {
        if (this.active) {
            this.operating = operating;
        }
    }

    @Override
    public double getStoredDataSize() {
        return storedDataSize;
    }

    @Override
    public void clearData(double amountMB) {
        if (amountMB <= 0.0) {
            return;
        }
        this.storedDataSize = Math.max(0.0, this.storedDataSize - amountMB);
    }

    @Override
    public double getPowerConsumption() {
        if (!active) {
            return 0.0;
        }
        return isOperating() ? POWER_OPERATING : POWER_STANDBY;
    }

    @Override
    public void processPayload(IEnvironment env) {
        if (!active) {
            return;
        }

        if (isOperating() && env.isEarthInSight()) {
            this.storedDataSize += DATA_GEN_RATE;
        }
    }
}