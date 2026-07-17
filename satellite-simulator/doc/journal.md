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