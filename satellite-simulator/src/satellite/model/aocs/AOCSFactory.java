package satellite.model.aocs;

/**
 * Factory class responsible for instantiating all AOCS (Attitude and Orbit Control System) components.
 * Encapsulates concrete implementations behind their public interfaces.
 */
public final class AOCSFactory {

    private AOCSFactory() {
        // Utility class
    }

    /**
     * Creates a default Attitude Control subsystem.
     */
    public static IAttitudeControl createAttitudeControl() {
        return new AttitudeControl();
    }

    /**
     * Creates a default Orbit Control subsystem.
     */
    public static IOrbitControl createOrbitControl() {
        return new OrbitControl();
    }

    /**
     * Creates a Propulsion subsystem initialized with specified fuel capacity.
     * 
     * @requires initialFuel >= 0.0;
     */
    public static IPropulsionSystem createPropulsionSystem(double initialFuel) {
        return new PropulsionSystem(initialFuel);
    }
}