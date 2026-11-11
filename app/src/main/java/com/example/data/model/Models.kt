package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NodeStatus {
    HEALTHY,
    WARNING,
    EMERGENCY,
    OFFLINE
}

enum class IncidentSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class IncidentStatus {
    ACTIVE,
    ACKNOWLEDGED,
    DISPATCHED,
    RESOLVED
}

enum class LightingMode {
    AUTOMATIC,
    MANUAL,
    EMERGENCY_OVERRIDE,
    SCHEDULED
}

enum class TelemetryEventType {
    MOTION_DETECTED,
    SOS_TRIGGERED,
    LUX_CHANGED,
    LIGHT_ADJUSTED,
    HEARTBEAT,
    NODE_OFFLINE,
    NODE_RESTORED,
    NETWORK_STATUS
}

@Entity(tableName = "nodes")
data class IotNode(
    @PrimaryKey val id: String,
    val name: String,
    val zone: String,
    val locationDescription: String,
    val posX: Float, // Normalized 0.0f - 1.0f on campus map
    val posY: Float, // Normalized 0.0f - 1.0f on campus map
    val status: NodeStatus,
    val batteryPercent: Int,
    val solarVoltage: Float,
    val isCharging: Boolean,
    val ambientLux: Float,
    val motionDetected: Boolean,
    val lightBrightness: Int, // 0 - 100%
    val isEmergencyStrobe: Boolean,
    val rssi: Int, // dBm, e.g. -58
    val lastSeenEpochMs: Long,
    val macAddress: String,
    val firmwareVersion: String,
    val neighborNodeIds: List<String> = emptyList()
)

@Entity(tableName = "incidents")
data class EmergencyIncident(
    @PrimaryKey val id: String,
    val originatingNodeId: String,
    val location: String,
    val timestampEpochMs: Long,
    val severity: IncidentSeverity,
    val status: IncidentStatus,
    val responderName: String = "",
    val resolutionNotes: String = "",
    val activatedNodeIds: List<String> = emptyList()
)

@Entity(tableName = "telemetry_logs")
data class TelemetryLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nodeId: String,
    val timestampEpochMs: Long,
    val eventType: TelemetryEventType,
    val lux: Float = 0f,
    val brightness: Int = 0,
    val battery: Int = 0,
    val message: String
)

data class MeshNetworkState(
    val wanConnected: Boolean = true,
    val localGatewayConnected: Boolean = true,
    val gatewayIp: String = "192.168.4.1",
    val activeMeshChannel: Int = 7,
    val packetsTransmitted: Long = 14820,
    val packetDeliveryRate: Float = 99.4f,
    val duplicatePacketsFiltered: Long = 342,
    val averageLatencyMs: Int = 18,
    val encryptionEnabled: Boolean = true,
    val espNowPeersCount: Int = 5
)

data class LightingConfig(
    val mode: LightingMode = LightingMode.AUTOMATIC,
    val ambientLuxThreshold: Float = 45f,
    val motionTimeoutSeconds: Int = 20,
    val standbyBrightness: Int = 20,
    val activeBrightness: Int = 90
)

enum class SimulationScenario {
    NONE,
    SCENARIO_A_PEDESTRIAN_WALK,
    SCENARIO_B_LOW_LIGHT,
    SCENARIO_C_EMERGENCY_SOS,
    SCENARIO_D_WAN_DISCONNECT,
    SCENARIO_E_NODE_FAILURE,
    SCENARIO_F_FULL_SHOWCASE
}

data class SimulationStatus(
    val activeScenario: SimulationScenario = SimulationScenario.NONE,
    val isRunning: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
    val title: String = "Simulator Idle",
    val description: String = "Select a scenario or trigger manual environmental events."
)
