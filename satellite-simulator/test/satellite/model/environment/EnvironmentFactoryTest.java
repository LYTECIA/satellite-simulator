package satellite.model.environment;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EnvironmentFactoryTest {

    
    @Test
    void testCreateDefaultEnvironment() {
        IEnvironment env = EnvironmentFactory.createDefaultEnvironment();
        assertNotNull(env);
        assertTrue(env.isInSunlight());
        assertTrue(env.isEarthInSight());
        assertTrue(env.isGroundStationInSight());
        assertEquals(1.0, env.getSunAlignment(), 0.001);
        assertEquals(1.0, env.getEarthAlignment(), 0.001);
        assertEquals(0.01, env.getNaturalDrift(), 0.001);
    }


    
    @Test
    void testCreateEclipseEnvironment() {
        IEnvironment env = EnvironmentFactory.createEclipseEnvironment();
        assertNotNull(env);
        assertFalse(env.isInSunlight());
        assertTrue(env.isEarthInSight());
        assertEquals(0.0, env.getSunAlignment(), 0.001);
    }


    @Test
    void testCreateCustomEnvironmentNominal() {
        IEnvironment env = EnvironmentFactory.createCustomEnvironment(true, true, false, 0.75, 0.85, 0.03);
        
        assertNotNull(env);
        assertTrue(env.isInSunlight());
        assertTrue(env.isEarthInSight());
        assertFalse(env.isGroundStationInSight());
        assertEquals(0.75, env.getSunAlignment(), 0.001, "Sun alignment evaluates to 0.0 in eclipse mode");
        assertEquals(0.85, env.getEarthAlignment(), 0.001);
        assertEquals(0.03, env.getNaturalDrift(), 0.001);
    }

    @Test
    void testCreateCustomEnvironmentPreconditions() {
        // --- SUN ALIGNMENT BOUNDARY CHECKS ---
        // Sun alignment > 1.0
        assertThrows(AssertionError.class, () ->
            EnvironmentFactory.createCustomEnvironment(true, true, true, 1.05, 0.5, 0.01)
        );
        // Sun alignment < 0.0
        assertThrows(AssertionError.class, () ->
            EnvironmentFactory.createCustomEnvironment(true, true, true, -0.05, 0.5, 0.01)
        );
        
        // --- EARTH ALIGNMENT BOUNDARY CHECKS ---
        // Earth alignment > 1.0
        assertThrows(AssertionError.class, () ->
            EnvironmentFactory.createCustomEnvironment(true, true, true, 0.8, 1.05, 0.01)
        );
        // Earth alignment < 0.0
        assertThrows(AssertionError.class, () ->
            EnvironmentFactory.createCustomEnvironment(true, true, true, 0.8, -0.05, 0.01)
        );
        
        // --- NATURAL DRIFT CHECK ---
        // Negative natural drift (< 0.0)
        assertThrows(AssertionError.class, () ->
            EnvironmentFactory.createCustomEnvironment(true, true, true, 0.8, 0.5, -0.01)
        );
    }
}