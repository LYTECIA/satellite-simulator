package satellite.model.aocs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AOCSFactoryTest {

    @Test
    void testCreateAttitudeControl() {
        IAttitudeControl attitude = AOCSFactory.createAttitudeControl();
        assertNotNull(attitude);
        assertTrue(attitude.isActive());
    }

    @Test
    void testCreateOrbitControl() {
        IOrbitControl orbit = AOCSFactory.createOrbitControl();
        assertNotNull(orbit);
        assertTrue(orbit.isActive());
    }

    @Test
    void testCreatePropulsionSystem() {
        IPropulsionSystem propulsion = AOCSFactory.createPropulsionSystem(15.0);
        assertNotNull(propulsion);
        assertTrue(propulsion.isActive());
        assertEquals(15.0, propulsion.getFuelLevel(), 0.001);
    }

    @Test
    void testCreatePropulsionSystemPrecondition() {
        assertThrows(AssertionError.class, () -> AOCSFactory.createPropulsionSystem(-5.0));
    }
}