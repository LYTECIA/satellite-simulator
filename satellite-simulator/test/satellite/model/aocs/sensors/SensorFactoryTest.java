package satellite.model.aocs.sensors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SensorFactoryTest {

    @Test
    void testCreateSunSensor() {
        ISunSensor sunSensor = SensorFactory.createSunSensor();
        
        assertNotNull(sunSensor, "Factory should not return null for SunSensor");
        assertEquals(SensorType.SUN_SENSOR, sunSensor.getType());
        assertTrue(sunSensor.isActive(), "SunSensor should be active by default upon creation");
    }

    @Test
    void testCreateEarthSensor() {
        IEarthSensor earthSensor = SensorFactory.createEarthSensor();
        
        assertNotNull(earthSensor, "Factory should not return null for EarthSensor");
        assertEquals(SensorType.EARTH_SENSOR, earthSensor.getType());
        assertTrue(earthSensor.isActive(), "EarthSensor should be active by default upon creation");
    }
}