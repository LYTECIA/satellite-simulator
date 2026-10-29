package satellite.model.power;

/**
 * Factory class responsible for instantiating Power System components.
 * Hides concrete implementations behind their public interfaces.
 */
public final class PowerFactory {

    /**
     * Private constructor to prevent instantiation of utility factory class.
     */
    private PowerFactory() {
        // Utility class
    }

    /**
     * Creates a default battery initialized at full capacity (100.0 W).
     */
    public static IBattery createBattery() {
        return new Battery();
    }

    /**
     * Creates a default solar panel initialized in stowed launch configuration.
     */
    public static ISolarPanel createSolarPanel() {
        return new SolarPanel();
    }

    /**
     * Creates a central Power System Controller with new default battery and solar panel.
     */
    public static IPowerSystemController createPowerSystemController() {
        return new PowerSystemController(createBattery(), createSolarPanel());
    }

    /**
     * Creates a Power System Controller with specific battery and solar panel instances.
     * 
     * @requires battery != null && solarPanel != null;
     */
    public static IPowerSystemController createPowerSystemController(IBattery battery, ISolarPanel solarPanel) {
        if (battery == null || solarPanel == null) {
            throw new AssertionError("Precondition violated: components cannot be null.");
        }
        return new PowerSystemController(battery, solarPanel);
    }
}