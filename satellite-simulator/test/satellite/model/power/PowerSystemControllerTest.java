package satellite.model.power;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PowerSystemControllerTest {

    private Battery battery;
    private SolarPanel solarPanel;
    private PowerSystemController controller;

    @BeforeEach
    void setUp() {
        battery = new Battery(); // Starts full at 100.0 W
        solarPanel = new SolarPanel(); // Starts stowed, 0.0 W
        controller = new PowerSystemController(battery, solarPanel);
    }

    @Test
    void testInitialState() {
        assertNotNull(controller.getBattery());
        assertNotNull(controller.getSolarPanel());
        assertEquals(0.0, controller.getNetFlow(), 0.001);
        assertEquals(0.0, controller.getGrantedEnergy(), 0.001);
    }

    @Test
    void testPreconditionNullConstructorArguments() {
        assertThrows(AssertionError.class, () -> new PowerSystemController(null, solarPanel));
        assertThrows(AssertionError.class, () -> new PowerSystemController(battery, null));
    }

    @Test
    void testPreconditionNegativeConsumption() {
        assertThrows(AssertionError.class, () -> controller.processEnergyBalance(-5.0));
    }

    @Test
    void testSurplusEnergyBatteryAlreadyFull() {
        // Deploy solar panel and produce 50.0 W
        solarPanel.deploy();
        solarPanel.updateProduction(50.0);

        // System consumes 20.0 W (Surplus of 30.0 W)
        controller.processEnergyBalance(20.0);

        // Battery is already full at 100.0 W, so surplus is discarded (clamped)
        assertEquals(100.0, battery.getChargeLevel(), 0.001);
        assertEquals(0.0, controller.getNetFlow(), 0.001, "Net flow to battery is 0.0 because it's full");
        assertEquals(20.0, controller.getGrantedEnergy(), 0.001, "Granted energy equals requested consumption");
    }

    @Test
    void testSurplusEnergyBatteryPartiallyEmpty() {
        // Discharge battery to 80.0 W first
        battery.discharge(20.0);

        solarPanel.deploy();
        solarPanel.updateProduction(30.0);

        // System consumes 10.0 W (Surplus of 20.0 W)
        controller.processEnergyBalance(10.0);

        // Battery should absorb the 20.0 W surplus and reach 100.0 W
        assertEquals(100.0, battery.getChargeLevel(), 0.001);
        assertEquals(20.0, controller.getNetFlow(), 0.001, "Net flow to battery is +20.0 W");
        assertEquals(10.0, controller.getGrantedEnergy(), 0.001);
    }

    @Test
    void testDeficitEnergyDrawnFromBattery() {
        // Solar panel produces 10.0 W
        solarPanel.deploy();
        solarPanel.updateProduction(10.0);

        // System requires 25.0 W (Deficit of 15.0 W)
        controller.processEnergyBalance(25.0);

        // 15.0 W should be drawn from the battery (100.0 W -> 85.0 W)
        assertEquals(85.0, battery.getChargeLevel(), 0.001);
        assertEquals(-15.0, controller.getNetFlow(), 0.001, "Net flow from battery is -15.0 W");
        assertEquals(25.0, controller.getGrantedEnergy(), 0.001, "All requested energy was granted");
    }

    @Test
    void testDeficitEnergyBatteryDepletedPartialGrant() {
        // Discharge battery completely (0.0 W left)
        battery.discharge(100.0);

        // Solar panel produces only 10.0 W
        solarPanel.deploy();
        solarPanel.updateProduction(10.0);

        // System requests 30.0 W (Deficit of 20.0 W, but battery is empty)
        controller.processEnergyBalance(30.0);

        assertEquals(0.0, battery.getChargeLevel(), 0.001, "Battery stays empty");
        assertEquals(0.0, controller.getNetFlow(), 0.001, "No energy could be drawn from battery");
        assertEquals(10.0, controller.getGrantedEnergy(), 0.001, "Only solar production (10.0 W) could be granted");
    }
}