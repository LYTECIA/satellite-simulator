package satellite.model.aocs.sensors;

import satellite.model.IEnvironment;

public interface ISunSensor extends ISensor {

    /**
     * Returns the current simplified Sun alignment measured by the sensor.
     *
     * Returns 0.0 if the sensor is inactive
     * or if the satellite is not illuminated by the Sun.
     */
    /*@ pure @*/
    /*@ requires env != null;
      @ ensures !isActive() ==> \result == 0.0;
      @ ensures (isActive() && !env.isInSunlight()) ==> \result == 0.0;
      @ ensures \result >= 0.0 && \result <= 1.0;
      @*/
    double measureSun(IEnvironment env);
}