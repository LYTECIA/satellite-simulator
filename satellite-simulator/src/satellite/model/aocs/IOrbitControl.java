package satellite.model.aocs;

import satellite.model.environment.IEnvironment;

public interface IOrbitControl {

    /*@ pure @*/
    boolean isActive();

    /**
     * Returns true if an orbital correction maneuver is currently in progress.
     */
    /*@ pure @*/
    boolean isManeuverInProgress();

    /**
     * Returns the current orbital drift scalar (0.0 = perfect orbit, > 0.05 = drift).
     */
    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getOrbitalDrift();

    /*@ pure @*/
    /*@ ensures \result >= 0.0; @*/
    double getPowerConsumption();

    /**
     * Turns the orbit control system ON or OFF.
     */
    /*@ ensures isActive() == active; @*/
    void setActive(boolean active);

    /**
     * Telecommand received from Ground Control (via OBC/Comm) ordering a maneuver.
     */
    /*@ requires isActive();
      @ ensures isManeuverInProgress();
      @*/
    void requestGroundManeuver();

    /**
     * Processes one discrete simulation tick for orbit management.
     * Evaluates drift and triggers propulsion ONLY IF a ground command was received.
     */
    /*@ requires propulsion != null;
      @ requires env != null;
      @*/
    void processOrbit(IPropulsionSystem propulsion, IEnvironment env);
}   
