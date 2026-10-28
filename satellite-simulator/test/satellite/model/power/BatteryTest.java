package satellite.model.power;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatteryTest {

    private Battery battery;

    @BeforeEach
    void setUp() {
        // Battery initializes at full capacity (100.0 W)
        battery = new Battery();
    }

    @Test
    void testInitialState() {
        assertEquals(100.0, battery.getChargeLevel(), 0.001, "Battery must initially be full at 100.0 W");
        assertTrue(battery.isFull(), "isFull() must return true at startup");
        assertFalse(battery.isEmpty(), "isEmpty() must return false at startup");
    }

    @Test
    void testDischargeAndChargeNominal() {
        // Nominal discharge of 30 W
        battery.discharge(30.0);
        assertEquals(70.0, battery.getChargeLevel(), 0.001, "Battery level must be at 70.0 W");
        assertFalse(battery.isFull());
        assertFalse(battery.isEmpty());

        // Nominal recharge of 20 W
        battery.charge(20.0);
        assertEquals(90.0, battery.getChargeLevel(), 0.001, "Battery level must be at 90.0 W");
    }

    @Test
    void testPreconditionNegativeOrZeroAmount() {
        // Charge  amount <= 0 must trigger an AssertionError (precondition violation)
        assertThrows(AssertionError.class, () -> battery.charge(0.0));
        assertThrows(AssertionError.class, () -> battery.charge(-10.0));

        // Discharge amount <= 0 must trigger an AssertionError
        assertThrows(AssertionError.class, () -> battery.discharge(0.0));
        assertThrows(AssertionError.class, () -> battery.discharge(-5.0));
    }

    @Test
    void testPreconditionOvercharge() {
        // Battery is at 100 W. Attempting to charge even 0.1 W violates the precondition
        assertThrows(AssertionError.class, () -> battery.charge(0.1));
    }

    @Test
    void testPreconditionOverDischarge() {
        // Attempting to discharge more than remaining capacity (100 W)
        assertThrows(AssertionError.class, () -> battery.discharge(150.0));
    }

    @Test
    void testClampingAndBoundaries() {
        // Discharge until reaching exactly 0 W
        battery.discharge(100.0);
        assertEquals(0.0, battery.getChargeLevel(), 0.001);
        assertTrue(battery.isEmpty(), "isEmpty() must return true when level reaches MIN_CHARGE_LEVEL");

        // Charge until reaching exactly 100 W again
        battery.charge(100.0);
        assertEquals(100.0, battery.getChargeLevel(), 0.001);
        assertTrue(battery.isFull(), "isFull() must return true when level reaches MAX_CHARGE_LEVEL");
    }
}