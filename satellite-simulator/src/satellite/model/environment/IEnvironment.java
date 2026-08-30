package satellite.model.environment;

public interface IEnvironment {

    /**
     * Returns true if the satellite is currently illuminated by the Sun.
     */
    /*@ pure @*/
    boolean isInSunlight();

    /**
     * Returns true if the Earth is currently visible from the satellite.
     */
    /*@ pure @*/
    boolean isEarthInSight();
    
    /**
     * Returns true if the Ground Station is currently visible from the satellite.
     */
    /*@ pure @*/
    boolean isGroundStationInSight() ;

    /**
     * Returns the current simplified Sun alignment level.
     *
     * 0.0 = no alignment / Sun not detectable
     * 1.0 = perfect alignment
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0 && \result <= 1.0; @*/
    double getSunAlignment();

    /**
     * Returns the current simplified Earth alignment level.
     *
     * 0.0 = no alignment / Earth not detectable
     * 1.0 = perfect alignment
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0 && \result <= 1.0; @*/
    double getEarthAlignment();

    /**
     * Returns the simplified natural disturbance affecting the satellite.
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getNaturalDrift();

    /**
     * Updates the environmental state for the next simulation tick.
     */
    void updateState();
}
