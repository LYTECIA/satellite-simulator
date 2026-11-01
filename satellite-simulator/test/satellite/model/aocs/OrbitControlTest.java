package satellite.model.aocs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import satellite.model.environment.EnvironmentFactory;
import satellite.model.environment.IEnvironment;

class OrbitControlTest {

    private OrbitControl orbitControl;
    private IPropulsionSystem propulsion;
    private IEnvironment env;

    @BeforeEach
    void setUp() {
        orbitControl = new OrbitControl();
        propulsion = new PropulsionSystem(10.0); // 10.0 kg of fuel
        env = EnvironmentFactory.createCustomEnvironment(true, true, true, 1.0, 1.0, 0.5); // Natural drift = 0.5
    }

    @Test
    void testInitialState() {
        assertTrue(orbitControl.isActive());
        assertFalse(orbitControl.isManeuverInProgress());
        assertEquals(0.0, orbitControl.getOrbitalDrift(), 0.001);
        assertEquals(1.0, orbitControl.getPowerConsumption(), 0.001);
    }

    @Test
    void testProcessOrbitIncreasesDriftWithoutManeuver() {
        // First tick: orbital drift increases by naturalDrift * DRIFT_FACTOR = 0.5 * 0.02 = 0.01
        orbitControl.processOrbit(propulsion, env);

        assertEquals(0.01, orbitControl.getOrbitalDrift(), 0.001);
        assertFalse(propulsion.isFiring());
        assertEquals(6.0, orbitControl.getPowerConsumption(), 0.001, "1.0 W (Control) + 5.0 W (Propulsion Standby)");
    }

    @Test
    void testProcessOrbitExecutesGroundManeuver() {
        // Accumulate initial drift
        orbitControl.processOrbit(propulsion, env);
        assertTrue(orbitControl.getOrbitalDrift() > 0.0);

        // Ground station sends maneuver order
        orbitControl.requestGroundManeuver();
        assertTrue(orbitControl.isManeuverInProgress());

        // Process tick with maneuver
        orbitControl.processOrbit(propulsion, env);

        assertEquals(0.0, orbitControl.getOrbitalDrift(), 0.001, "Maneuver resets orbital drift to 0.0");
        assertFalse(orbitControl.isManeuverInProgress(), "Maneuver flag cleared after execution");
        assertEquals(41.0, orbitControl.getPowerConsumption(), 0.001, "1.0 W (Control) + 40.0 W (Propulsion Firing)");
    }

    @Test
    void testManeuverFailsDueToFuelDepletion() {
        IPropulsionSystem emptyPropulsion = new PropulsionSystem(0.1); // Insufficient fuel for 0.5 kg cost

        orbitControl.requestGroundManeuver();
        orbitControl.processOrbit(emptyPropulsion, env);

        assertFalse(emptyPropulsion.isFiring());
        assertTrue(orbitControl.getOrbitalDrift() > 0.0, "Drift is not reset if thrusters fail to fire");
    }

    @Test
    void testDeactivation() {
        orbitControl.setActive(false);

        assertFalse(orbitControl.isActive());
        orbitControl.processOrbit(propulsion, env);

        assertEquals(0.0, orbitControl.getPowerConsumption(), 0.001);
    }

    @Test
    void testPreconditionsNullParameters() {
        assertThrows(AssertionError.class, () -> orbitControl.processOrbit(null, env));
        assertThrows(AssertionError.class, () -> orbitControl.processOrbit(propulsion, null));
    }

    @Test
    void testPreconditionRequestManeuverWhenInactive() {
        orbitControl.setActive(false);
        assertThrows(AssertionError.class, () -> orbitControl.requestGroundManeuver());
    }
}