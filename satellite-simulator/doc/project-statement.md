
# Project Statement

## Purpose

This project aims to simulate the lifecycle and operations of an Earth-orbiting satellite, its On-Board Computer (OBC), and a ground-based Mission Operations Center (MOC), alongside the communication link that connects them.

The goal is not to reproduce a real, hyper-complex spacecraft system, but to model the structural states, logical behaviors, and data exchanges of these entities. By doing so, I aim to understand how flight software interacts with space environments and ground crews.

The scope of the project follows a highly modular design and will be expanded iteratively step by step.

---

## System Architecture Overview

The simulation is architected around four core pillars:

1. **The Satellite:** Composed of the centralized **On-Board Computer (OBC)** acting as the flight software brain, and the **Hardware Subsystems** (Power, Payload, Thermal, ADCS, and Comm Systems).
2. **The Environment:** A physics and time simulation engine that drives external inputs like orbital positioning, sun visibility (eclipses), and thermal variations.
3. **The Mission Operations Center (MOC):** The ground control station interface used by human operators to upload telecommands and monitor spacecraft health.
4. **The Communication Link:** A network layer (built using Java Sockets) that acts as the radio frequency bridge delivering telemetry up and telecommands down.

---

## Personal and Educational Objectives

This personal project serves multiple technical and professional goals:

* **Mastering Object-Oriented Programming (OOP):** Implementing this architecture in Java helps me deepen my understanding of design patterns, interfaces, abstraction, data structures, and multithreading.
* **Aerospace Passion:** Designing a satellite simulator from scratch feeds my curiosity about space systems, translating academic computer science concepts into a practical project aligned with my passion.
* **Dual-Skill Training (Coding & Language):** To prepare for a global tech or aerospace career, this entire project—including documentation, code design, and logs—is written and developed in English to improve my technical language skills.

