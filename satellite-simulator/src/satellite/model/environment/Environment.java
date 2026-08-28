package satellite.model.environment;

public class Environment implements IEnvironment {

    private boolean inSunlight;
    private boolean earthInSight;

    /*
     * Simplified current alignment values.
     *
     * 0.0 = no alignment
     * 1.0 = perfect alignment
     */
    private double sunAlignment;
    private double earthAlignment;

    /*
     * Simplified environmental disturbance.
     */
    private double naturalDrift;


    public Environment(
            boolean inSunlight,
            boolean earthInSight,
            double sunAlignment,
            double earthAlignment,
            double naturalDrift) {

        this.inSunlight = inSunlight;
        this.earthInSight = earthInSight;
        this.sunAlignment = sunAlignment;
        this.earthAlignment = earthAlignment;
        this.naturalDrift = naturalDrift;
    }


    @Override
    public boolean isInSunlight() {
        return this.inSunlight;
    }


    @Override
    public boolean isEarthInSight() {
        return this.earthInSight;
    }


    @Override
    public double getSunAlignment() {
        return this.sunAlignment;
    }


    @Override
    public double getEarthAlignment() {
        return this.earthAlignment;
    }


    @Override
    public double getNaturalDrift() {
        return this.naturalDrift;
    }


    @Override
    public void updateState() {
        /*
         * The environment will be updated here at each simulation tick.
         *
         * For now, no physical model is implemented.
         */
    }
}