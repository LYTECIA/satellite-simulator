package satellite.model.aocs.sensors;

/**
 * Factory class responsible for instantiating AOCS Sensor components.
 */
public final class SensorFactory {

    private SensorFactory() {
        // Utility class
    }

    /**
     * Creates a default active Sun Sensor.
     */
    public static ISunSensor createSunSensor() {
        return new SunSensor();
    }

    /**
     * Creates a default active Earth Sensor.
     */
    public static IEarthSensor createEarthSensor() {
        return new EarthSensor();
    }
}