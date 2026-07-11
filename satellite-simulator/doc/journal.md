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
