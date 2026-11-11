package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.EmergencyIncident
import com.example.data.model.IncidentSeverity
import com.example.data.model.IncidentStatus
import com.example.data.model.IotNode
import com.example.data.repository.AuraMeshRepository
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmergencyGlow
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraOfflineGray
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyCenterScreen(
    repository: AuraMeshRepository,
    modifier: Modifier = Modifier
) {
    val incidents by repository.allIncidents.collectAsState(initial = emptyList())
    val nodes by repository.allNodes.collectAsState(initial = emptyList())
    val networkState by repository.networkState.collectAsState()

    var resolveTargetIncident by remember { mutableStateOf<EmergencyIncident?>(null) }
    var resolutionNotesText by remember { mutableStateOf("Patrol unit arrived on scene. Area secured.") }
    var selectedOriginNodeId by remember { mutableStateOf("NODE-02") }
    var expandedDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .padding(16.dp)
            .testTag("screen_emergency_center"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "INCIDENT MANAGEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraEmergencyRed
                    )
                )
                Text(
                    text = "Emergency Response Center",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = AuraTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Safety Principle Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AuraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SAFETY PRINCIPLE (NON-911 DISCLAIMER)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "AURA MESH is an environmental assistance infrastructure. It coordinates pathway lighting and alerts security dashboards. It does NOT guarantee personal safety and NEVER contacts real emergency services from demonstration software.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AuraTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        // Trigger Emergency SOS Simulator Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AuraEmergencyRed.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SIMULATE PHYSICAL SOS BUTTON ACTIVATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraEmergencyRed,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Simulate a pedestrian pressing the emergency call button on a physical post. Originating node will strobe red while adjacent nodes flood the walkway.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expandedDropdown,
                            onExpandedChange = { expandedDropdown = !expandedDropdown },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedOriginNodeId,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                                modifier = Modifier.menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = AuraTextPrimary,
                                    unfocusedTextColor = AuraTextPrimary,
                                    focusedBorderColor = AuraCyan,
                                    unfocusedBorderColor = AuraBorder
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false },
                                modifier = Modifier.background(AuraDarkCardElevated)
                            ) {
                                nodes.forEach { node ->
                                    DropdownMenuItem(
                                        text = { Text("${node.id} (${node.name})", color = AuraTextPrimary) },
                                        onClick = {
                                            selectedOriginNodeId = node.id
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { repository.triggerEmergencySos(selectedOriginNodeId, IncidentSeverity.CRITICAL) },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraEmergencyRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_broadcast_emergency")
                        ) {
                            Icon(Icons.Default.CrisisAlert, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trigger SOS", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active & Historical Incident List
        item {
            Text(
                text = "INCIDENT AUDIT LOG (${incidents.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AuraTextMuted,
                    letterSpacing = 1.sp
                )
            )
        }

        if (incidents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraSuccessGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Emergency Incidents Reported", style = MaterialTheme.typography.titleMedium.copy(color = AuraTextPrimary, fontWeight = FontWeight.Bold))
                        Text("Mesh monitoring actively scanning on Channel 7.", style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary))
                    }
                }
            }
        } else {
            items(incidents) { incident ->
                IncidentCardItem(
                    incident = incident,
                    onAcknowledge = { repository.acknowledgeIncident(incident.id) },
                    onDispatch = { repository.dispatchResponder(incident.id, "Security Unit Alpha") },
                    onResolveClick = { resolveTargetIncident = incident }
                )
            }
        }
    }

    // Confirmation dialog before resolving active emergency incident
    if (resolveTargetIncident != null) {
        val target = resolveTargetIncident!!
        AlertDialog(
            onDismissRequest = { resolveTargetIncident = null },
            containerColor = AuraDarkCard,
            title = {
                Text(
                    text = "Confirm Incident Resolution",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AuraTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to resolve incident ${target.id}? Nodes will return to standby lighting.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resolutionNotesText,
                        onValueChange = { resolutionNotesText = it },
                        label = { Text("Resolution Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.resolveIncident(target.id, resolutionNotesText)
                        resolveTargetIncident = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraSuccessGreen)
                ) {
                    Text("Resolve & Clear", color = AuraDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { resolveTargetIncident = null }) {
                    Text("Cancel", color = AuraTextMuted)
                }
            }
        )
    }
}

@Composable
private fun IncidentCardItem(
    incident: EmergencyIncident,
    onAcknowledge: () -> Unit,
    onDispatch: () -> Unit,
    onResolveClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm:ss", Locale.US).format(Date(incident.timestampEpochMs))

    val statusColor = when (incident.status) {
        IncidentStatus.ACTIVE -> AuraEmergencyRed
        IncidentStatus.ACKNOWLEDGED -> AuraWarningAmber
        IncidentStatus.DISPATCHED -> AuraCyan
        IncidentStatus.RESOLVED -> AuraSuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (incident.status != IncidentStatus.RESOLVED) statusColor.copy(alpha = 0.8f) else AuraBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = incident.id,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuraTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = incident.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Origin: ${incident.location}",
                style = MaterialTheme.typography.bodyMedium.copy(color = AuraTextPrimary, fontWeight = FontWeight.SemiBold)
            )

            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodySmall.copy(color = AuraTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cascade Corridor info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(AuraDarkCardElevated)
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "ACTIVATED ILLUMINATION CORRIDOR:",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = incident.activatedNodeIds.joinToString(" ➔ "),
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    )
                }
            }

            if (incident.responderName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Assigned Responder: ${incident.responderName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraTextSecondary, fontSize = 11.sp)
                )
            }

            if (incident.resolutionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Resolution: ${incident.resolutionNotes}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AuraSuccessGreen, fontSize = 11.sp)
                )
            }

            // Controls
            if (incident.status != IncidentStatus.RESOLVED) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (incident.status == IncidentStatus.ACTIVE) {
                        Button(
                            onClick = onAcknowledge,
                            colors = ButtonDefaults.buttonColors(containerColor = AuraWarningAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Acknowledge", color = AuraDarkBackground, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    } else if (incident.status == IncidentStatus.ACKNOWLEDGED) {
                        Button(
                            onClick = onDispatch,
                            colors = ButtonDefaults.buttonColors(containerColor = AuraCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = AuraDarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch Patrol", color = AuraDarkBackground, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    OutlinedButton(
                        onClick = onResolveClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraSuccessGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AuraSuccessGreen.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraSuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resolve", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
