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
