package satellite.model.aocs;

import satellite.model.IEnvironment;
import satellite.model.aocs.sensors.IEarthSensor;
import satellite.model.aocs.sensors.ISunSensor;

public class AttitudeControl implements IAttitudeControl {

    private boolean active;
    private double alignmentError;
    private double powerConsumption;

    private double currentSunAlignment;
    private double currentEarthAlignment;

    private static final double DESIRED_SUN_ALIGNMENT = 1.0;
    private static final double DESIRED_EARTH_ALIGNMENT = 1.0;
    private static final double TOLERANCE = 0.05;

    private static final double CORRECTION_POWER = 15.0;
    private static final double MAINTENANCE_POWER = 2.0;

    public AttitudeControl() {
        this.active = true;
        this.alignmentError = 0.0;
        this.powerConsumption = 0.0;
        this.currentSunAlignment = DESIRED_SUN_ALIGNMENT;
        this.currentEarthAlignment = DESIRED_EARTH_ALIGNMENT;
    }

    @Override
    public boolean isAligned() {
        return (Math.abs(DESIRED_SUN_ALIGNMENT - this.currentSunAlignment) <= TOLERANCE) &&
               (Math.abs(DESIRED_EARTH_ALIGNMENT - this.currentEarthAlignment) <= TOLERANCE);
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public double getAlignmentError() {
        return this.alignmentError;
    }

    @Override
    public double getPowerConsumption() {
        return this.powerConsumption;
    }

    @Override
    public double getCurrentSunAlignment() {
        return this.currentSunAlignment;
    }

    @Override
    public double getCurrentEarthAlignment() {
        return this.currentEarthAlignment;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.alignmentError = 0.0;
            this.powerConsumption = 0.0;
        }
    }

    @Override
    public void processAttitude(
            ISunSensor sunSensor,
            IEarthSensor earthSensor,
            IEnvironment env) {

        if (!this.active) {
            this.alignmentError = 0.0;
            this.powerConsumption = 0.0;
            return;
        }

        double sunMeasurement = sunSensor.measureSun(env);
        double earthMeasurement = earthSensor.measureEarth(env);

        double sunError = env.isInSunlight() 
                ? Math.abs(env.getSunAlignment() - sunMeasurement) 
                : 0.0;

        double earthError = Math.abs(env.getEarthAlignment() - earthMeasurement);

        this.alignmentError = sunError + earthError;

        if (sunError > TOLERANCE || earthError > TOLERANCE) {
            
            if (env.isInSunlight()) {
                this.currentSunAlignment = env.getSunAlignment();
            } else {
                this.currentSunAlignment = 0.0; 
            }

            this.currentEarthAlignment = env.getEarthAlignment();

            this.powerConsumption = CORRECTION_POWER;

        } else {
            this.currentSunAlignment = sunMeasurement;
            this.currentEarthAlignment = earthMeasurement;
            
            this.powerConsumption = MAINTENANCE_POWER;
        }
    }
}