package satellite.model.aocs.sensors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import satellite.model.environment.EnvironmentFactory;
import satellite.model.environment.IEnvironment;

class EarthSensorTest {

    private EarthSensor earthSensor;

    @BeforeEach
    void setUp() {
        earthSensor = new EarthSensor();
    }

    @Test
    void testInitialState() {
        assertEquals(SensorType.EARTH_SENSOR, earthSensor.getType());
        assertTrue(earthSensor.isActive());
    }

    @Test
    void testMeasureEarthNominal() {
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment(); // earth alignment = 1.0
        
        double measurement = earthSensor.measureEarth(env);
        assertEquals(1.0, measurement, 0.001);
    }

    @Test
    void testMeasureEarthWhenEarthNotInSight() {
        // Environment where earthInSight = false
        IEnvironment env = EnvironmentFactory.createCustomEnvironment(true, false, false, 0.9, 0.8, 0.01);

        double measurement = earthSensor.measureEarth(env);
        assertEquals(0.0, measurement, 0.001, "Must return 0.0 if Earth is out of sight");
    }

    @Test
    void testMeasureEarthWhenInactive() {
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment();
        earthSensor.setActive(false);

        double measurement = earthSensor.measureEarth(env);
        assertEquals(0.0, measurement, 0.001, "Inactive sensor must return 0.0");
    }

    @Test
    void testPreconditionNullEnvironment() {
        assertThrows(AssertionError.class, () -> earthSensor.measureEarth(null));
    }
}