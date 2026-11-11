package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.IotNode
import com.example.data.model.NodeStatus
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraLightEmergency
import com.example.ui.theme.AuraLightStandby
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraWarningAmber
import kotlin.math.hypot

@Composable
fun NetworkMapView(
    nodes: List<IotNode>,
    selectedNodeId: String?,
    highlightedRouteIds: List<String> = emptyList(),
    onNodeSelected: (IotNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    val emergencyStrobePhase by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe"
    )

    val nodeMap = nodes.associateBy { it.id }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AuraDarkCard)
            .testTag("interactive_network_map")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        // Check which node was tapped (threshold 44.dp in pixels)
                        val touchRadiusPx = 48.dp.toPx()
                        var closestNode: IotNode? = null
                        var closestDist = Float.MAX_VALUE

                        nodes.forEach { node ->
                            val nx = node.posX * w
                            val ny = node.posY * h
                            val dist = hypot(tapOffset.x - nx, tapOffset.y - ny)
                            if (dist < touchRadiusPx && dist < closestDist) {
                                closestDist = dist
                                closestNode = node
                            }
                        }

                        closestNode?.let { onNodeSelected(it) }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle grid backdrop
            drawCampusGrid(width, height)

            // 2. Central Gateway coordinates
            val gatewayX = 0.48f * width
            val gatewayY = 0.52f * height

            // Draw Gateway pulsing wireless rings
            val maxGatewayRadius = 70.dp.toPx()
            val ringRadius = pulseProgress * maxGatewayRadius
            drawCircle(
                color = AuraCyan.copy(alpha = (1f - pulseProgress) * 0.45f),
                radius = ringRadius,
                center = Offset(gatewayX, gatewayY),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = AuraElectricBlue.copy(alpha = 0.25f),
                radius = 24.dp.toPx(),
                center = Offset(gatewayX, gatewayY)
            )
            drawCircle(
                color = AuraCyan,
                radius = 8.dp.toPx(),
                center = Offset(gatewayX, gatewayY)
            )

            // 3. Draw Walkways / Mesh Links between nodes
            val drawnLinks = mutableSetOf<Pair<String, String>>()
            nodes.forEach { node ->
                val x1 = node.posX * width
                val y1 = node.posY * height

                // Connect to gateway if in primary range
                drawLine(
                    color = AuraBorder.copy(alpha = 0.6f),
                    start = Offset(x1, y1),
                    end = Offset(gatewayX, gatewayY),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Connect neighbors
                node.neighborNodeIds.forEach { neighborId ->
                    val pair = if (node.id < neighborId) node.id to neighborId else neighborId to node.id
                    if (pair !in drawnLinks) {
                        drawnLinks.add(pair)
                        val neighbor = nodeMap[neighborId]
                        if (neighbor != null) {
                            val x2 = neighbor.posX * width
                            val y2 = neighbor.posY * height

                            // Check if this link is part of the highlighted route
                            val isRouteLink = highlightedRouteIds.contains(node.id) &&
                                    highlightedRouteIds.contains(neighbor.id) &&
                                    kotlin.math.abs(highlightedRouteIds.indexOf(node.id) - highlightedRouteIds.indexOf(neighbor.id)) == 1

                            if (isRouteLink) {
                                // Luminous route corridor glow
                                drawLine(
                                    color = AuraCyan.copy(alpha = 0.35f),
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 12.dp.toPx()
                                )
                                drawLine(
                                    color = AuraCyan,
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 3.dp.toPx()
                                )
                            } else {
                                val linkColor = if (node.status == NodeStatus.OFFLINE || neighbor.status == NodeStatus.OFFLINE) {
                                    AuraOfflineGray.copy(alpha = 0.25f)
                                } else {
                                    AuraElectricBlue.copy(alpha = 0.4f)
                                }
                                drawLine(
                                    color = linkColor,
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), pulseProgress * 20f)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Draw Lighting Cones & Illumination Halos around each node
            nodes.forEach { node ->
                val nx = node.posX * width
                val ny = node.posY * height
                val brightnessFactor = (node.lightBrightness / 100f).coerceIn(0f, 1f)

                if (node.isEmergencyStrobe) {
                    // Pulsing Red emergency cone
                    val emergencyRadius = (45.dp.toPx() * emergencyStrobePhase)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AuraEmergencyRed.copy(alpha = 0.55f * emergencyStrobePhase),
                                AuraEmergencyRed.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(nx, ny),
                            radius = emergencyRadius
                        ),
                        radius = emergencyRadius,
                        center = Offset(nx, ny)
                    )
                } else if (node.status != NodeStatus.OFFLINE && brightnessFactor > 0.05f) {
                    val haloRadius = 20.dp.toPx() + (brightnessFactor * 32.dp.toPx())
                    val haloColor = if (node.motionDetected) AuraLightActive else AuraLightStandby
                    val haloAlpha = 0.25f + (brightnessFactor * 0.35f)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                haloColor.copy(alpha = haloAlpha),
                                haloColor.copy(alpha = haloAlpha * 0.4f),
                                Color.Transparent
                            ),
                            center = Offset(nx, ny),
                            radius = haloRadius
                        ),
                        radius = haloRadius,
                        center = Offset(nx, ny)
                    )
                }

                // Motion wave ring
                if (node.motionDetected) {
                    val motionRadius = pulseProgress * 40.dp.toPx()
                    drawCircle(
                        color = AuraLightActive.copy(alpha = (1f - pulseProgress) * 0.8f),
                        radius = motionRadius,
                        center = Offset(nx, ny),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // 5. Draw Node Physical Markers and Labels
            nodes.forEach { node ->
                val nx = node.posX * width
                val ny = node.posY * height
                val isSelected = node.id == selectedNodeId

                val statusColor = when (node.status) {
                    NodeStatus.HEALTHY -> AuraSuccessGreen
                    NodeStatus.WARNING -> AuraWarningAmber
                    NodeStatus.EMERGENCY -> AuraEmergencyRed
                    NodeStatus.OFFLINE -> AuraOfflineGray
                }

                // Selection outer ring
                if (isSelected) {
                    drawCircle(
                        color = AuraCyan,
                        radius = 18.dp.toPx(),
                        center = Offset(nx, ny),
                        style = Stroke(width = 2.5f)
                    )
                }

                // Node Base
                drawCircle(
                    color = AuraDarkBackground,
                    radius = 12.dp.toPx(),
                    center = Offset(nx, ny)
                )
                drawCircle(
                    color = statusColor,
                    radius = 10.dp.toPx(),
                    center = Offset(nx, ny),
                    style = Stroke(width = 2.5f)
                )
                drawCircle(
                    color = if (node.status == NodeStatus.EMERGENCY) AuraEmergencyRed else statusColor.copy(alpha = 0.8f),
                    radius = 6.dp.toPx(),
                    center = Offset(nx, ny)
                )

                // Draw Text Label using native Canvas
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
                        textAlign = android.graphics.Paint.Align.CENTER
                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                    }
                    drawText(node.id, nx, ny + 26.dp.toPx(), paint)

                    val subPaint = android.graphics.Paint().apply {
                        color = if (node.status == NodeStatus.OFFLINE) android.graphics.Color.GRAY else android.graphics.Color.CYAN
                        textSize = 22f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        setShadowLayer(3f, 0f, 0f, android.graphics.Color.BLACK)
                    }
                    val subText = if (node.status == NodeStatus.OFFLINE) "OFFLINE" else "${node.lightBrightness}%"
                    drawText(subText, nx, ny + 38.dp.toPx(), subPaint)
                }
            }

            // Draw Gateway text label
            drawContext.canvas.nativeCanvas.apply {
                val gwPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#00F0FF")
                    textSize = 24f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                    setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                }
                drawText("CENTRAL GATEWAY", gatewayX, gatewayY - 14.dp.toPx(), gwPaint)
            }
        }
    }
}

private fun DrawScope.drawCampusGrid(width: Float, height: Float) {
    val step = 40.dp.toPx()
    var x = 0f
    while (x <= width) {
        drawLine(
            color = AuraBorder.copy(alpha = 0.2f),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 0.8f
        )
        x += step
    }
    var y = 0f
    while (y <= height) {
        drawLine(
            color = AuraBorder.copy(alpha = 0.2f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 0.8f
        )
        y += step
    }
}
