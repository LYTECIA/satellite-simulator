package satellite.model.thermal;

import satellite.model.environment.IEnvironment;

/**
 * Implementation of the satellite Thermal Control System.
 */
public class ThermalControl implements IThermalControl {

    // Thresholds and operational parameters
    public static final double MIN_SAFE_TEMP = 5.0;     // Below this, heaters turn ON (°C)
    public static final double TARGET_TEMP = 20.0;      // Nominal target temperature (°C)
    public static final double HEATING_RATE = 1.5;      // Heat added per tick by heaters (°C)
    public static final double SOLAR_HEATING = 0.8;     // Heat added per tick in sunlight (°C)
    public static final double SHADOW_COOLING = 1.0;    // Heat lost per tick in shadow (°C)

    public static final double POWER_STANDBY = 1.0;     // Power when idle (W)
    public static final double POWER_HEATER = 25.0;     // Power when heating (W)

    private double currentTemperature;
    private boolean heaterActive;
    private boolean active;

    /**
     * Constructs a ThermalControl system with an initial temperature.
     * 
     * @param initialTemperature Starting temperature in °C
     */
    public ThermalControl(double initialTemperature) {
        this.currentTemperature = initialTemperature;
        this.heaterActive = false;
        this.active = true;
    }

    @Override
    public double getTemperature() {
        return currentTemperature;
    }

    @Override
    public boolean isHeaterActive() {
        return heaterActive;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.heaterActive = false;
        }
    }

    @Override
    public double getPowerConsumption() {
        if (!active) {
            return 0.0;
        }
        return heaterActive ? POWER_HEATER : POWER_STANDBY;
    }

    @Override
    public void processThermal(IEnvironment env) {
        if (!active) {
            return;
        }

        if (env.isInSunlight()) {
            currentTemperature += SOLAR_HEATER_EFFECT(env);
        } else {
            currentTemperature -= SHADOW_COOLING;
        }

        if (currentTemperature < MIN_SAFE_TEMP) {
            heaterActive = true;
        } else if (currentTemperature >= TARGET_TEMP) {
            heaterActive = false;
        }

        if (heaterActive) {
            currentTemperature += HEATING_RATE;
        }
    }

    
    
    
    
    
    private double SOLAR_HEATER_EFFECT(IEnvironment env) {
        return env.isInSunlight() ? SOLAR_HEATING : 0.0;
    }
}