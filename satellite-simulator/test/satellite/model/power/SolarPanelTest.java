package satellite.model.power;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolarPanelTest {

    private SolarPanel solarPanel;

    @BeforeEach
    void setUp() {
        solarPanel = new SolarPanel();
    }

    @Test
    void testInitialState() {
        assertFalse(solarPanel.isDeployed(), "Solar panel must initially be stowed");
        assertFalse(solarPanel.isFunctional(), "Solar panel must not be functional at startup");
        assertEquals(0.0, solarPanel.getEnergyProduction(), 0.001, "Initial energy production must be 0.0 W");
    }

    @Test
    void testDeployNominal() {
        solarPanel.deploy();
        assertTrue(solarPanel.isDeployed(), "Solar panel must be deployed after deploy() call");
        assertFalse(solarPanel.isFunctional(), "Panel is deployed but produces no energy yet (not functional)");
        assertEquals(0.0, solarPanel.getEnergyProduction(), 0.001);
    }

    @Test
    void testPreconditionDoubleDeployment() {
        solarPanel.deploy();
        // Attempting to deploy an already deployed panel violates @requires !isDeployed()
        assertThrows(AssertionError.class, () -> solarPanel.deploy());
    }

    @Test
    void testUpdateProductionWhileStowed() {
        // Environment sends 50.0 W of sun energy while panel is stowed
        solarPanel.updateProduction(50.0);
        
        assertEquals(0.0, solarPanel.getEnergyProduction(), 0.001, "Production must remain 0.0 W when stowed");
        assertFalse(solarPanel.isFunctional(), "Stowed panel must remain non-functional");
    }

    @Test
    void testUpdateProductionWhileDeployedAndInSunlight() {
        solarPanel.deploy();
        solarPanel.updateProduction(45.5);

        assertEquals(45.5, solarPanel.getEnergyProduction(), 0.001, "Production must equal sun energy when deployed");
        assertTrue(solarPanel.isFunctional(), "Deployed panel in sunlight must be functional");
    }

    @Test
    void testUpdateProductionEclipseTransition() {
        solarPanel.deploy();
        solarPanel.updateProduction(40.0); // In sunlight
        assertTrue(solarPanel.isFunctional());

        // Entering eclipse (0.0 W)
        solarPanel.updateProduction(0.0);
        assertEquals(0.0, solarPanel.getEnergyProduction(), 0.001);
        assertFalse(solarPanel.isFunctional(), "Panel in eclipse must not be functional");
    }

    @Test
    void testPreconditionNegativeSunEnergy() {
        // Negative sun energy violates @requires sunEnergy >= MIN_ENERGY
        assertThrows(AssertionError.class, () -> solarPanel.updateProduction(-10.0));
    }
}