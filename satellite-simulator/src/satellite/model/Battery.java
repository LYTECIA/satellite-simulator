package satellite.model;

/**
 * Concrete implementation of the satellite battery.
 * Includes defensive checks and mathematical clamping to prevent floating-point drift.
 */
public class Battery implements IBattery {
    
    // --- ATTRIBUTES ---
    private double chargeLevel;

    // --- CONSTRUCTOR ---
    
    /**
     * Initializes the battery at full capacity.
     */
    public Battery() {
        this.chargeLevel = MAX_CHARGE_LEVEL;
    }

    // --- QUERIES ---

    @Override
    public double getChargeLevel() {
        return this.chargeLevel;
    }

    @Override
    public boolean isFull() {
        // Robust boundary check to accommodate potential double precision noise
        return this.chargeLevel >= MAX_CHARGE_LEVEL;
    }

    @Override
    public boolean isEmpty() {
        // Robust boundary check to accommodate potential double precision noise
        return this.chargeLevel <= MIN_CHARGE_LEVEL;
    }

    // --- COMMANDS ---

    @Override
    public void charge(double amount) {
        if (amount <= 0) {
            throw new AssertionError("Precondition violated: amount must be strictly positive.");
        }
        if (this.chargeLevel + amount > MAX_CHARGE_LEVEL) {
            throw new AssertionError("Precondition violated: cannot overcharge past maximum capacity.");
        }
        
        // Clamping calculation to prevent IEEE 754 precision drift over long simulation cycles
        this.chargeLevel = Math.min(this.chargeLevel + amount, MAX_CHARGE_LEVEL);
    }

    @Override
    public void discharge(double amount) {
        if (amount <= 0) {
            throw new AssertionError("Precondition violated: amount must be strictly positive.");
        }
        if (this.chargeLevel - amount < MIN_CHARGE_LEVEL) {
            throw new AssertionError("Precondition violated: battery cannot be drained below minimum capacity.");
        }
        
        // Clamping calculation to prevent IEEE 754 precision drift over long simulation cycles
        this.chargeLevel = Math.max(this.chargeLevel - amount, MIN_CHARGE_LEVEL);
    }
}