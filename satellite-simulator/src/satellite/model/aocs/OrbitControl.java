package satellite.model.aocs;

import satellite.model.IEnvironment;

public class OrbitControl implements IOrbitControl {

    private boolean active;
    private boolean maneuverRequested;
    private double orbitalDrift;
    private double powerConsumption;

    private static final double DRIFT_FACTOR = 0.02;        
    private static final double MANEUVER_FUEL_COST = 0.5;   
    private static final double CONTROL_POWER = 1.0;        
    public OrbitControl() {
        this.active = true;
        this.maneuverRequested = false;
        this.orbitalDrift = 0.0;
        this.powerConsumption = CONTROL_POWER;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean isManeuverInProgress() {
        return this.maneuverRequested;
    }

    @Override
    public double getOrbitalDrift() {
        return this.orbitalDrift;
    }

    @Override
    public double getPowerConsumption() {
        return this.powerConsumption;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.maneuverRequested = false;
            this.powerConsumption = 0.0;
        } else {
            this.powerConsumption = CONTROL_POWER;
        }
    }

    @Override
    public void requestGroundManeuver() {
        if (this.active) {
            this.maneuverRequested = true;
        }
    }

    @Override
    public void processOrbit(IPropulsionSystem propulsion, IEnvironment env) {
        if (!this.active) {
            this.powerConsumption = 0.0;
            this.maneuverRequested = false;
            return;
        }

        double environmentalDrift = env.getNaturalDrift();
        this.orbitalDrift += environmentalDrift * DRIFT_FACTOR;

        if (this.maneuverRequested) {
            if (propulsion.isActive()) {
                propulsion.fireThrusters(MANEUVER_FUEL_COST);

                if (propulsion.isFiring()) {
                    this.orbitalDrift = 0.0;
                }
            }
            
            this.maneuverRequested = false;
        } else {
            if (propulsion.isActive()) {
                propulsion.stopFiring();
            }
        }

        this.powerConsumption = CONTROL_POWER + (propulsion.isActive() ? propulsion.getPowerConsumption() : 0.0);
    }
}