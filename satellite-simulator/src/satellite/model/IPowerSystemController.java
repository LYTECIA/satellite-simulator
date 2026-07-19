package satellite.model;

/**
 * Interface representing the central Power System Controller.
 */
/*@ public invariant 
@   getBattery() != null && 
@   getSolarPanel() != null &&
@   getGrantedEnergy() >= 0.0 &&
@   getGrantedEnergy() <= IBattery.MAX_CHARGE_LEVEL + getSolarPanel().getEnergyProduction();
@*/
public interface IPowerSystemController {

    // --- QUERIES ---

    /**
     * Returns the monitored battery unit.
     */
    /*@ pure @*/ IBattery getBattery();

    /**
     * Returns the monitored solar panel unit.
     */
    /*@ pure @*/ ISolarPanel getSolarPanel();

    /**
     * Returns the net energy flow (positive if surplus, negative if drawing from battery).
     */
    /*@ pure @*/ double getNetFlow();

    /**
     * Returns the energy actually granted to the subsystems during the last cycle.
     */
    /*@ pure @*/ double getGrantedEnergy();

    // --- COMMANDS ---

    /**
     * Processes the energy balance for the current simulation tick.
     * Updates the net flow, battery state, and the granted energy registry.
     * 
     * @requires totalConsumption >= 0.0;
     * @ensures getGrantedEnergy() <= totalConsumption;
     */
    void processEnergyBalance(double totalConsumption);
}