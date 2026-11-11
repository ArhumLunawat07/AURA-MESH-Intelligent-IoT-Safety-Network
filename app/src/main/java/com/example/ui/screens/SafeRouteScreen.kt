package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IotNode
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
import com.example.ui.components.NetworkMapView
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraLightActive
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraWarningAmber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeRouteScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    var originNodeId by remember { mutableStateOf("NODE-01") }
    var destinationNodeId by remember { mutableStateOf("NODE-05") }

    var expandedOrigin by remember { mutableStateOf(false) }
    var expandedDest by remember { mutableStateOf(false) }

    var calculatedRoute by remember { mutableStateOf<AuraMeshRepository.CalculatedRoute?>(null) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Recalculate route whenever origin, destination, or nodes update
    LaunchedEffect(originNodeId, destinationNodeId, nodes) {
        if (nodes.isNotEmpty()) {
            calculatedRoute = repository.calculateSafeRoute(originNodeId, destinationNodeId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_safe_route"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "PATHWAY ASSISTANCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraCyan
                )
            )
            Text(
                text = "Assisted Illuminated Route",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // Safety Disclaimer (Required by spec)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SAFETY LABELING PRINCIPLE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "This module provides 'Assisted Route Illumination'. It calculates pathways with active IoT lighting and flags dark or offline nodes. It NEVER guarantees personal safety and collects ZERO facial or personal tracking data.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                    )
                }
            }
        }

        // Origin and Destination Selectors
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SELECT ROUTE ENDPOINTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Origin dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedOrigin,
                        onExpandedChange = { expandedOrigin = !expandedOrigin },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = originNodeId,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Start Node", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrigin) },
                            modifier = Modifier.menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AuraTextPrimary,
                                unfocusedTextColor = AuraTextPrimary,
                                focusedBorderColor = AuraCyan,
                                unfocusedBorderColor = AuraBorder
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedOrigin,
                            onDismissRequest = { expandedOrigin = false },
                            modifier = Modifier.background(AuraDarkCardElevated)
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text("${node.id} (${node.name})", color = AuraTextPrimary, fontSize = 12.sp) },
                                    onClick = {
                                        originNodeId = node.id
                                        expandedOrigin = false
                                    }
                                )
                            }
                        }
                    }

                    // Destination dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedDest,
                        onExpandedChange = { expandedDest = !expandedDest },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = destinationNodeId,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Destination", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDest) },
                            modifier = Modifier.menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AuraTextPrimary,
                                unfocusedTextColor = AuraTextPrimary,
                                focusedBorderColor = AuraCyan,
                                unfocusedBorderColor = AuraBorder
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDest,
                            onDismissRequest = { expandedDest = false },
                            modifier = Modifier.background(AuraDarkCardElevated)
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text("${node.id} (${node.name})", color = AuraTextPrimary, fontSize = 12.sp) },
                                    onClick = {
                                        destinationNodeId = node.id
                                        expandedDest = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Map with Highlighted Route Corridor
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "VISUAL ROUTE CORRIDOR MESH",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraCyan, letterSpacing = 1.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                NetworkMapView(
                    nodes = nodes,
                    selectedNodeId = null,
                    highlightedRouteIds = calculatedRoute?.pathNodeIds ?: emptyList(),
                    onNodeSelected = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )
            }
        }

        // Calculated Route Evaluation Card
        if (calculatedRoute != null) {
            val route = calculatedRoute!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (route.hasOfflineNode) AuraWarningAmber else AuraCyan)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GRAPH PATH COMPUTATION",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextMuted, letterSpacing = 1.sp)
                            )
                            Text(
                                text = "${route.pathNodeIds.size} Nodes • ~${route.totalDistanceMeters} meters",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AuraTextPrimary)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (route.coveragePercent == 100) AuraSuccessGreen.copy(alpha = 0.2f) else AuraWarningAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${route.coveragePercent}% ILLUMINATED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (route.coveragePercent == 100) AuraSuccessGreen else AuraWarningAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step-by-step path chain
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        route.pathNodeIds.forEachIndexed { index, nodeId ->
                            val n = nodes.find { it.id == nodeId }
                            val isOffline = n?.status == NodeStatus.OFFLINE
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isOffline) AuraOfflineGray else AuraDarkCardElevated)
                                    .border(1.dp, if (isOffline) AuraEmergencyRed else AuraCyan, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = nodeId,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isOffline) Color.White else AuraCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                            if (index < route.pathNodeIds.size - 1) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AuraTextMuted,
                                    modifier = Modifier.padding(horizontal = 4.dp).size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notes & Problematic Sections Alert
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (route.hasOfflineNode) AuraWarningAmber.copy(alpha = 0.15f) else AuraDarkCardElevated)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                if (route.hasOfflineNode) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (route.hasOfflineNode) AuraWarningAmber else AuraSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = route.safeRouteNotes,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AuraTextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Button to activate corridor lighting
                    Button(
                        onClick = { repository.activateRouteCorridorLighting(route.pathNodeIds) },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_activate_corridor_lighting")
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AuraDarkBackground, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Activate Assisted Corridor Lighting Wave",
                            color = AuraDarkBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
