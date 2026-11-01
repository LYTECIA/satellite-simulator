package satellite.model.environment;

/**
 * Factory class responsible for instantiating Space Environment models.
 * Encapsulates concrete Environment instantiation behind the IEnvironment interface.
 */
public final class EnvironmentFactory {

   
    private EnvironmentFactory() {
        // Utility class
    }

    /**
     * Creates a default nominal space environment (In sunlight, Earth in sight, perfect alignment, low drift).
     */
    public static IEnvironment createDefaultEnvironment() {
        return new Environment(true, true, true, 1.0, 1.0, 0.01);
    }

    /**
     * Creates an eclipse space environment (In Earth's shadow, zero sun alignment).
     */
    public static IEnvironment createEclipseEnvironment() {
        return new Environment(false, true, true, 0.0, 1.0, 0.01);
    }

    /**
     * Creates a custom space environment configuration.
     * 
     * @requires sunAlignment >= 0.0 && sunAlignment <= 1.0;
     * @requires earthAlignment >= 0.0 && earthAlignment <= 1.0;
     * @requires naturalDrift >= 0.0;
     */
    public static IEnvironment createCustomEnvironment(
            boolean inSunlight,
            boolean earthInSight,
            boolean groundStationInSight,
            double sunAlignment,
            double earthAlignment,
            double naturalDrift) {

        return new Environment(
                inSunlight,
                earthInSight,
                groundStationInSight,
                sunAlignment,
                earthAlignment,
                naturalDrift);
    }
}