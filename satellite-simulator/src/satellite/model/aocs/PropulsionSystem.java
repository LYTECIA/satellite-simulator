package satellite.model.aocs;

    class PropulsionSystem implements IPropulsionSystem {

    private boolean active;
    private boolean firing;
    private double fuelLevel;
    private double powerConsumption;

    private static final double FIRING_POWER = 40.0;  // High power when valves and thrusters are firing
    private static final double STANDBY_POWER = 5.0;  // Low power for tank heaters in standby

    PropulsionSystem(double initialFuel) {
    	if (initialFuel < 0.0) {
            throw new AssertionError("Precondition violated: initialFuel cannot be negative.");
        }
    	
        this.active = true;
        this.firing = false;
        this.fuelLevel =initialFuel;
        this.powerConsumption = STANDBY_POWER;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean isFiring() {
        return this.firing;
    }

    @Override
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    @Override
    public double getPowerConsumption() {
        return this.powerConsumption;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.firing = false;
            this.powerConsumption = 0.0;
        } else {
            this.powerConsumption = STANDBY_POWER;
        }
    }

    @Override
    public void fireThrusters(double fuelAmount) {
    	if (!this.active) {
            throw new AssertionError("Precondition violated: propulsion system must be active to fire thrusters.");
        }
        if (fuelAmount <= 0.0) {
            throw new AssertionError("Precondition violated: fuelAmount must be strictly positive.");
        }

        if (this.fuelLevel >= fuelAmount && fuelAmount > 0.0) {
            this.fuelLevel -= fuelAmount;
            this.firing = true;
            this.powerConsumption = FIRING_POWER;
        } else {
            this.firing = false;
            this.powerConsumption = STANDBY_POWER;
        }
    }


    @Override
    public void stopFiring() {
        if (!this.active) {
            throw new AssertionError("Precondition violated: propulsion system must be active to stop firing.");
        }
        this.firing = false;
        this.powerConsumption = STANDBY_POWER;
    }
}
