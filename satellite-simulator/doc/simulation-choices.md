# Simulation Choices and Design Decisions

## Purpose of this Document

The overall software architecture of the simulator is described in `architecture.md`.

This document has a different purpose.

As the implementation progresses, some design decisions and simplifications must be made. A real satellite is an extremely complex system, and accurately reproducing every physical phenomenon would considerably increase the complexity of the project without providing significant value for the software architecture that this project aims to demonstrate.

Whenever such a decision is made, it is documented here together with its motivation and its consequences on the simulation model.

This document will therefore evolve throughout the development of the simulator.

---

# 1. Simulation Time Model

## Choice

The simulator uses a **discrete-time model** instead of a real-time continuous simulation.

## Motivation

Executing the simulation in discrete time makes the system deterministic, easier to understand, and easier to verify using JML specifications.

It also allows every subsystem to evolve in clearly defined simulation steps instead of reacting continuously.

## Implementation

The simulation is driven by a main execution loop.

Each iteration of this loop represents one **simulation tick**.

During each tick, every subsystem updates its internal state according to the current simulation state.

---

# 2. Power System

## 2.1 Unified Energy Unit

### Choice

The project uses a single energy unit throughout the Power System.

Whenever an energy value appears in the simulation, it is assumed to be expressed in **Watts (W)**.

### Motivation

In reality, satellites manipulate several electrical units (Watts, Watt-hours, Ampere-hours, percentages, etc.).

For this project, introducing conversions between these units would increase the implementation complexity without improving the software architecture.

Using a single unit keeps the mathematical model simple, readable, and consistent.

### Consequences

Solar panels produce energy in Watts.

Battery capacity is represented using the same unit.

Subsystem consumption is also expressed in Watts.

No unit conversion is performed inside the simulator.

---

## 2.2 Simplified Energy Surplus Management

### Choice

Energy that cannot be consumed or stored is discarded by the simulation.

### Motivation

In a real satellite, when the battery is fully charged and the current power consumption is lower than the energy produced by the solar panels, the excess energy is managed by the Electrical Power System (EPS) and eventually dissipated as heat. This thermal effect can then interact with the Thermal Control System.

Modeling these physical interactions would require coupling several subsystems and would considerably increase the complexity of the project.

Since this additional complexity does not provide significant value for the software architecture being developed, these interactions are intentionally omitted.

### Behaviour

If the solar panels produce more energy than the battery can store and the subsystems can consume during the current simulation tick, only the useful energy is kept.

The remaining surplus is simply removed from the simulation and has no further effect.

It is not converted into heat and does not interact with the Thermal System.

---

## Future Decisions

Additional implementation choices will be documented here as new subsystems are developed.


# 3. Attitude and Orbit Control System (AOCS)

## 3.1 Information-Based Abstraction Over 3D Physics

### Choice

The AOCS is modeled around discrete scalar alignment indicators (range $[0.0, 1.0]$) rather than explicit 3D physical dynamics (vector forces, rotation matrices, quaternions, or Euler angles).

### Motivation

In full-scale satellite engineering, attitude dynamics involve complex 3D mechanics and continuous perturbation models. Implementing these mathematical models would introduce heavy mathematical overhead without yielding extra architectural value for a software simulation. 

Following core software engineering principles, the simulator focuses on the **information content** rather than the physical object: the software controller needs to know *if* and *by how much* the satellite has drifted, not compute complex vector mechanics.

### Consequences

* **Sun Alignment** (`[0.0, 1.0]`): Represents how well the solar panels are facing the Sun ($1.0$ = optimal alignment, $0.0$ = total misalignment or eclipsed).
* **Earth Alignment** (`[0.0, 1.0]`): Represents how accurately payload sensors and communication antennas point toward Earth.
* Attitude drift and correction are represented as simple scalar deviations updated per simulation tick.

---

## 3.2 Simplified Environmental Relative Viewpoint

### Choice

The environment (`IEnvironment`) represents the space conditions exclusively **relative to the satellite’s perspective**, rather than simulating global orbital mechanics.

### Motivation

Simulating absolute celestial mechanics (Earth/Sun ephemerides, orbital planes) would couple the environment heavily with orbital physics engines. Modeling boolean visibility states and alignment coefficients from the satellite's viewpoint satisfies all functional dependencies of the AOCS, Power, and Payload subsystems.

### Behaviour

* `isInSunlight()`: Directly indicates whether the satellite is in direct sunlight or in Earth's shadow (eclipse).
* `isEarthInSight()`: Directly indicates whether the Earth is visible for communication/payload operations.
* Orbital perturbations are aggregated into a single scalar value returned by `getNaturalDrift()`.

---

## 3.3 Eclipse Mode Behaviour

### Choice

When the satellite enters an eclipse (`isInSunlight() == false`), the Sun alignment error is artificially evaluated to `0.0` by the attitude controller.

### Motivation

When the Sun is occulted by the Earth, a physical Sun sensor reads `0.0`. If compared directly to the desired alignment target ($1.0$), the controller would falsely identify a maximum misalignment error ($1.0$) and continuously fire reaction wheels/thrusters in the dark. 

Forcing the Sun error to `0.0` during eclipses prevents unrealistic, continuous energy drain during orbital nights.

### Consequences

* No attitude corrections are executed for the Sun axis during eclipses.
* The AOCS enters low-power maintenance mode unless Earth-pointing alignment requires correction.

---



## 3.4 Discrete Two-State Power Consumption

### Choice

The AOCS subsystem operates under a simplified two-tier power consumption model:
* **Maintenance Power:** $2.0\text{ W}$ (passive monitoring / idle state).
* **Correction Power:** $15.0\text{ W}$ (active reaction wheel / actuator correction).

### Motivation

Real reaction wheels and control moment gyroscopes vary their power consumption continuously based on angular momentum, motor torque, and wheel speed profiles. Representing power as a binary step function based on error thresholds (`TOLERANCE = 0.05`) simplifies power calculations while accurately capturing active vs. idle power demands on the EPS.

---

## 3.5 Ground-Commanded Orbit Control Model

### Choice

Unlike Attitude Control (which operates autonomously every tick), Orbit Control maneuvers are modeled as **ground-commanded operations** triggered exclusively via telecommands (`requestGroundManeuver()`).

### Motivation

In real satellite missions, orbital drift occurs over extended periods (days or weeks). Orbital correction maneuvers (*station-keeping*) consume critical propellant reserves and alter the trajectory significantly. Consequently, orbit maneuvers are not performed in an autonomous closed-loop by the satellite; they are calculated by ground stations and dispatched via telecommands to be executed by the On-Board Computer (OBC).

### Consequences

* `OrbitControl` tracks orbital drift continuously using a scalar value (`getOrbitalDrift()`).
* Orbital drift increases dynamically at each simulation tick based on environmental perturbations (`env.getNaturalDrift() * DRIFT_FACTOR`).
* No automatic propulsion firing occurs unless an explicit ground command is received.
* Executing a maneuver resets `orbitalDrift` back to `0.0` and consumes the ground command flag.

---

## 3.6 Simplified Propulsion & Fuel Management Model

### Choice

The propulsion system (`IPropulsionSystem`) uses a simplified mass-based fuel model and discrete power states rather than modeling fluid dynamics, chamber pressure, or specific impulse ($I_{sp}$).

### Motivation

Simulating chemical propellant thermodynamics, valve pressures, and thrust vectors would add heavy physical complexity without altering the architectural interactions between the AOCS, the Power system, and the On-Board Computer.

### Consequences

* **Propellant Tracking:** Fuel is represented as a simple scalar mass in kilograms ($kg$), decremented by a fixed cost (`MANEUVER_FUEL_COST = 0.5 kg`) per firing pulse.
* **Fuel Guarding:** Thruster activation is conditional on fuel availability (`fuelLevel >= fuelAmount`). If fuel is exhausted, the thrusters fail to fire cleanly without throwing runtime exceptions.
* **Two-Tier Propulsion Power:**
  * **Standby Power:** $5.0\text{ W}$ (active tank and valve heaters).
  * **Firing Power:** $40.0\text{ W}$ (active thruster pulse and magnetic valves).
* **Inactive State:** When turned OFF (`setActive(false)`), power consumption drops strictly to $0.0\text{ W}$.


---

# 4. Thermal Control System (TCS)

## 4.1 Environmental & Operational Heat Balance Abstraction

### Choice

Thermal dynamics are modeled using discrete scalar temperature variations ($\text{°C/tick}$) driven by sunlight exposure and active heating, rather than solving continuous thermodynamic heat transfer differential equations.

### Motivation

Full-scale spacecraft thermal engineering requires complex finite-element thermal analysis (FEA), modeling conduction, radiative heat exchange ($Q = \epsilon \sigma A T^4$), and view factors. Implementing continuous thermal partial differential equations would introduce significant computational overhead without adding software architecture value.

A scalar heat model satisfies all functional requirements: evaluating environmental impact (heating in sunlight vs. cooling in shadow) and determining when power-consuming heaters must be triggered.

### Consequences

* **Sunlight Heating:** Increases internal temperature by $+0.8\text{ °C}$ per simulation tick when exposed to the Sun (`env.isInSunlight() == true`).
* **Shadow Cooling:** Decreases internal temperature by $-1.0\text{ °C}$ per tick during eclipses (`env.isInSunlight() == false`).
* **Configurable Initial State:** The constructor accepts an explicit initial temperature to test both nominal startup conditions and cold-start emergency scenarios.

---

## 4.2 Hysteresis Threshold Control Loop

### Choice

Active thermal regulation operates using a two-threshold hysteresis control loop (`MIN_SAFE_TEMP = 5.0 °C` and `TARGET_TEMP = 20.0 °C`) instead of proportional-integral-derivative (PID) analog control.

### Motivation

In physical systems, simple single-threshold triggers cause rapid, high-frequency ON/OFF switching (*chatter*) when operating near boundary values. Using an hysteresis band prevents relay chatter, prolongs component lifetime, and provides clean state transitions for discrete software simulation.

### Consequences

* Heaters switch ON automatically when internal temperature drops strictly below $5.0\text{ °C}$.
* Heaters remain active until internal temperature reaches or exceeds the target of $20.0\text{ °C}$.
* Between $5.0\text{ °C}$ and $20.0\text{ °C}$, the system retains its previous heater state.

---

## 4.3 Discrete Thermal Power Tiers

### Choice

The Thermal Control System operates across three discrete power consumption levels:
* **Inactive State (`setActive(false)`):** $0.0\text{ W}$ (system completely powered off).
* **Standby Mode:** $1.0\text{ W}$ (idle monitoring, temperature sensors active).
* **Heater Active Mode:** $25.0\text{ W}$ (active heating elements consuming electrical power from EPS).

### Motivation

Electric resistance heaters consume significant power when energized. Modeling power as discrete states allows the simulation to accurately reflect energy draw on the battery (`IBattery`) through the central `PowerSystemController` during orbital night operations.


---

# 5. Communication System (TT&C)

## 5.1 Environmental Visibility & Contact Window Abstraction

### Choice

Ground-to-satellite communication is modeled using a boolean visibility flag (`isGroundStationInSight()`) provided by the environment, rather than computing orbital line-of-sight geometry or signal attenuation (Friis transmission equation).

### Motivation

Calculating real-time RF propagation loss, Doppler shift, and antenna beam patterns requires heavy orbital geometry and physics simulation. 

For an On-Board Computer (OBC) architectural model, the critical functional requirement is verifying that telemetry transmission and command ingestion are strictly gated by ground station availability. Abstracting radio contact to an environmental visibility check provides an exact interface for mission logic without unnecessary computational overhead.

### Consequences

* Uplink (`receiveCommand`) and Downlink (`sendData`) operations are rejected automatically when `env.isGroundStationInSight()` returns `false`.
* The OBC can only execute ground-driven state changes or offload buffer data during valid visibility windows.

---

## 5.2 Command Ingestion & Telemetry Buffer Management

### Choice

Command uplink and data downlink are implemented using high-level string abstractions (`receiveCommand(String)` and `sendData(String)`) backed by a single-element command buffer (`lastReceivedCommand`).

### Motivation

In flight software, telecommand (TC) and telemetry (TM) frames follow complex packetization standards (e.g., CCSDS). At this stage of system simulation, string-based messaging captures the exact semantic interface required for command routing while avoiding low-level binary bit-parsing complexity.

### Consequences

* `receiveCommand` stores the latest telecommand string received during a pass window.
* `getLastReceivedCommand` allows the Central OBC Controller to poll and execute pending commands.
* Command buffers are safely guarded by `isCommunicationAvailable()` preconditions to prevent ghost execution when out of ground range.

---

## 5.3 Discrete RF Power Consumption Profile

### Choice

The Communication System's power consumption transitions between three discrete operational tiers:
* **Inactive State (`setActive(false)`):** $0.0\text{ W}$ (radio subsystem unpowered).
* **Standby Mode:** $1.0\text{ W}$ (receiver listening continuously for incoming uplink signals).
* **Transmission Mode (`transmitting = true`):** $20.0\text{ W}$ (RF power amplifier energized for telemetry downlink).

### Motivation

Radio transmitters consume significantly more electrical energy when driving power amplifiers during RF modulation compared to idle listening. Modeling distinct standby and transmit power levels allows the Electric Power System (EPS) to dynamically track energy drain during active ground station passes.

### Consequences

* Each simulation tick in `processCommunication` automatically resets `transmitting` to `false`.
* Calling `sendData()` during a valid contact window sets `transmitting = true`, instantly increasing system draw to 20.0for the current tick.



# 6. On-Board Computer (OBC) & Flight Software

## 6.1 Deterministic Sequential Execution Sequence

### Choice

The On-Board Computer (`OBC`) executes its flight loop via a strictly deterministic, 6-phase sequential pipeline per simulation tick:

Time Management------> FDIR ------> Mode Management ------> Command & Control ------> Telemetry & Data ------> Physical Subsystems

### Motivation

In full-scale satellite flight software, execution order is paramount to system safety and causality. Executing tasks out of sequence could lead to race conditions or dangerous operational states—such as executing a high-power ground command right after a critical battery drop, but *before* the FDIR has a chance to trigger safety isolation.

Ordering the flight loop sequentially ensures absolute determinism, making the simulation reproducible, testable, and compliant with formal JML specifications.

### Consequences

* **Phase 1 — Time Management (`onBoardTime++`):** Time is incremented as the very first instruction so that all logs, telemetry packets, and FDIR decisions generated within the tick carry the exact current time tag.
* **Phase 2 — FDIR (`runFDIR`):** Safety monitoring evaluates critical system boundaries (*Watch Points*) before any external commands are processed.
* **Phase 3 — Mode Management (`updateFlightModes`):** State transitions are applied immediately to align hardware operational profiles with the latest safety decisions.
* **Phase 4 — Command & Control (`processTelecommands`):** Ground telecommands are ingested and filtered against active mode constraints (e.g., rejecting payload activation if in `SAFE` mode).
* **Phase 5 — Telemetry & Data Handling (`handleTelemetryAndData`):** Housekeeping packets and science data transfers are packaged using the finalized state of the current tick.
* **Phase 6 — Physical Subsystem Step Execution:** Physical routines (`aocs.processAOCS`, `thermal.processThermal`, etc.) run at the very end of the tick so that hardware energy draw and physics reflect the exact software decisions established during the same cycle.

---

## 6.2 Hysteresis State Machine for Power Recovery

### Choice

Transitions between operational modes—specifically entering and exiting `CHARGE` mode—are governed by an **hysteresis loop** (50.0 entry threshold vs. 80.0 exit threshold).

### Motivation

Using a single battery threshold (e.g., 50.0) for both triggering and clearing power recovery would cause high-frequency mode chatter (rapid toggling between `NOMINAL` and `CHARGE` on consecutive ticks near 50.0%). In real spacecraft engineering, chatter causes relay fatigue, electrical instability, and corrupted payload acquisitions.

Introducing a wide hysteresis band stabilizes mode management and ensures that the power storage system recovers a substantial energy margin before high-power instruments are re-energized.

### Consequences

* **Entry (`BATTERY_LOW = 50.0%`):** If battery charge drops below 50.0% while exposed to sunlight, FDIR shifts `currentMode` to `CHARGE`, shutting down high-power payload instruments.
* **Latching:** The satellite remains locked in `CHARGE` mode as battery level recovers through 50.0%, 60.0%, and 70.0%.
* **Exit (`BATTERY_RECHARGED = 80.0%`):** Mode Management transitions back to `NOMINAL` strictly when battery charge reaches or exceeds 80.0%.

---

## 6.3 Immutable Housekeeping Telemetry Snapshots

### Choice

Telemetry parameter collection within the OBC uses an immutable record data structure (`HousekeepingData`).

### Motivation

During a flight loop execution cycle, multiple software routines (FDIR, telemetry formatters, mode managers) require access to health metrics. If underlying subsystem states were modified concurrently or mid-tick by side effects, different tasks within the same tick would evaluate inconsistent data.

Capturing an immutable snapshot at the beginning of the FDIR phase guarantees data consistency across all software evaluations within that simulation tick.

### Consequences

* `collectHousekeeping(env)` returns a frozen, read-only snapshot of all subsystem states (SoC, temperature, memory, AOCS alignment error).
* Subsystem mutations occurring later in the tick do not alter the active snapshot being evaluated by the FDIR or telemetry encoder.

---

## 6.4 Bounded Science Data Downlink Capacity

### Choice

Payload science memory offloading is constrained by a fixed maximum downlink transfer rate per tick (`MAX_DOWNLINK_RATE_PER_TICK = 10.0 MB`).

### Motivation

In physical space missions, onboard mass memory cannot be emptied instantaneously upon establishing contact with a ground station due to finite RF bandwidth and channel throughput. 

Modeling a bounded downlink rate forces the simulation to accurately reflect progressive data offloading across finite ground station pass windows.

### Consequences

* Mass memory (`payload.getStoredDataSize()`) is cleared progressively via `payload.clearData(dataToTransfer)` over multiple ticks during a pass.
* Telemetry packets accurately track remaining onboard storage (`REMAINING`) during active downlink passes.

---

## 6.5 Subsystem Aggregation via the AOCS Facade

### Choice

The OBC interacts with the Attitude and Orbit Control System exclusively through a unified facade interface (`IAOCS`).

### Motivation

Decoupling the central flight computer from individual sensors and actuators (`SunSensor`, `EarthSensor`, `AttitudeControl`, `OrbitControl`, `PropulsionSystem`) enforces clean architectural boundaries. The OBC requires high-level operational health indicators and control handles rather than direct manipulation of sensor electronics.

### Consequences

* The OBC queries high-level pointing metrics directly (`aocs.getAttitudeAlignmentError()`, `aocs.isAttitudeStable()`) for FDIR gating and command validation.
* The flight loop invokes a single physical update method (`aocs.processAOCS(env)`), leaving sub-component coordination to the `AOCS` facade.

---

## 6.6 Explicit OBC Computer Base Power Draw

### Choice

The central flight computer processor is assigned an explicit, constant base power consumption (`OBC_POWER_DRAW = 3.0 W`).

### Motivation

The On-Board Computer hardware (CPU, system RAM, supervisor circuits) continuously draws electrical power regardless of whether payload instruments or transmitters are active. 

Including `OBC_POWER_DRAW` in the global energy balance ensures realistic baseline battery depletion during idle or `SAFE` mode operations.
