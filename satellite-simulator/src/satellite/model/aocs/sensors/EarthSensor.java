package satellite.model.aocs.sensors;

import satellite.model.environment.IEnvironment;

 class EarthSensor implements IEarthSensor {

    private boolean active;

    EarthSensor() {
        this.active = true;
    }

    @Override
    public SensorType getType() {
        return SensorType.EARTH_SENSOR;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    
    @Override
    public double measureEarth(IEnvironment env) {
        if (env == null) {
            throw new AssertionError("Precondition violated: environment cannot be null.");
        }

        if (!this.active) {
            return 0.0;
        }

        if (!env.isEarthInSight()) {
            return 0.0;
        }

        return env.getEarthAlignment();
    }
}

