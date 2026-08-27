package satellite.model.aocs.sensors;

import satellite.model.IEnvironment;

public interface IEarthSensor extends ISensor {

    /**
     * Returns the current simplified Earth alignment measured by the sensor.
     *
     * Returns 0.0 if the sensor is inactive
     * or if the Earth is not visible.
     */
    /*@ pure @*/
    /*@ requires env != null;
      @ ensures !isActive() ==> \result == 0.0;
      @ ensures (isActive() && !env.isEarthInSight()) ==> \result == 0.0;
      @ ensures \result >= 0.0 && \result <= 1.0;
      @*/
    double measureEarth(IEnvironment env);
}
