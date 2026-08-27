package satellite.model;

/**
 * Interface representing the satellite's battery subsystem.
 * 
 * @invariant getChargeLevel() >= MIN_CHARGE_LEVEL && getChargeLevel() <= MAX_CHARGE_LEVEL;
 * @invariant isFull() <==> (getChargeLevel() >= MAX_CHARGE_LEVEL);
 * @invariant isEmpty() <==> (getChargeLevel() <= MIN_CHARGE_LEVEL);
 */
public interface IBattery {
    
    // --- CONSTANTS ---
    double MAX_CHARGE_LEVEL = 100.0;
    double MIN_CHARGE_LEVEL = 0.0;

    // --- QUERIES ---

    /**
     * Returns the current battery charge level as a percentage.
     * @ensures \result >= MIN_CHARGE_LEVEL && \result <= MAX_CHARGE_LEVEL;
     */
    /*@ pure @*/ double getChargeLevel();

    /**
     * Checks if the battery is fully charged.
     */
    /*@ pure @*/ boolean isFull();

    /**
     * Checks if the battery is completely empty.
     */
    /*@ pure @*/ boolean isEmpty();

    // --- COMMANDS ---

    /**
     * Charges the battery by a specific amount.
     * 
     * @requires amount > 0;
     * @requires getChargeLevel() + amount <= MAX_CHARGE_LEVEL;
     * @ensures getChargeLevel() == \old(getChargeLevel()) + amount;
     */
    void charge(double amount);

    /**
     * Discharges the battery by a specific amount.
     * 
     * @requires amount > 0;
     * @requires getChargeLevel() - amount >= MIN_CHARGE_LEVEL;
     * @ensures getChargeLevel() == \old(getChargeLevel()) - amount;
     */
    void discharge(double amount);
}