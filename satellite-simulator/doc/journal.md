# Development Journal & Learning Log

This file tracks my progress, design decisions, technical challenges, and key learnings throughout the development of the Satellite Simulator.

---

## 📝 Entry 1: Architecture & The Dependency Challenge
**Date:** 10 July 2026

### What I Did:
1. Sketched the global system architecture on paper, identifying the 4 major pillars: Satellite, Environment, Mission Operations Center (MOC), and the Communication Link.
2. Mapped out the inner layers of the Satellite: the software brain (OBC) and the 5 physical hardware subsystems.
3. Created the project structure in Eclipse and set up the `satellite.model` source package.

### Challenges & Insights (What I Realized):
* **The Top-Down Block:** My initial plan was to immediately write the Java interface and formal contracts for the main `ISatellite` object. 
* **The Dependency Trap:** While drafting the postconditions for the `ISatellite` constructor, I noticed that the satellite's state depends heavily on the states of its components (e.g., checking if `getPowerSystem().getBatteryLevel() == 100.0`). 
* **The Pivot:** I realized I couldn't rigorously define the `ISatellite` interface contracts without first defining what the subsystems can actually do. 

### Next Steps:
Instead of forcing a top-down design, I am pivoting to a **bottom-up approach**. I will architect and implement individual hardware subsystem interfaces (starting with the Power System) before returning to tie them all together inside the master `ISatellite` interface.

---

## 📝 Entry 2: Designing the Battery Subsystem & Overcoming Numerical Drift
**Date:** 14 July 2026

### What I Did:
1. Initiated the bottom-up approach by designing the `IBattery` interface and its concrete implementation `Battery`.
2. Documented the interface using formal **JML (Java Modeling Language)** specifications (`@invariant`, `/*@ pure @*/`, `@requires`, `@ensures`) to strictly separate queries from commands.
3. Fully implemented the battery behavior with runtime defensive checks (`AssertionError`) to enforce contract compliance.

### Challenges & Insights (What I Realized):

* **The Danger of `double` and `==`:** 
  I learned that the binary representation of floating-point numbers (IEEE 754 standard) cannot precisely represent certain decimal values (like `0.1`). Therefore, cumulative calculations like `chargeLevel += amount` introduce micro-imprecisions. Using a strict `==` on calculated `double` values is highly risky because a value like `100.00000000000001` would cause `isFull()` to evaluate to `false`.

* **Boundary Checks over Epsilon:** 
  While the standard fix for floating-point equality is comparing an absolute difference to a tiny margin (`Math.abs(a - b) < epsilon`), I realized that for a battery ceiling, a directional boundary check (`>=`) is cleaner and more robust since the business logic dictates the battery charges upwards toward a fixed maximum limit.

* **Preventing Numerical Drift with Clamping:**
  Even with `>=` operators, allowing floating-point noise to accumulate over thousands of simulation cycles would corrupt the state. To eliminate this "numerical drift", I implemented **clamping** in the `charge` and `discharge` commands using `Math.min` and `Math.max`. This forcibly resets the value back to exactly `100.0` or `0.0`.
  
* **The JML Tooling Paradigm:** 
  I discovered the power of formal methods. Adding `/*@ pure @*/` to queries guarantees they are side-effect free, meaning they only read the state without altering it. This ensures they can be evaluated safely within contracts without accidentally draining or corrupting the system's state.  

### 🛠️ Tooling & IDE Troubleshooting:
* **Eclipse Build Path Corruption:** 
  Ran into a misleading `"cannot be resolved to a type"` error on the standard `AssertionError` class. I discovered that moving project folders can corrupt Eclipse's Build Path, occasionally dropping the **JRE System Library** from the `Modulepath`. 
  * *Resolution:* Restored it via Project Properties -> Java Build Path -> Libraries -> Selected Modulepath -> Add Library -> Re-added JRE System Library, which cleared all ghost compilation errors.

### Next Steps:
Now that the battery is solid, fully specified, and safely protected against numerical drift, I am ready to design the next critical component of the Power System: the **Solar Panels (`ISolarPanel`)**, incorporating their deployment mechanics and energy production logic.


---

## 📝 Entry 3: Solar Array Design & Functional Domain Modeling
**Date:** 18 July 2026

### What I Did:
1. Designed and implemented the `ISolarPanel` interface along with its concrete `SolarPanel` class.
2. Formulated strict JML behavioral contracts to mirror the physical mechanics of stowed versus deployed states.
3. Decoupled the solar panel component from any heavy external environmental dependencies (`Environment` or `Sun` classes) by using a push-based data approach (`double sunEnergy`).

### Challenges & Insights (What I Realized):

* **The Launch Lifecycle Constraints:**
  Satellites are packed tightly into rocket fairings, meaning solar arrays must start the simulation completely stowed (`deployed = false`). The constructor explicitly enforces this launch state, ensuring that the initial energy production rate is strictly locked at `MIN_ENERGY`.

* **Defining "Functional" Operations:**
  I initially wrestled with what it truly means for a subsystem to be "functional." While one might think it means "not broken," in space operations, a component is only functional if it is actively delivering utility. I refined the `isFunctional()` query to evaluate an operational status: the panel is functional if and only if it is fully deployed **AND** actively receiving solar energy. If the satellite enters an eclipse (Earth's shadow, where `sunEnergy == 0.0`), the panel instantly flags itself as non-functional.

* **Decoupling vs. Dependency (The Control Flow):**
  I realized a vital architectural pattern: the solar panel shouldn't "know" or poll the environment for sun status. By designing `updateProduction(double sunEnergy)` to accept a primitive value instead of an `Environment` object reference, the class remains entirely modular, loosely coupled, and simple to unit-test in isolation. The master simulator loop will handle the physics calculations and pass the resulting power value down.

### Next Steps:
With the individual power generation (`SolarPanel`) and power storage (`Battery`) building blocks now completely specified and implemented, the next objective is to construct the central supervisor: the **Power Management System (PMS)**, which will coordinate distribution between these components.


---

## 📝 Entry 4: Power System Controller & Architectural Refinements
**Date:** 19 July 2026

### What I Did:
1. Designed and implemented the `IPowerSystemController` interface and its concrete `PowerSystemController` class to bridge solar production and battery storage.
2. Strictly applied the **Command-Query Separation (CQS)** principle by keeping the state-updating method (`processEnergyBalance`) as a `void` command and exposing its results via a dedicated pure query (`getGrantedEnergy()`).
3. Resolved a critical units mismatch and boundary condition (*edge case*) regarding battery overcharging.

### Challenges & Insights (What I Realized):

* **The Command-Query Separation (CQS) Imperative:**
  Initially, I considered having the energy balance method return the allocated wattage directly. However, I realized this violated CQS by combining a state mutation with a query. By splitting this into a `void processEnergyBalance(double)` command and a `double getGrantedEnergy()` query, the code remains clean, modular, and fully compliant with strict JML analytical tools.

* **The Overcharge Defensiveness (Clamping):**
  A critical logical flaw was discovered: if the solar surplus exceeded the remaining physical space in the battery, a naive charge call would violate the battery's strict JML preconditions and crash the simulation. I introduced a clamping mechanism using `Math.min(surplus, emptySpace)` to gracefully dissipate excess power once the battery hits maximum capacity.

* **Dimensional Analysis & Dimensional Alignment:**
  I noticed a conceptual mismatch between the battery state (originally thought of as a percentage from 0 to 100) and the solar/system flow (expressed in Watts). To maintain physical and mathematical integrity without adding verbose conversion layers, I established a project-wide convention: **all energy metrics are unified under Watts (W)**. The battery's `MAX_CHARGE_LEVEL` of 100 represents 100 Watts, which naturally maps to a 100% full state.

### Next Steps:
With the entire energy subsystem specified, implemented, and mathematically secured, the power framework is complete. The next phase will involve developing **the Orbit and Attitude System **.

