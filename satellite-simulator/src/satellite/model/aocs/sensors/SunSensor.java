package satellite.model.aocs.sensors;

import satellite.model.environment.IEnvironment;

public class SunSensor implements ISunSensor {

    private boolean active;

    public SunSensor() {
        this.active = true;
    }

    @Override
    public SensorType getType() {
        return SensorType.SUN_SENSOR;
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
    public double measureSun(IEnvironment env) {
        if (!this.active || !env.isInSunlight()) {
            return 0.0;
        }

        double realMeasurement = env.getSunAlignment() - env.getNaturalDrift();
        return Math.max(0.0, Math.min(1.0, realMeasurement));
    }
}