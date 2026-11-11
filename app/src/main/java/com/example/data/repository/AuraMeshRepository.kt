package com.example.data.repository

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.local.AuraMeshDatabase
import com.example.data.model.EmergencyIncident
import com.example.data.model.IncidentSeverity
import com.example.data.model.IncidentStatus
import com.example.data.model.IotNode
import com.example.data.model.LightingConfig
import com.example.data.model.LightingMode
import com.example.data.model.MeshNetworkState
import com.example.data.model.NodeStatus
import com.example.data.model.SimulationScenario
import com.example.data.model.SimulationStatus
import com.example.data.model.TelemetryEventType
import com.example.data.model.TelemetryLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class AuraMeshRepository(
    private val context: Context,
    private val database: AuraMeshDatabase
) {
    private val nodeDao = database.nodeDao()
    private val incidentDao = database.incidentDao()
    private val telemetryDao = database.telemetryDao()

    private val repositoryScope = CoroutineScope(Dispatchers.IO + Job())
    private var simulationJob: Job? = null
    private var motionResetJobs = mutableMapOf<String, Job>()

    private val _networkState = MutableStateFlow(MeshNetworkState())
    val networkState: StateFlow<MeshNetworkState> = _networkState.asStateFlow()

    private val _lightingConfig = MutableStateFlow(LightingConfig())
    val lightingConfig: StateFlow<LightingConfig> = _lightingConfig.asStateFlow()

    private val _simulationStatus = MutableStateFlow(SimulationStatus())
    val simulationStatus: StateFlow<SimulationStatus> = _simulationStatus.asStateFlow()

    // Global ambient lux level (simulated or sensor-driven)
    private val _globalAmbientLux = MutableStateFlow(24f)
    val globalAmbientLux: StateFlow<Float> = _globalAmbientLux.asStateFlow()

    val allNodes: Flow<List<IotNode>> = nodeDao.getAllNodes()
    val allIncidents: Flow<List<EmergencyIncident>> = incidentDao.getAllIncidents()
    val activeIncidents: Flow<List<EmergencyIncident>> = incidentDao.getActiveIncidents()
    val recentTelemetry: Flow<List<TelemetryLog>> = telemetryDao.getRecentTelemetry()

    init {
        repositoryScope.launch {
            seedInitialNodesIfEmpty()
        }
    }

    private suspend fun seedInitialNodesIfEmpty() {
        if (nodeDao.getNodeCount() == 0) {
            val initialNodes = listOf(
                IotNode(
                    id = "NODE-01",
                    name = "West Gate Post",
                    zone = "Zone A - West Perimeter",
                    locationDescription = "West Campus Entrance Walkway & Bike Path",
                    posX = 0.16f,
                    posY = 0.26f,
                    status = NodeStatus.HEALTHY,
                    batteryPercent = 94,
                    solarVoltage = 4.12f,
                    isCharging = true,
                    ambientLux = 24f,
                    motionDetected = false,
                    lightBrightness = 20,
                    isEmergencyStrobe = false,
                    rssi = -54,
                    lastSeenEpochMs = System.currentTimeMillis() - 2500,
                    macAddress = "24:6F:28:B4:71:01",
                    firmwareVersion = "v2.4.1-espnow",
                    neighborNodeIds = listOf("NODE-02", "NODE-03")
                ),
                IotNode(
                    id = "NODE-02",
                    name = "Central Quad Spire",
                    zone = "Zone A - Central Hub",
                    locationDescription = "Main Pedestrian Plaza & Student Union Crossing",
                    posX = 0.44f,
                    posY = 0.36f,
                    status = NodeStatus.HEALTHY,
                    batteryPercent = 88,
                    solarVoltage = 3.98f,
                    isCharging = true,
                    ambientLux = 24f,
                    motionDetected = false,
                    lightBrightness = 20,
                    isEmergencyStrobe = false,
                    rssi = -48,
                    lastSeenEpochMs = System.currentTimeMillis() - 1100,
                    macAddress = "24:6F:28:B4:71:02",
                    firmwareVersion = "v2.4.1-espnow",
                    neighborNodeIds = listOf("NODE-01", "NODE-03", "NODE-04")
                ),
                IotNode(
                    id = "NODE-03",
                    name = "Library Promenade",
                    zone = "Zone B - Library Path",
                    locationDescription = "North Quad shaded walkway outside Science Library",
                    posX = 0.30f,
                    posY = 0.68f,
                    status = NodeStatus.HEALTHY,
                    batteryPercent = 92,
                    solarVoltage = 4.05f,
                    isCharging = true,
                    ambientLux = 22f,
                    motionDetected = false,
                    lightBrightness = 20,
                    isEmergencyStrobe = false,
                    rssi = -61,
                    lastSeenEpochMs = System.currentTimeMillis() - 3200,
                    macAddress = "24:6F:28:B4:71:03",
                    firmwareVersion = "v2.4.1-espnow",
                    neighborNodeIds = listOf("NODE-01", "NODE-02", "NODE-05")
                ),
                IotNode(
                    id = "NODE-04",
                    name = "Science Courtyard",
                    zone = "Zone C - Science Complex",
                    locationDescription = "Engineering & Physics Lab Courtyard Spine",
                    posX = 0.76f,
                    posY = 0.40f,
                    status = NodeStatus.HEALTHY,
                    batteryPercent = 81,
                    solarVoltage = 3.89f,
                    isCharging = false,
                    ambientLux = 25f,
                    motionDetected = false,
                    lightBrightness = 20,
                    isEmergencyStrobe = false,
                    rssi = -59,
                    lastSeenEpochMs = System.currentTimeMillis() - 1800,
                    macAddress = "24:6F:28:B4:71:04",
                    firmwareVersion = "v2.4.1-espnow",
                    neighborNodeIds = listOf("NODE-02", "NODE-05")
                ),
                IotNode(
                    id = "NODE-05",
                    name = "East Residence Path",
                    zone = "Zone D - Dormitory Spine",
                    locationDescription = "East Dormitory Walkway & Dining Hall Path",
                    posX = 0.68f,
                    posY = 0.78f,
                    status = NodeStatus.HEALTHY,
                    batteryPercent = 78,
                    solarVoltage = 3.75f,
                    isCharging = false,
                    ambientLux = 21f,
                    motionDetected = false,
                    lightBrightness = 20,
                    isEmergencyStrobe = false,
                    rssi = -64,
                    lastSeenEpochMs = System.currentTimeMillis() - 4100,
                    macAddress = "24:6F:28:B4:71:05",
                    firmwareVersion = "v2.4.1-espnow",
                    neighborNodeIds = listOf("NODE-03", "NODE-04")
                )
            )
            nodeDao.insertOrUpdateNodes(initialNodes)

            // Seed historical telemetry
            val now = System.currentTimeMillis()
            val initialLogs = listOf(
                TelemetryLog(
                    nodeId = "GATEWAY",
                    timestampEpochMs = now - 600000,
                    eventType = TelemetryEventType.NETWORK_STATUS,
                    message = "AURA MESH Local Gateway initialized. 5 ESP-NOW peers verified on Channel 7."
                ),
                TelemetryLog(
                    nodeId = "NODE-01",
                    timestampEpochMs = now - 340000,
                    eventType = TelemetryEventType.HEARTBEAT,
                    lux = 24f,
                    brightness = 20,
                    battery = 94,
                    message = "Heartbeat ACK: Battery 94%, Solar 4.12V, RSSI -54dBm"
                ),
                TelemetryLog(
                    nodeId = "NODE-02",
                    timestampEpochMs = now - 180000,
                    eventType = TelemetryEventType.MOTION_DETECTED,
                    lux = 24f,
                    brightness = 90,
                    battery = 88,
                    message = "PIR Trigger: Pedestrian approaching central plaza. Illumination ramped to 90%."
                ),
                TelemetryLog(
                    nodeId = "NODE-02",
                    timestampEpochMs = now - 150000,
                    eventType = TelemetryEventType.LIGHT_ADJUSTED,
                    lux = 24f,
                    brightness = 20,
                    battery = 88,
                    message = "Dwell timeout elapsed. Pathway returned to standby 20%."
                )
            )
            initialLogs.forEach { telemetryDao.insertLog(it) }
        }
    }

    // --- EMERGENCY SOS WORKFLOW ---
    fun triggerEmergencySos(originatingNodeId: String, severity: IncidentSeverity = IncidentSeverity.CRITICAL) {
        repositoryScope.launch {
            val origin = nodeDao.getNodeById(originatingNodeId) ?: return@launch
            val incidentId = "INC-${SimpleDateFormat("yyMMdd-HHmmss", Locale.US).format(Date())}"
            val now = System.currentTimeMillis()

            // 1. Originating node strobes in emergency mode
            val updatedOrigin = origin.copy(
                status = NodeStatus.EMERGENCY,
                lightBrightness = 100,
                isEmergencyStrobe = true,
                lastSeenEpochMs = now
            )
            nodeDao.insertOrUpdateNode(updatedOrigin)

            // 2. Cascade safe corridor illumination to neighbors
            val activatedNeighborIds = origin.neighborNodeIds
            activatedNeighborIds.forEach { neighborId ->
                val neighbor = nodeDao.getNodeById(neighborId)
                if (neighbor != null && neighbor.status != NodeStatus.OFFLINE) {
                    nodeDao.insertOrUpdateNode(
                        neighbor.copy(
                            lightBrightness = 100,
                            lastSeenEpochMs = now
                        )
                    )
                }
            }

            // 3. Create persistent incident record
            val allActivated = listOf(originatingNodeId) + activatedNeighborIds
            val incident = EmergencyIncident(
                id = incidentId,
                originatingNodeId = originatingNodeId,
                location = "${origin.name} (${origin.zone})",
                timestampEpochMs = now,
                severity = severity,
                status = IncidentStatus.ACTIVE,
                activatedNodeIds = allActivated
            )
            incidentDao.insertIncident(incident)

            // 4. Log telemetry
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = originatingNodeId,
                    timestampEpochMs = now,
                    eventType = TelemetryEventType.SOS_TRIGGERED,
                    brightness = 100,
                    battery = origin.batteryPercent,
                    message = "CRITICAL SOS ACTIVATED at $originatingNodeId. Mesh broadcast cascaded to: ${activatedNeighborIds.joinToString()}"
                )
            )

            // 5. Update mesh packet stats
            _networkState.value = _networkState.value.copy(
                packetsTransmitted = _networkState.value.packetsTransmitted + 14,
                packetDeliveryRate = 99.8f
            )

            // 6. Provide tactile feedback
            triggerHapticFeedback()
        }
    }

    fun acknowledgeIncident(incidentId: String, responderName: String = "Campus Security Unit Alpha") {
        repositoryScope.launch {
            incidentDao.updateIncidentStatus(incidentId, IncidentStatus.ACKNOWLEDGED.name, responderName)
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = "GATEWAY",
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.NETWORK_STATUS,
                    message = "Incident $incidentId acknowledged by $responderName"
                )
            )
        }
    }

    fun dispatchResponder(incidentId: String, responderName: String = "Campus Security Unit Alpha") {
        repositoryScope.launch {
            incidentDao.updateIncidentStatus(incidentId, IncidentStatus.DISPATCHED.name, responderName)
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = "GATEWAY",
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.NETWORK_STATUS,
                    message = "Responder $responderName dispatched to incident $incidentId scene"
                )
            )
        }
    }

    fun resolveIncident(incidentId: String, notes: String = "All clear verified by patrol unit.") {
        repositoryScope.launch {
            val incidents = incidentDao.getAllIncidents().first()
            val target = incidents.find { it.id == incidentId }

            incidentDao.resolveIncident(incidentId, notes)

            // Reset affected nodes back to healthy standby
            target?.activatedNodeIds?.forEach { nodeId ->
                val node = nodeDao.getNodeById(nodeId)
                if (node != null && node.status != NodeStatus.OFFLINE) {
                    nodeDao.insertOrUpdateNode(
                        node.copy(
                            status = NodeStatus.HEALTHY,
                            lightBrightness = _lightingConfig.value.standbyBrightness,
                            isEmergencyStrobe = false,
                            lastSeenEpochMs = System.currentTimeMillis()
                        )
                    )
                }
            }

            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = "GATEWAY",
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.NETWORK_STATUS,
                    message = "Incident $incidentId RESOLVED: $notes"
                )
            )
        }
    }

    // --- PEDESTRIAN MOVEMENT & ADAPTIVE LIGHTING ---
    fun triggerPedestrianMotion(nodeId: String) {
        repositoryScope.launch {
            val node = nodeDao.getNodeById(nodeId) ?: return@launch
            if (node.status == NodeStatus.OFFLINE) return@launch

            val now = System.currentTimeMillis()
            val targetBrightness = _lightingConfig.value.activeBrightness

            // Activate target node
            nodeDao.insertOrUpdateNode(
                node.copy(
                    motionDetected = true,
                    lightBrightness = targetBrightness,
                    lastSeenEpochMs = now
                )
            )

            // Pre-warm neighbor nodes (predictive lighting 45%)
            node.neighborNodeIds.forEach { neighborId ->
                val neighbor = nodeDao.getNodeById(neighborId)
                if (neighbor != null && neighbor.status == NodeStatus.HEALTHY && !neighbor.motionDetected && !neighbor.isEmergencyStrobe) {
                    nodeDao.insertOrUpdateNode(
                        neighbor.copy(
                            lightBrightness = maxOf(neighbor.lightBrightness, 45)
                        )
                    )
                }
            }

            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = nodeId,
                    timestampEpochMs = now,
                    eventType = TelemetryEventType.MOTION_DETECTED,
                    lux = node.ambientLux,
                    brightness = targetBrightness,
                    battery = node.batteryPercent,
                    message = "Pedestrian motion detected at ${node.name}. Pathway illuminated to $targetBrightness%."
                )
            )

            // Cancel any previous motion timer for this node
            motionResetJobs[nodeId]?.cancel()
            motionResetJobs[nodeId] = repositoryScope.launch {
                delay(_lightingConfig.value.motionTimeoutSeconds * 1000L)
                val currentNode = nodeDao.getNodeById(nodeId)
                if (currentNode != null && !currentNode.isEmergencyStrobe && currentNode.status != NodeStatus.OFFLINE) {
                    val standby = _lightingConfig.value.standbyBrightness
                    nodeDao.insertOrUpdateNode(
                        currentNode.copy(
                            motionDetected = false,
                            lightBrightness = standby
                        )
                    )
                    // Reset neighbors back if not active
                    currentNode.neighborNodeIds.forEach { nId ->
                        val n = nodeDao.getNodeById(nId)
                        if (n != null && !n.motionDetected && !n.isEmergencyStrobe && n.status != NodeStatus.OFFLINE) {
                            nodeDao.insertOrUpdateNode(n.copy(lightBrightness = standby))
                        }
                    }
                }
            }
        }
    }

    fun setGlobalAmbientLux(lux: Float) {
        _globalAmbientLux.value = lux
        repositoryScope.launch {
            nodeDao.updateAllAmbientLux(lux)
            val config = _lightingConfig.value
            if (config.mode == LightingMode.AUTOMATIC) {
                val nodes = nodeDao.getAllNodes().first()
                nodes.forEach { node ->
                    if (node.status != NodeStatus.OFFLINE && !node.isEmergencyStrobe) {
                        val brightness = if (lux < config.ambientLuxThreshold) {
                            if (node.motionDetected) config.activeBrightness else config.standbyBrightness
                        } else {
                            // Daylight energy conservation mode
                            0
                        }
                        nodeDao.updateLightBrightness(node.id, brightness)
                    }
                }
            }
        }
    }

    fun updateLightingConfig(config: LightingConfig) {
        _lightingConfig.value = config
        repositoryScope.launch {
            setGlobalAmbientLux(_globalAmbientLux.value)
        }
    }

    fun setManualNodeBrightness(nodeId: String, brightness: Int) {
        repositoryScope.launch {
            nodeDao.updateLightBrightness(nodeId, brightness.coerceIn(0, 100))
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = nodeId,
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.LIGHT_ADJUSTED,
                    brightness = brightness,
                    message = "Manual brightness override set to $brightness%"
                )
            )
        }
    }

    fun setAllNodesBrightness(brightness: Int) {
        repositoryScope.launch {
            val nodes = nodeDao.getAllNodes().first()
            nodes.forEach { node ->
                if (node.status != NodeStatus.OFFLINE && !node.isEmergencyStrobe) {
                    nodeDao.updateLightBrightness(node.id, brightness.coerceIn(0, 100))
                }
            }
        }
    }

    // --- NODE HEALTH & OFFLINE MESH SIMULATION ---
    fun toggleNodeOffline(nodeId: String) {
        repositoryScope.launch {
            val node = nodeDao.getNodeById(nodeId) ?: return@launch
            val newStatus = if (node.status == NodeStatus.OFFLINE) NodeStatus.HEALTHY else NodeStatus.OFFLINE
            val newBrightness = if (newStatus == NodeStatus.OFFLINE) 0 else _lightingConfig.value.standbyBrightness

            nodeDao.insertOrUpdateNode(
                node.copy(
                    status = newStatus,
                    lightBrightness = newBrightness,
                    motionDetected = false,
                    isEmergencyStrobe = false,
                    lastSeenEpochMs = if (newStatus == NodeStatus.OFFLINE) node.lastSeenEpochMs - 600000 else System.currentTimeMillis()
                )
            )

            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = nodeId,
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = if (newStatus == NodeStatus.OFFLINE) TelemetryEventType.NODE_OFFLINE else TelemetryEventType.NODE_RESTORED,
                    message = if (newStatus == NodeStatus.OFFLINE) "WARNING: $nodeId heartbeat timed out. Marked OFFLINE. Mesh rerouting active."
                              else "HEALTH: $nodeId re-established ESP-NOW mesh handshake. Online."
                )
            )
        }
    }

    fun toggleWanInternet() {
        _networkState.value = _networkState.value.copy(
            wanConnected = !_networkState.value.wanConnected
        )
        repositoryScope.launch {
            val status = if (_networkState.value.wanConnected) "RESTORED" else "SEVERED (OFFLINE-MESH ACTIVE)"
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = "GATEWAY",
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.NETWORK_STATUS,
                    message = "Cloud WAN Internet connection $status. Local ESP-NOW & Local Gateway remain 100% operational."
                )
            )
        }
    }

    fun toggleGatewayConnection() {
        _networkState.value = _networkState.value.copy(
            localGatewayConnected = !_networkState.value.localGatewayConnected
        )
    }

    // --- SAFE ROUTE GRAPH PATHFINDING ---
    data class CalculatedRoute(
        val pathNodeIds: List<String>,
        val totalDistanceMeters: Int,
        val coveragePercent: Int,
        val hasOfflineNode: Boolean,
        val degradedNodeIds: List<String>,
        val safeRouteNotes: String
    )

    suspend fun calculateSafeRoute(originId: String, destinationId: String): CalculatedRoute {
        val allNodesList = nodeDao.getAllNodes().first()
        val nodeMap = allNodesList.associateBy { it.id }

        if (originId == destinationId) {
            return CalculatedRoute(
                pathNodeIds = listOf(originId),
                totalDistanceMeters = 0,
                coveragePercent = 100,
                hasOfflineNode = false,
                degradedNodeIds = emptyList(),
                safeRouteNotes = "Same node selected."
            )
        }

        // BFS traversal preferring operational nodes
        val queue = ArrayDeque<List<String>>()
        val visited = mutableSetOf<String>()
        queue.add(listOf(originId))
        visited.add(originId)

        var foundPath: List<String>? = null

        // Pass 1: Try finding a path strictly through non-offline nodes
        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst()
            val currentNodeId = currentPath.last()

            if (currentNodeId == destinationId) {
                foundPath = currentPath
                break
            }

            val currentNode = nodeMap[currentNodeId] ?: continue
            for (neighbor in currentNode.neighborNodeIds) {
                val neighborNode = nodeMap[neighbor]
                if (neighbor !in visited && neighborNode != null && neighborNode.status != NodeStatus.OFFLINE) {
                    visited.add(neighbor)
                    queue.add(currentPath + neighbor)
                }
            }
        }

        // Pass 2: If no healthy-only path, find any graph path
        var degradedPath = false
        if (foundPath == null) {
            queue.clear()
            visited.clear()
            queue.add(listOf(originId))
            visited.add(originId)

            while (queue.isNotEmpty()) {
                val currentPath = queue.removeFirst()
                val currentNodeId = currentPath.last()

                if (currentNodeId == destinationId) {
                    foundPath = currentPath
                    degradedPath = true
                    break
                }

                val currentNode = nodeMap[currentNodeId] ?: continue
                for (neighbor in currentNode.neighborNodeIds) {
                    if (neighbor !in visited) {
                        visited.add(neighbor)
                        queue.add(currentPath + neighbor)
                    }
                }
            }
        }

        val finalPath = foundPath ?: listOf(originId, destinationId)
        val offlineNodesOnPath = finalPath.filter { nodeMap[it]?.status == NodeStatus.OFFLINE }
        val warningNodesOnPath = finalPath.filter { nodeMap[it]?.status == NodeStatus.WARNING }

        val totalMeters = (finalPath.size - 1).coerceAtLeast(1) * 85
        val healthyCount = finalPath.count { nodeMap[it]?.status == NodeStatus.HEALTHY }
        val coverage = ((healthyCount.toFloat() / finalPath.size) * 100).toInt()

        val notes = when {
            offlineNodesOnPath.isNotEmpty() ->
                "CAUTION: Dark section identified! Node ${offlineNodesOnPath.joinToString()} is offline. Maintain elevated awareness."
            degradedPath ->
                "Assisted route active. Secondary pathway illuminated."
            else ->
                "All nodes operational. Full 100% illumination coverage confirmed along corridor."
        }

        return CalculatedRoute(
            pathNodeIds = finalPath,
            totalDistanceMeters = totalMeters,
            coveragePercent = coverage,
            hasOfflineNode = offlineNodesOnPath.isNotEmpty(),
            degradedNodeIds = offlineNodesOnPath + warningNodesOnPath,
            safeRouteNotes = notes
        )
    }

    fun activateRouteCorridorLighting(nodeIds: List<String>) {
        repositoryScope.launch {
            nodeIds.forEachIndexed { index, nodeId ->
                delay(index * 250L) // Wave cascade
                val node = nodeDao.getNodeById(nodeId)
                if (node != null && node.status != NodeStatus.OFFLINE && !node.isEmergencyStrobe) {
                    nodeDao.insertOrUpdateNode(node.copy(lightBrightness = 95, motionDetected = true))
                }
            }
            telemetryDao.insertLog(
                TelemetryLog(
                    nodeId = "ROUTE",
                    timestampEpochMs = System.currentTimeMillis(),
                    eventType = TelemetryEventType.LIGHT_ADJUSTED,
                    brightness = 95,
                    message = "Assisted Route Corridor illumination wave engaged for: ${nodeIds.joinToString()}"
                )
            )
        }
    }

    // --- AUTOMATED SIMULATION LAB SCENARIOS ---
    fun runSimulationScenario(scenario: SimulationScenario) {
        simulationJob?.cancel()
        simulationJob = repositoryScope.launch {
            when (scenario) {
                SimulationScenario.SCENARIO_A_PEDESTRIAN_WALK -> runScenarioPedestrianWalk()
                SimulationScenario.SCENARIO_B_LOW_LIGHT -> runScenarioLowLight()
                SimulationScenario.SCENARIO_C_EMERGENCY_SOS -> runScenarioEmergencySos()
                SimulationScenario.SCENARIO_D_WAN_DISCONNECT -> runScenarioWanDisconnect()
                SimulationScenario.SCENARIO_E_NODE_FAILURE -> runScenarioNodeFailure()
                SimulationScenario.SCENARIO_F_FULL_SHOWCASE -> runScenarioFullShowcase()
                SimulationScenario.NONE -> {
                    _simulationStatus.value = SimulationStatus()
                }
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.NONE,
            isRunning = false,
            title = "Simulation Paused",
            description = "Simulation halted by user."
        )
    }

    private suspend fun runScenarioPedestrianWalk() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_A_PEDESTRIAN_WALK,
            isRunning = true,
            currentStep = 1,
            totalSteps = 4,
            title = "Scenario A: Pedestrian Movement",
            description = "Pedestrian approaches NODE-01 at West Gate. PIR sensor triggers."
        )
        triggerPedestrianMotion("NODE-01")
        delay(2200)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "Pedestrian moves towards NODE-02. NODE-02 detects motion and ramps up."
        )
        triggerPedestrianMotion("NODE-02")
        delay(2200)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            description = "Pedestrian enters NODE-04 Science Courtyard. Pathway follows seamlessly."
        )
        triggerPedestrianMotion("NODE-04")
        delay(2200)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 4,
            isRunning = false,
            title = "Scenario A Complete",
            description = "Pedestrian safely traversed corridor. Dwell timers will return lights to standby."
        )
    }

    private suspend fun runScenarioLowLight() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_B_LOW_LIGHT,
            isRunning = true,
            currentStep = 1,
            totalSteps = 3,
            title = "Scenario B: Environmental Low Light",
            description = "Dusk falls across campus. Ambient light dropping from 120 lux to 20 lux."
        )
        setGlobalAmbientLux(20f)
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "Ambient light drops below 45 lux threshold. Automatic standby lighting (20%) engages across all nodes."
        )
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            isRunning = false,
            title = "Scenario B Complete",
            description = "Autonomous dusk transition verified. Power saved: 80% compared to legacy high-pressure sodium fixtures."
        )
    }

    private suspend fun runScenarioEmergencySos() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_C_EMERGENCY_SOS,
            isRunning = true,
            currentStep = 1,
            totalSteps = 4,
            title = "Scenario C: Emergency SOS Activation",
            description = "Pedestrian presses physical emergency SOS button on NODE-03 (Library Promenade)."
        )
        delay(1500)
        triggerEmergencySos("NODE-03", IncidentSeverity.CRITICAL)
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "Originating node strobe engaged (100%). Nearby nodes NODE-01, NODE-02, NODE-05 floodlight safe corridor."
        )
        delay(3000)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            description = "Security command dashboard receives broadcast. Audio/tactile alarm and incident logged."
        )
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 4,
            isRunning = false,
            title = "Scenario C Complete",
            description = "Full emergency cascade demonstrated. Acknowledge and resolve incident in the Emergency Center."
        )
    }

    private suspend fun runScenarioWanDisconnect() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_D_WAN_DISCONNECT,
            isRunning = true,
            currentStep = 1,
            totalSteps = 4,
            title = "Scenario D: Internet Disconnection Test",
            description = "Simulating campus wide-area network (WAN) internet failure. Local Gateway remains active."
        )
        if (_networkState.value.wanConnected) {
            toggleWanInternet()
        }
        delay(2000)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "WAN disconnected! Internet status: Severed. Triggering local motion event on NODE-02."
        )
        triggerPedestrianMotion("NODE-02")
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            description = "ESP-NOW peer-to-peer mesh handles local event with 0ms internet dependency. Zero packets lost."
        )
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 4,
            isRunning = false,
            title = "Scenario D Complete",
            description = "Verified offline-first architecture. Mesh operates autonomously without cloud servers."
        )
    }

    private suspend fun runScenarioNodeFailure() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_E_NODE_FAILURE,
            isRunning = true,
            currentStep = 1,
            totalSteps = 3,
            title = "Scenario E: Node Failure Injection",
            description = "Simulating physical damage or power cut on NODE-04 (Science Courtyard)."
        )
        toggleNodeOffline("NODE-04")
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "Heartbeat lost. Dashboard flags NODE-04 as OFFLINE. Mesh recalculates paths to bypass dead node."
        )
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            isRunning = false,
            title = "Scenario E Complete",
            description = "Mesh resilience verified. Assisted route planner automatically flags dark zone and detours traffic."
        )
    }

    private suspend fun runScenarioFullShowcase() {
        _simulationStatus.value = SimulationStatus(
            activeScenario = SimulationScenario.SCENARIO_F_FULL_SHOWCASE,
            isRunning = true,
            currentStep = 1,
            totalSteps = 6,
            title = "National Hackathon Showcase",
            description = "Step 1/6: Baseline health check. 5 nodes reporting telemetry over ESP-NOW."
        )
        delay(2200)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 2,
            description = "Step 2/6: Dusk falls. Ambient lux drops to 18 lux. Autonomous standby illumination ramps up."
        )
        setGlobalAmbientLux(18f)
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 3,
            description = "Step 3/6: Pedestrian walks from West Gate to Central Plaza. Adaptive wave illuminates path."
        )
        triggerPedestrianMotion("NODE-01")
        delay(1800)
        triggerPedestrianMotion("NODE-02")
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 4,
            description = "Step 4/6: Emergency SOS triggered at NODE-02! Corridor floods with light and strobe."
        )
        triggerEmergencySos("NODE-02", IncidentSeverity.CRITICAL)
        delay(3000)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 5,
            description = "Step 5/6: Campus internet fails! Local Gateway & ESP-NOW mesh continue operating with zero interruption."
        )
        if (_networkState.value.wanConnected) toggleWanInternet()
        delay(2500)

        _simulationStatus.value = _simulationStatus.value.copy(
            currentStep = 6,
            isRunning = false,
            title = "Showcase Complete",
            description = "All core innovations demonstrated: Mesh coordination, adaptive lighting, SOS cascade, and 100% offline autonomy."
        )
    }

    private fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300, 100, 500), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300, 100, 500), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 200, 100, 300, 100, 500), -1)
                }
            }
        } catch (_: Exception) {
            // Safe fallback if vibrator permission or device unavailable
        }
    }
}
