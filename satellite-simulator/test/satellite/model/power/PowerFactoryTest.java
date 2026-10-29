package satellite.model.power;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PowerFactoryTest {

    @Test
    void testCreateBattery() {
        IBattery battery = PowerFactory.createBattery();
        assertNotNull(battery);
        assertTrue(battery.isFull());
    }

    @Test
    void testCreateSolarPanel() {
        ISolarPanel solarPanel = PowerFactory.createSolarPanel();
        assertNotNull(solarPanel);
        assertFalse(solarPanel.isDeployed());
    }

    @Test
    void testCreateDefaultPowerSystemController() {
        IPowerSystemController controller = PowerFactory.createPowerSystemController();
        assertNotNull(controller);
        assertNotNull(controller.getBattery());
        assertNotNull(controller.getSolarPanel());
    }

    @Test
    void testCreateCustomPowerSystemController() {
        IBattery battery = PowerFactory.createBattery();
        ISolarPanel solarPanel = PowerFactory.createSolarPanel();
        
        IPowerSystemController controller = PowerFactory.createPowerSystemController(battery, solarPanel);
        assertNotNull(controller);
        assertEquals(battery, controller.getBattery());
        assertEquals(solarPanel, controller.getSolarPanel());
    }

    @Test
    void testPreconditionNullCustomComponents() {
        IBattery battery = PowerFactory.createBattery();
        ISolarPanel solarPanel = PowerFactory.createSolarPanel();

        assertThrows(AssertionError.class, () -> PowerFactory.createPowerSystemController(null, solarPanel));
        assertThrows(AssertionError.class, () -> PowerFactory.createPowerSystemController(battery, null));
    }
}