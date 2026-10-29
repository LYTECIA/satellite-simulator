package satellite.model.power;

/**
 * Interface representing the central Power System Controller.
 * 
 * @invariant getBattery() != null && getSolarPanel() != null
 * @invariant getGrantedEnergy() >= 0.0
 * @invariant getGrantedEnergy() <= IBattery.MAX_CHARGE_LEVEL + getSolarPanel().getEnergyProduction()
 * 
 * @cons
 * $DESC$ Creates a Power System Controller monitoring a battery and a solar panel.
 * $ARGS$ IBattery battery, ISolarPanel solarPanel
 * $PRE$ battery != null && solarPanel != null
 * $POST$ getBattery() == battery && getSolarPanel() == solarPanel && getNetFlow() == 0.0 && getGrantedEnergy() == 0.0
 */
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