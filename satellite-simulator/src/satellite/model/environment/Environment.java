package satellite.model.environment;

class Environment implements IEnvironment {

    private boolean inSunlight;
    private boolean earthInSight;
    private boolean groundStationInSight;
    private double sunAlignment;
    private double earthAlignment;
    private double naturalDrift;

    // --- SIMULATION TICK COUNTER & CONSTANTS ---
    private int tickCounter;
    private static final int ECLIPSE_PERIOD_TICKS = 50; // Switches sunlight state every 50 ticks
    
    
    public Environment(
            boolean inSunlight,
            boolean earthInSight,
            boolean groundStationInSight,
            double sunAlignment,
            double earthAlignment,
            double naturalDrift) {
    	
    	
    	if (sunAlignment < 0.0 || sunAlignment > 1.0) {
            throw new AssertionError("Precondition violated: sunAlignment must be between 0.0 and 1.0.");
        }
        if (earthAlignment < 0.0 || earthAlignment > 1.0) {
            throw new AssertionError("Precondition violated: earthAlignment must be between 0.0 and 1.0.");
        }
        if (naturalDrift < 0.0) {
            throw new AssertionError("Precondition violated: naturalDrift cannot be negative.");
        }

        this.inSunlight = inSunlight;
        this.earthInSight = earthInSight;
        this.groundStationInSight = groundStationInSight;
        this.sunAlignment = sunAlignment;
        this.earthAlignment = earthAlignment;
        this.naturalDrift = naturalDrift;
    }


    @Override
    public boolean isInSunlight() {
        return this.inSunlight;
    }


    @Override
    public boolean isEarthInSight() {
        return this.earthInSight;
    }
    
    @Override
    public boolean isGroundStationInSight() {
        return groundStationInSight;
    }


    @Override
    public double getSunAlignment() {
        return this.sunAlignment;
    }


    @Override
    public double getEarthAlignment() {
        return this.earthAlignment;
    }


    @Override
    public double getNaturalDrift() {
        return this.naturalDrift;
    }


    @Override
    public void updateState() {
        this.tickCounter++;

        // 1. Simulate Day/Night orbit cycle (e.g., toggles sunlight state every 50 ticks)
        if (this.tickCounter % ECLIPSE_PERIOD_TICKS == 0) {
            this.inSunlight = !this.inSunlight;
        }

        // 2. Apply natural environmental drift to alignments (degradation over time)
        if (this.inSunlight) {
            this.sunAlignment = Math.max(0.0, this.sunAlignment - this.naturalDrift);
        } else {
            this.sunAlignment = 0.0; // Locked to 0.0 during eclipse
        }

        if (this.earthInSight) {
            this.earthAlignment = Math.max(0.0, this.earthAlignment - (this.naturalDrift * 0.5));
        }
    }
}