package satellite.model.thermal;

import satellite.model.environment.IEnvironment;

/**
 * Interface representing the Thermal Control System (TCS) of the satellite.
 * Controls internal temperature and manages active heating elements.
 */
public interface IThermalControl {

    /**
     * Returns the current internal temperature of the satellite in degrees Celsius.
     * 
     * @return current temperature in °C
     */
    /*@ pure @*/
    double getTemperature();

    /**
     * Returns true if the active heaters are currently ON.
     * 
     * @return true if heating, false otherwise
     */
    /*@ pure @*/
    boolean isHeaterActive();

    /**
     * Returns true if the thermal control system is active.
     * 
     * @return active state
     */
    /*@ pure @*/
    boolean isActive();

    /**
     * Turns the thermal control system ON or OFF.
     * 
     * @param active desired state
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);

    /**
     * Returns the current power consumption of the thermal control system in Watts.
     * 
     * @return power consumption in W
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getPowerConsumption();

    /**
     * Processes one discrete simulation step for thermal dynamics.
     * Updates internal temperature based on solar radiation and internal heat,
     * and triggers heaters if temperature drops below the safety threshold.
     * 
     * @param env the space environment state
     */
    /*@ requires env != null; @*/
    void processThermal(IEnvironment env);
}
