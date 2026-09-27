package satellite.model.aocs;

import satellite.model.aocs.sensors.IEarthSensor;
import satellite.model.aocs.sensors.ISunSensor;
import satellite.model.environment.IEnvironment;

/**
 * Concrete facade orchestrating sub-components: AttitudeControl, OrbitControl,
 * PropulsionSystem, SunSensor, and EarthSensor.
 */
public class AOCS implements IAOCS {

    private final IAttitudeControl attitudeControl;
    private final IOrbitControl orbitControl;
    private final IPropulsionSystem propulsionSystem;
    private final ISunSensor sunSensor;
    private final IEarthSensor earthSensor;

    /**
     * Constructor injecting all sub-components.
     */
    public AOCS(IAttitudeControl attitudeControl, 
                IOrbitControl orbitControl, 
                IPropulsionSystem propulsionSystem, 
                ISunSensor sunSensor, 
                IEarthSensor earthSensor) {
        
        if (attitudeControl == null || orbitControl == null || propulsionSystem == null 
            || sunSensor == null || earthSensor == null) {
            throw new IllegalArgumentException("AOCS sub-components cannot be null.");
        }

        this.attitudeControl = attitudeControl;
        this.orbitControl = orbitControl;
        this.propulsionSystem = propulsionSystem;
        this.sunSensor = sunSensor;
        this.earthSensor = earthSensor;
    }

    @Override
    public IAttitudeControl getAttitudeControl() {
        return attitudeControl;
    }

    @Override
    public IOrbitControl getOrbitControl() {
        return orbitControl;
    }

    @Override
    public IPropulsionSystem getPropulsionSystem() {
        return propulsionSystem;
    }

    @Override
    public ISunSensor getSunSensor() {
        return sunSensor;
    }

    @Override
    public IEarthSensor getEarthSensor() {
        return earthSensor;
    }

    @Override
    public double getAttitudeAlignmentError() {
        return attitudeControl.getAlignmentError();
    }

    @Override
    public boolean isAttitudeStable() {
        return attitudeControl.isAligned();
    }

    @Override
    public double getPowerConsumption() {
        return attitudeControl.getPowerConsumption()
             + orbitControl.getPowerConsumption()
             + propulsionSystem.getPowerConsumption();
    }

    @Override
    public void processAOCS(IEnvironment env) {
        if (env == null) {
            return;
        }

        attitudeControl.processAttitude(this.sunSensor, this.earthSensor, env);

        orbitControl.processOrbit(this.propulsionSystem, env);
    }
}