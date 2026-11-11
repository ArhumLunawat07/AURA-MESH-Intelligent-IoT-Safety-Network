package com.example.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuraMeshRepository
import com.example.ui.screens.AdaptiveLightingScreen
import com.example.ui.screens.AnalyticsReportsScreen
import com.example.ui.screens.CommandCenterScreen
import com.example.ui.screens.EmergencyCenterScreen
import com.example.ui.screens.EnvironmentalMonitoringScreen
import com.example.ui.screens.ExploreLandingScreen
import com.example.ui.screens.HardwareIntegrationScreen
import com.example.ui.screens.NetworkHealthScreen
import com.example.ui.screens.NetworkMapScreen
import com.example.ui.screens.SafeRouteScreen
import com.example.ui.screens.SettingsDocsScreen
import com.example.ui.screens.SimulationLabScreen
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmergencyRed
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary

enum class AuraScreen(val title: String, val icon: ImageVector) {
    EXPLORE("Explore", Icons.Default.Explore),
    COMMAND("Command", Icons.Default.Speed),
    MAP("Topology", Icons.Default.Map),
    EMERGENCY("Emergency", Icons.Default.CrisisAlert),
    SIM_LAB("Sim Lab", Icons.Default.Science),
    LIGHTING("Lighting", Icons.Default.Lightbulb),
    ROUTES("Routes", Icons.Default.Route),
    SENSORS("Sensors", Icons.Default.Sensors),
    NETWORK("Health", Icons.Default.Hub),
    HARDWARE("Hardware", Icons.Default.DeveloperBoard),
    ANALYTICS("Analytics", Icons.Default.BarChart),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraMeshApp(
    repository: AuraMeshRepository
) {
    var currentScreen by remember { mutableStateOf(AuraScreen.EXPLORE) }
    var showAllModulesSheet by remember { mutableStateOf(false) }

    val activeIncidents by repository.activeIncidents.collectAsState(initial = emptyList())
    val networkState by repository.networkState.collectAsState()

    // Proper BackHandler handling as required by Android guidelines
    BackHandler(enabled = currentScreen != AuraScreen.EXPLORE) {
        if (showAllModulesSheet) {
            showAllModulesSheet = false
        } else if (currentScreen != AuraScreen.COMMAND) {
            currentScreen = AuraScreen.COMMAND
        } else {
            currentScreen = AuraScreen.EXPLORE
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraCyan.copy(alpha = 0.15f))
                                .border(1.dp, AuraCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AURA MESH",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AuraTextPrimary
                                )
                            )
                            Text(
                                text = currentScreen.title.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AuraCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != AuraScreen.EXPLORE) {
                        IconButton(onClick = { currentScreen = AuraScreen.EXPLORE }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Explore", tint = AuraTextPrimary)
                        }
                    }
                },
                actions = {
                    // WAN Status indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (networkState.wanConnected) AuraSuccessGreen.copy(alpha = 0.15f) else AuraEmergencyRed.copy(alpha = 0.2f))
                            .clickable { repository.toggleWanInternet() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (networkState.wanConnected) "WAN ONLINE" else "OFFLINE MESH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (networkState.wanConnected) AuraSuccessGreen else AuraEmergencyRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    // All Modules Sheet Opener
                    IconButton(onClick = { showAllModulesSheet = true }, modifier = Modifier.testTag("btn_open_all_modules")) {
                        Icon(Icons.Default.Apps, contentDescription = "All Modules", tint = AuraCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraDarkSurface,
                    titleContentColor = AuraTextPrimary
                )
            )
        },
        bottomBar = {
            if (currentScreen != AuraScreen.EXPLORE) {
                NavigationBar(
                    containerColor = AuraDarkSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    val primaryTabs = listOf(
                        AuraScreen.COMMAND,
                        AuraScreen.MAP,
                        AuraScreen.EMERGENCY,
                        AuraScreen.SIM_LAB
                    )

                    primaryTabs.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                if (screen == AuraScreen.EMERGENCY && activeIncidents.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(AuraEmergencyRed)
                                            )
                                        }
                                    ) {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                } else {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraDarkBackground,
                                selectedTextColor = AuraCyan,
                                indicatorColor = AuraCyan,
                                unselectedIconColor = AuraTextMuted,
                                unselectedTextColor = AuraTextMuted
                            )
                        )
                    }

                    // 5th tab: "Modules" (Opens all 12 modules sheet)
                    NavigationBarItem(
                        selected = currentScreen !in primaryTabs,
                        onClick = { showAllModulesSheet = true },
                        icon = { Icon(Icons.Default.Apps, contentDescription = "More Modules") },
                        label = { Text("More", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AuraDarkBackground,
                            selectedTextColor = AuraCyan,
                            indicatorColor = AuraCyan,
                            unselectedIconColor = AuraTextMuted,
                            unselectedTextColor = AuraTextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AuraScreen.EXPLORE -> {
                    ExploreLandingScreen(
                        onLaunchSimulation = { currentScreen = AuraScreen.SIM_LAB },
                        onOpenDashboard = { currentScreen = AuraScreen.COMMAND },
                        onViewHardware = { currentScreen = AuraScreen.HARDWARE }
                    )
                }
                AuraScreen.COMMAND -> {
                    CommandCenterScreen(
                        repository = repository,
                        onNavigateToEmergency = { currentScreen = AuraScreen.EMERGENCY },
                        onNavigateToMap = { currentScreen = AuraScreen.MAP },
                        onNavigateToSimLab = { currentScreen = AuraScreen.SIM_LAB }
                    )
                }
                AuraScreen.MAP -> {
                    NetworkMapScreen(repository = repository)
                }
                AuraScreen.EMERGENCY -> {
                    EmergencyCenterScreen(repository = repository)
                }
                AuraScreen.SIM_LAB -> {
                    SimulationLabScreen(repository = repository)
                }
                AuraScreen.LIGHTING -> {
                    AdaptiveLightingScreen(repository = repository)
                }
                AuraScreen.ROUTES -> {
                    SafeRouteScreen(repository = repository)
                }
                AuraScreen.SENSORS -> {
                    EnvironmentalMonitoringScreen(repository = repository)
                }
                AuraScreen.NETWORK -> {
                    NetworkHealthScreen(repository = repository)
                }
                AuraScreen.HARDWARE -> {
                    HardwareIntegrationScreen()
                }
                AuraScreen.ANALYTICS -> {
                    AnalyticsReportsScreen(repository = repository)
                }
                AuraScreen.SETTINGS -> {
                    SettingsDocsScreen(repository = repository)
                }
            }
        }
    }

    // Modal Bottom Sheet displaying all 12 system modules
    if (showAllModulesSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAllModulesSheet = false },
            sheetState = sheetState,
            containerColor = AuraDarkCardElevated,
            contentColor = AuraTextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AURA MESH ARCHITECTURE",
                            style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        )
                        Text(
                            text = "All 12 Infrastructure Modules",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AuraTextPrimary)
                        )
                    }
                    IconButton(onClick = { showAllModulesSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AuraTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AuraScreen.entries.toTypedArray()) { screen ->
                        val isCurrent = currentScreen == screen
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    currentScreen = screen
                                    showAllModulesSheet = false
                                },
                            colors = CardDefaults.cardColors(containerColor = if (isCurrent) AuraCyan.copy(alpha = 0.2f) else AuraDarkCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) AuraCyan else AuraBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCurrent) AuraCyan else AuraDarkBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isCurrent) AuraDarkBackground else AuraCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isCurrent) AuraCyan else AuraTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
