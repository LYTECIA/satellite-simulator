package satellite.model;

/**
 * Concrete implementation of the satellite's solar panel subsystem.
 * Handles deployment mechanics and energy production tracking.
 */
public class SolarPanel implements ISolarPanel {

    // --- ATTRIBUTES ---
    private boolean deployed;
    private double energyProduction;

    // --- CONSTRUCTOR ---

    /**
     * Initializes the solar panel in its launch configuration.
     * Stowed (folded) and producing no energy.
     */
    public SolarPanel() {
        this.deployed = false;
        this.energyProduction = MIN_ENERGY;
    }

    // --- QUERIES ---

    @Override
    public boolean isDeployed() {
        return this.deployed;
    }

    @Override
    public boolean isFunctional() {
        // Active only if deployed AND capturing positive sun energy
        return this.deployed && this.energyProduction > MIN_ENERGY;
    }

    @Override
    public double getEnergyProduction() {
        return this.energyProduction;
    }

    // --- COMMANDS ---

    @Override
    public void deploy() {
        if (this.deployed) {
            throw new AssertionError("Precondition violated: Solar panel is already deployed.");
        }
        this.deployed = true;
    }

    @Override
    public void updateProduction(double sunEnergy) {
        if (sunEnergy < MIN_ENERGY) {
            throw new AssertionError("Precondition violated: sunEnergy cannot be negative.");
        }

        if (this.deployed) {
            this.energyProduction = sunEnergy;
        } else {
            // Safety fallback: if stowed, energy production remains locked at MIN_ENERGY
            this.energyProduction = MIN_ENERGY;
        }
    }
}