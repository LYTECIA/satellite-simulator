package satellite.model;

/**
 * Interface representing the satellite's solar panel subsystem.
 * 
 * @invariant getEnergyProduction() >= MIN_ENERGY;
 * @invariant !isDeployed() ==> getEnergyProduction() == MIN_ENERGY;
 * @invariant isFunctional() <==> (isDeployed() && getEnergyProduction() > MIN_ENERGY);
 */
public interface ISolarPanel {
    
    // --- CONSTANTS ---
    double MIN_ENERGY = 0.0;
    
    // --- QUERIES ---
    
    /**
     * Checks if the solar panel is deployed.
     */
    /*@ pure @*/ boolean isDeployed();
    
    /**
     * Checks if the solar panel is actively functional (deployed and producing energy).
     */
    /*@ pure @*/ boolean isFunctional();
    
    /**
     * Returns the current energy production rate.
     */
    /*@ pure @*/ double getEnergyProduction();
    
    // --- COMMANDS ---
    
    /**
     * Deploys the solar panel.
     * 
     * @requires !isDeployed();
     * @ensures isDeployed();
     */
    void deploy();
    
    /**
     * Updates the production based on the sun energy received from the environment.
     * 
     * @requires sunEnergy >= MIN_ENERGY;
     * @ensures isDeployed() ==> getEnergyProduction() == sunEnergy;
     * @ensures !isDeployed() ==> getEnergyProduction() == MIN_ENERGY;
     */
    void updateProduction(double sunEnergy);
}    