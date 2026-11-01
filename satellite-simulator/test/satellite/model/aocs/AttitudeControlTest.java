package satellite.model.aocs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import satellite.model.aocs.sensors.IEarthSensor;
import satellite.model.aocs.sensors.ISunSensor;
import satellite.model.aocs.sensors.SensorFactory;
import satellite.model.environment.EnvironmentFactory;
import satellite.model.environment.IEnvironment;

class AttitudeControlTest {

    private AttitudeControl attitudeControl;
    private ISunSensor sunSensor;
    private IEarthSensor earthSensor;

    @BeforeEach
    void setUp() {
        attitudeControl = new AttitudeControl();
        sunSensor = SensorFactory.createSunSensor();
        earthSensor = SensorFactory.createEarthSensor();
    }

    @Test
    void testInitialState() {
        assertTrue(attitudeControl.isActive());
        assertTrue(attitudeControl.isAligned());
        assertEquals(0.0, attitudeControl.getAlignmentError(), 0.001);
        assertEquals(0.0, attitudeControl.getPowerConsumption(), 0.001);
    }

    @Test
    void testProcessAttitudeMaintenanceMode() {
        // Environment with zero drift -> sensor measurement equals real alignment
        IEnvironment env = EnvironmentFactory.createCustomEnvironment(true, true, true, 1.0, 1.0, 0.0);

        attitudeControl.processAttitude(sunSensor, earthSensor, env);

        assertEquals(0.0, attitudeControl.getAlignmentError(), 0.001);
        assertEquals(2.0, attitudeControl.getPowerConsumption(), 0.001, "Maintenance power should be 2.0W");
        assertTrue(attitudeControl.isAligned());
    }

    @Test
    void testProcessAttitudeCorrectionModeDueToDrift() {
        // Environment with higher drift (0.10) exceeding TOLERANCE (0.05)
        IEnvironment env = EnvironmentFactory.createCustomEnvironment(true, true, true, 1.0, 1.0, 0.10);

        attitudeControl.processAttitude(sunSensor, earthSensor, env);

        assertTrue(attitudeControl.getAlignmentError() > 0.05);
        assertEquals(15.0, attitudeControl.getPowerConsumption(), 0.001, "Correction power should be 15.0W");
    }

    @Test
    void testProcessAttitudeInEclipse() {
        IEnvironment eclipseEnv = EnvironmentFactory.createEclipseEnvironment();

        attitudeControl.processAttitude(sunSensor, earthSensor, eclipseEnv);

        assertEquals(0.0, attitudeControl.getCurrentSunAlignment(), 0.001, "Sun alignment locks to 0.0 in eclipse");
        assertEquals(2.0, attitudeControl.getPowerConsumption(), 0.001, "No sun correction trigger during eclipse");
    }

    @Test
    void testDeactivation() {
        attitudeControl.setActive(false);

        assertFalse(attitudeControl.isActive());
        
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment();
        attitudeControl.processAttitude(sunSensor, earthSensor, env);

        assertEquals(0.0, attitudeControl.getPowerConsumption(), 0.001);
        assertEquals(0.0, attitudeControl.getAlignmentError(), 0.001);
    }

    @Test
    void testPreconditionsNullParameters() {
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment();

        assertThrows(AssertionError.class, () -> attitudeControl.processAttitude(null, earthSensor, env));
        assertThrows(AssertionError.class, () -> attitudeControl.processAttitude(sunSensor, null, env));
        assertThrows(AssertionError.class, () -> attitudeControl.processAttitude(sunSensor, earthSensor, null));
    }
}
