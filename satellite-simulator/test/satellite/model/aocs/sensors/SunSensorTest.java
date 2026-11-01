package satellite.model.aocs.sensors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import satellite.model.environment.EnvironmentFactory;
import satellite.model.environment.IEnvironment;

class SunSensorTest {

    private SunSensor sunSensor;

    @BeforeEach
    void setUp() {
        sunSensor = new SunSensor();
    }

    @Test
    void testInitialState() {
        assertEquals(SensorType.SUN_SENSOR, sunSensor.getType());
        assertTrue(sunSensor.isActive());
    }

    @Test
    void testSetActive() {
        sunSensor.setActive(false);
        assertFalse(sunSensor.isActive());
        
        sunSensor.setActive(true);
        assertTrue(sunSensor.isActive());
    }

    @Test
    void testMeasureSunNominalWithDrift() {
        // Environment: in sunlight, sun alignment = 0.9, drift = 0.05
        IEnvironment env = EnvironmentFactory.createCustomEnvironment(true, true, true, 0.9, 0.8, 0.05);

        // Measured value = 0.9 - 0.05 = 0.85
        double measurement = sunSensor.measureSun(env);
        assertEquals(0.85, measurement, 0.001);
    }

    @Test
    void testMeasureSunInEclipse() {
        IEnvironment eclipseEnv = EnvironmentFactory.createEclipseEnvironment();
        
        double measurement = sunSensor.measureSun(eclipseEnv);
        assertEquals(0.0, measurement, 0.001, "Sun sensor must return 0.0 in eclipse mode");
    }

    @Test
    void testMeasureSunWhenInactive() {
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment();
        sunSensor.setActive(false);

        double measurement = sunSensor.measureSun(env);
        assertEquals(0.0, measurement, 0.001, "Inactive sensor must return 0.0");
    }

    @Test
    void testPreconditionNullEnvironment() {
        assertThrows(AssertionError.class, () -> sunSensor.measureSun(null));
    }
}