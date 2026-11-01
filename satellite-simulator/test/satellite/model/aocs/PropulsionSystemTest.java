package satellite.model.aocs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropulsionSystemTest {

    private PropulsionSystem propulsionSystem;
    private static final double INITIAL_FUEL = 10.0; // 10.0 kg

    @BeforeEach
    void setUp() {
        propulsionSystem = new PropulsionSystem(INITIAL_FUEL);
    }

    @Test
    void testInitialState() {
        assertTrue(propulsionSystem.isActive());
        assertFalse(propulsionSystem.isFiring());
        assertEquals(INITIAL_FUEL, propulsionSystem.getFuelLevel(), 0.001);
        assertEquals(5.0, propulsionSystem.getPowerConsumption(), 0.001, "Standby power should be 5.0 W");
    }

    @Test
    void testFireThrustersSuccess() {
        double fuelToBurn = 0.5;
        propulsionSystem.fireThrusters(fuelToBurn);

        assertTrue(propulsionSystem.isFiring());
        assertEquals(INITIAL_FUEL - fuelToBurn, propulsionSystem.getFuelLevel(), 0.001);
        assertEquals(40.0, propulsionSystem.getPowerConsumption(), 0.001, "Firing power should be 40.0 W");
    }

    @Test
    void testFireThrustersInsufficientFuel() {
        double excessiveFuel = 15.0; // Greater than 10.0 kg
        propulsionSystem.fireThrusters(excessiveFuel);

        assertFalse(propulsionSystem.isFiring(), "Thrusters should fail to fire due to insufficient fuel");
        assertEquals(INITIAL_FUEL, propulsionSystem.getFuelLevel(), 0.001, "Fuel level should remain unchanged");
        assertEquals(5.0, propulsionSystem.getPowerConsumption(), 0.001, "Power should drop back to standby 5.0 W");
    }

    @Test
    void testStopFiring() {
        propulsionSystem.fireThrusters(1.0);
        assertTrue(propulsionSystem.isFiring());

        propulsionSystem.stopFiring();
        assertFalse(propulsionSystem.isFiring());
        assertEquals(5.0, propulsionSystem.getPowerConsumption(), 0.001, "Power should return to standby 5.0 W");
    }

    @Test
    void testDeactivation() {
        propulsionSystem.setActive(false);

        assertFalse(propulsionSystem.isActive());
        assertFalse(propulsionSystem.isFiring());
        assertEquals(0.0, propulsionSystem.getPowerConsumption(), 0.001, "Power consumption must be 0.0 W when inactive");
    }

    @Test
    void testPreconditionsConstructor() {
        assertThrows(AssertionError.class, () -> new PropulsionSystem(-1.0));
    }

    @Test
    void testPreconditionsFireThrusters() {
        // Invalid fuelAmount <= 0.0
        assertThrows(AssertionError.class, () -> propulsionSystem.fireThrusters(0.0));
        assertThrows(AssertionError.class, () -> propulsionSystem.fireThrusters(-0.5));

        // Inactive propulsion
        propulsionSystem.setActive(false);
        assertThrows(AssertionError.class, () -> propulsionSystem.fireThrusters(0.5));
    }

    @Test
    void testPreconditionsStopFiringWhenInactive() {
        propulsionSystem.setActive(false);
        assertThrows(AssertionError.class, () -> propulsionSystem.stopFiring());
    }
}