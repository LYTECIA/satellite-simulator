# Satellite & Mission Control Simulator

This project is a Java-based discrete-time simulation of an **Earth Orbiting Satellite**, its **On-Board Computer (OBC)**, the **Environment (Space Physics)**, and the **Mission Control Center (MCC)** on Earth. 

The goal of this project is to model real spacecraft software architecture, data handling, and communication links.

---

## 🌌 Global System Architecture

The project is divided into four major pillars:

1. **The Satellite:** The physical spacecraft containing the hardware and the flight software (OBC).
2. **The Environment:** The physics engine simulating orbital mechanics, time progression, sun position, and thermal cycles.
3. **The Mission Control Center (MCC):** The ground station interface used by operators to send commands and monitor telemetry.
4. **The Communication Link:** The network bridge (Java Sockets) simulating radio frequency communication between the MCC and the OBC.

---

## 🛰️ Spacecraft Architecture (The Satellite)

The satellite is split into two distinct layers: **The Flight Software (OBC)** which manages logic and decisions, and the **Hardware Subsystems** which act as the physical components.

## 1. Hardware Subsystems
*   **The Power System (EPS):** Manages energy collection and storage.
    *   *Components:* Battery, Solar Panels.
*   **The Payload System:** The core mission instrument used to collect scientific or image data.
    *   *Components:* Instrument (e.g., Camera/Sensor), Data Storage (Mass Memory).
*   **The Thermal System:** Regulates the satellite's internal temperature against space extremes.
    *   *Components:* Temperature Sensors, Heaters.
*   **The Attitude & Orbit Control System (ADCS):** Manages the satellite's orientation and path.
    *   *Components:* Attitude/Sun Sensors, Actuators (Reaction Wheels/Thrusters).
*   **The Communication System:** Handles physical radio broadcast and reception.
    *   *Components:* Transceiver, Antennas.

### 2. On-Board Computer (OBC) Responsibilities
The OBC runs a continuous flight control loop executing five critical software tasks:

*   **Housekeeping & FDIR (Fault Detection, Isolation, and Recovery):** Periodically monitors all sensor data (voltages, temperatures) and safely handles anomalies or hardware failures.
*   **Command & Control:** Decodes incoming telecommands from Earth, validates them against safe operating constraints, and schedules them for execution.
*   **Telemetry & Data Handling:** Gathers operational logs, sensor statuses, and mission data, packing them into telemetry packets for downlink.
*   **Mode Management:** Maintains the overall operational state machine of the satellite (e.g., `BOOT`, `NOMINAL`, `CHARGE`, `SAFE`).
*   **Time Management:** Manages On-Board Time (OBT) synchronization, orbital step pacing, and event scheduling.


