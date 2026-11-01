package satellite.model.environment;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnvironmentTest {

    private Environment environment;

    @BeforeEach
    void setUp() {
        // Default nominal orbital environment: in sunlight, Earth in sight, partial alignments
        environment = new Environment(true, true, false, 0.9, 0.85, 0.02);
    }

    @Test
    void testNominalState() {
        assertTrue(environment.isInSunlight());
        assertTrue(environment.isEarthInSight());
        assertFalse(environment.isGroundStationInSight());
        assertEquals(0.9, environment.getSunAlignment(), 0.001);
        assertEquals(0.85, environment.getEarthAlignment(), 0.001);
        assertEquals(0.02, environment.getNaturalDrift(), 0.001);
    }

    @Test
    void testPreconditionInvalidSunAlignment() {
        assertThrows(AssertionError.class, () -> 
            new Environment(true, true, true, -0.1, 0.8, 0.01)
        );
        assertThrows(AssertionError.class, () -> 
            new Environment(true, true, true, 1.1, 0.8, 0.01)
        );
    }

    @Test
    void testPreconditionInvalidEarthAlignment() {
        assertThrows(AssertionError.class, () -> 
            new Environment(true, true, true, 0.5, -0.05, 0.01)
        );
        assertThrows(AssertionError.class, () -> 
            new Environment(true, true, true, 0.5, 1.05, 0.01)
        );
    }

    @Test
    void testPreconditionNegativeNaturalDrift() {
        assertThrows(AssertionError.class, () -> 
            new Environment(true, true, true, 0.5, 0.5, -0.01)
        );
    }

    @Test
    void testUpdateStateExecution() {
        assertDoesNotThrow(() -> environment.updateState());
    }
    
    @Test
    void testUpdateStateDegradesAlignmentOverTicks() {
        // Start with perfect sun alignment (1.0) and drift of 0.05
        Environment env = new Environment(true, true, true, 1.0, 1.0, 0.05);

        env.updateState();

        // After 1 tick, sun alignment should degrade from 1.0 to 0.95
        assertEquals(0.95, env.getSunAlignment(), 0.001, "Sun alignment should degrade by naturalDrift");
    }

    @Test
    void testUpdateStateEclipseTransition() {
        // Create environment with 50-tick eclipse period
        Environment env = new Environment(true, true, true, 0.9, 0.9, 0.01);

        // Advance 49 ticks (still in sunlight)
        for (int i = 0; i < 49; i++) {
            env.updateState();
        }
        assertTrue(env.isInSunlight());

        // Tick 50: Enters eclipse
        env.updateState();
        assertFalse(env.isInSunlight(), "Environment should transition to eclipse at tick 50");
        assertEquals(0.0, env.getSunAlignment(), 0.001, "Sun alignment must evaluate to 0.0 in eclipse");
    }
}