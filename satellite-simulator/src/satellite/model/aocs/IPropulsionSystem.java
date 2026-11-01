package satellite.model.aocs;

/**
 * Interface representing the physical Propulsion Subsystem of the satellite.
 * Manages propellant mass (kg), thruster firing pulses, and dynamic power states.
 * 
 * @invariant getFuelLevel() >= 0.0
 * @invariant getPowerConsumption() >= 0.0
 * @invariant !isActive() ==> (!isFiring() && getPowerConsumption() == 0.0)
 * 
 * @cons
 * $DESC$ Creates a Propulsion system initialized with a specified fuel mass (kg) in standby state.
 * $ARGS$ double initialFuel
 * $PRE$ initialFuel >= 0.0
 * $POST$ isActive() && !isFiring() && getFuelLevel() == initialFuel && getPowerConsumption() == 5.0
 */

public interface IPropulsionSystem {

    /**
     * Returns true if the propulsion system is turned ON / armed.
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Returns true if thrusters are actively firing during the current tick.
     */
    /*@ pure @*/
    boolean isFiring();

    /**
     * Returns the current fuel level remaining in kilograms (kg).
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getFuelLevel();

    /**
     * Returns the electrical power consumed by heaters and valves (in Watts).
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    /*@ ensures !isActive() ==> \result == 0.0; @*/
    double getPowerConsumption();

    /**
     * Turns the propulsion system ON or OFF.
     */
    /*@ ensures isActive() == active; @*/
    /*@ ensures !isActive() ==> (!isFiring() && getPowerConsumption() == 0.0); @*/
    void setActive(boolean active);

    /**
     * Commands thrusters to fire for orbital correction during the current tick.
     * Consumes fuel and high electrical power.
     * 
     * @param fuelAmount Amount of fuel required for the pulse (in kg)
     */
    /*@ requires isActive();
      @ requires fuelAmount > 0.0;
      @
      @ // If enough fuel is available, fire thrusters and decrease fuel
      @ ensures \old(getFuelLevel()) >= fuelAmount ==> 
      @           (isFiring() && getFuelLevel() == \old(getFuelLevel()) - fuelAmount && getPowerConsumption() == 40.0);
      @
      @ // If fuel is insufficient, thrusters fail to fire
      @ ensures \old(getFuelLevel()) < fuelAmount ==> 
      @           (!isFiring() && getFuelLevel() == \old(getFuelLevel()) && getPowerConsumption() == 5.0);
      @*/
    void fireThrusters(double fuelAmount);

    /**
     * Stops thrusters firing and sets propulsion system back to standby state.
     */
    /*@ requires isActive();
      @ ensures !isFiring();
      @ ensures getPowerConsumption() == 5.0; // Standby heaters consumption
      @*/
    void stopFiring();
}
