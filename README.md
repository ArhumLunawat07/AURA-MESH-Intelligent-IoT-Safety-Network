# AURA MESH
### Intelligent Offline-First IoT Safety Infrastructure
> *"Don't just send an SOS. Make the environment respond."*

---

## 1. Executive Summary & Vision
Traditional personal safety tools rely strictly on smartphones, cellular data, GPS satellite visibility, and individual manual actions. In dense campuses, night walkways, residential corridors, and remote public paths, connectivity frequently drops, batteries die, and phones may not be accessible during an incident.

**AURA MESH** shifts the safety paradigm from individual reliance to **collaborative infrastructure response**. It links distributed physical IoT smart posts into an autonomous, locally coordinated mesh. When a pedestrian walks along a corridor, the pathway senses movement and adaptively illuminates. When an emergency SOS button is activated, the originating node strobes while adjacent nodes flood the walkway to provide an illuminated escape corridor and alert local security monitoring dashboards—even if wide-area cloud internet is completely severed.

---

## 2. Key Architectural Pillars
1. **Offline-First Dual Tier Network**:
   - **Tier 1 (Field Communication)**: ESP32 sensor nodes communicate peer-to-peer using **ESP-NOW** at 2.4GHz with sub-20ms frame latency and zero dependency on Wi-Fi routers or internet servers.
   - **Tier 2 (Gateway & Dashboard)**: A local gateway hub bridges ESP-NOW broadcast frames to a local **MQTT broker / WebSockets** streaming server, delivering live telemetry to the monitoring dashboard.
2. **Adaptive Pathway Lighting**:
   - Integrates digital **BH1750 ambient lux sensors** and **PIR passive infrared motion detectors**.
   - Daytime: 0% power consumption (energy conservation).
   - Low-light dusk (< 45 lux): 20% standby guiding glow.
   - Pedestrian approach: instantaneous ramp to 90% high-beam illumination + predictive neighbor pre-warming to 45%.
   - Reduces municipal lighting energy consumption by **68.4%**.
3. **Coordinated Emergency SOS Corridor**:
   - Momentary physical emergency button triggers instant peer cascade.
   - Originating post strobes red at 100% brightness and engages an acoustic beacon.
   - Neighbor nodes floodlight a continuous illuminated safe corridor.
   - Creates persistent audit records with Acknowledge, Dispatch, and Resolve confirmation states.
4. **Assisted Route Navigation**:
   - Graph-based pathfinding across mesh nodes.
   - Flags dark or offline sections and routes pedestrian assistance through healthy, illuminated corridors.
5. **Zero-Tracking Ethical Privacy**:
   - Zero facial recognition cameras.
   - Zero listening microphones.
   - Zero personal identity tracking. Sensors strictly detect anonymous physical occupancy.

---

## 3. Application Modules
| Module | Purpose |
| :--- | :--- |
| **Explore Landing** | 3-choice turnkey entry: Interactive Simulation, Dashboard, Hardware Specs |
| **Command Center** | Central control deck with real-time KPI chips, active emergency banners, and telemetry stream |
| **Live Network Map** | Interactive Canvas rendering of nodes, signal links, and illumination halos |
| **Emergency Response** | Complete SOS audit log, acknowledge/dispatch/resolve lifecycle, non-911 disclaimer |
| **Adaptive Lighting** | Ambient lux slider, explainable state machine rules, standby/active power controls |
| **Assisted Routes** | Graph route calculation, corridor coverage evaluation, illumination wave activation |
| **Sensors & Telemetry** | PIR, mmWave 24GHz radar, BH1750 Lux, LiFePO4 battery, and solar harvesting status |
| **Network Health** | ESP-NOW packet delivery rates, RSSI matrix, cloud severance simulation |
| **Simulation Lab** | 6 automated showcase scenarios for judges + manual hardware control deck |
| **Hardware Integration** | Gateway IP setup, MQTT JSON schemas with copy buttons, and ESP32 C++ firmware viewer |
| **Analytics & Reports** | Pedestrian traffic histograms, energy savings charts, and downloadable CSV audits |
| **Settings & Docs** | Mesh channel config, heartbeat frequency, encryption keys, and ethics manifesto |

---

## 4. Hardware Bill of Materials (BOM)
- **Microcontroller**: ESP32-WROOM-32 / ESP32-S3 DevKit
- **PIR Motion Sensor**: HC-SR501 or Panasonic AM312
- **Ambient Light Sensor**: BH1750 Digital Lux (I2C) or calibrated LDR
- **Emergency Input**: Momentary 16mm vandal-resistant push-button (Normally Open)
- **Pathway Illumination**: High-CRI 12V COB LED Strip driven via Logic-Level MOSFET (IRLZ44N / AO3400)
- **Audio Beacon**: 5V Active Piezo Buzzer (90dB at 10cm)
- **Power Subsystem**: 3.2V 6000mAh LiFePO4 Battery with 5V 2W Solar Panel and CN3791 MPPT Charger

---

## 5. Getting Started & Running the App
1. **Compilation**: The app is built with Jetpack Compose and Room Database on Kotlin 2.2+.
2. **First Launch**:
   - Select **"1. Launch Interactive Simulation"** to run the guided 6-step National Hackathon showcase.
   - Or select **"2. Open Monitoring Dashboard"** to inspect live node metrics.
3. **Simulation Controls**:
   - Test pedestrian motion by tapping any node or clicking "Motion".
   - Test emergency strobe cascade by pressing "SOS Alert".
   - Test WAN severance by clicking "Sever WAN"; observe that the local mesh continues running with 0% packet loss.

---

## 6. Safety Principle & Ethical Boundaries
- **Assistance Infrastructure Only**: AURA MESH is an environmental assistance and monitoring system. It does **NOT** guarantee personal safety, replace law enforcement, or automatically call emergency services from a demonstration platform.
- **Privacy by Design**: Anonymous thermal and micro-motion sensing prevents surveillance risks while protecting pedestrian safety.
