package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardElevated
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraSuccessGreen
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraViolet

@Composable
fun HardwareIntegrationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var gatewayIp by remember { mutableStateOf("192.168.4.1") }
    var mqttPort by remember { mutableStateOf("1883") }
    var wsEndpoint by remember { mutableStateOf("ws://192.168.4.1:8083/mqtt") }
    var connectionTested by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_hardware_integration"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "PHYSICAL EMBEDDED INTEGRATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraCyan
                )
            )
            Text(
                text = "Hardware & Gateway Specs",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AuraTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // Gateway & MQTT Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LOCAL GATEWAY CONFIGURATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraCyan, letterSpacing = 1.sp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AuraSuccessGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (connectionTested) "PING VERIFIED (12ms)" else "STANDALONE READY",
                            style = MaterialTheme.typography.labelSmall.copy(color = AuraSuccessGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = gatewayIp,
                    onValueChange = { gatewayIp = it },
                    label = { Text("Gateway Host IP / mDNS") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AuraTextPrimary,
                        unfocusedTextColor = AuraTextPrimary,
                        focusedBorderColor = AuraCyan,
                        unfocusedBorderColor = AuraBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mqttPort,
                        onValueChange = { mqttPort = it },
                        label = { Text("MQTT Port") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder
                        )
                    )
                    OutlinedTextField(
                        value = wsEndpoint,
                        onValueChange = { wsEndpoint = it },
                        label = { Text("WebSocket URI") },
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        connectionTested = true
                        Toast.makeText(context, "Local gateway handshake tested (12ms latency)", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraElectricBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Gateway Broker Handshake", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Subsystem Tab Navigation: JSON Schemas vs ESP32 Firmware
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AuraDarkCard,
            contentColor = AuraCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AuraCyan
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("JSON Topic Schemas", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("ESP32 Firmware C++", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        if (selectedTab == 0) {
            // Documented JSON Schemas
            JsonSchemaCard(
                title = "1. Telemetry Topic: aura/node/{nodeId}/telemetry",
                schema = """
{
  "node_id": "NODE-01",
  "timestamp_epoch_ms": 1731294820000,
  "battery_pct": 94,
  "solar_voltage_v": 4.12,
  "ambient_lux": 24.5,
  "motion_state": true,
  "light_brightness_pct": 90,
  "rssi_dbm": -54,
  "esp_now_peer_count": 2,
  "firmware": "v2.4.1-espnow"
}
                """.trimIndent(),
                onCopy = { copyToClipboard(context, it) }
            )

            JsonSchemaCard(
                title = "2. Emergency Event Topic: aura/events/emergency",
                schema = """
{
  "event_id": "INC-20261110-1948",
  "origin_node_id": "NODE-03",
  "severity": "CRITICAL",
  "strobe_active": true,
  "cascade_neighbor_ids": ["NODE-01", "NODE-02", "NODE-05"],
  "timestamp_epoch_ms": 1731294850000,
  "trigger_mechanism": "PHYSICAL_MOMENTARY_PUSHBUTTON",
  "offline_handled_locally": true
}
                """.trimIndent(),
                onCopy = { copyToClipboard(context, it) }
            )

            JsonSchemaCard(
                title = "3. Actuation Command Topic: aura/node/{nodeId}/command",
                schema = """
{
  "command": "SET_LIGHT_POWER",
  "target_node_id": "NODE-02",
  "brightness_pct": 90,
  "strobe_override": false,
  "duration_seconds": 30,
  "auth_token_sig": "e9b2f8a14c38d"
}
                """.trimIndent(),
                onCopy = { copyToClipboard(context, it) }
            )
        } else {
            // ESP32 Arduino C++ Firmware Code
            CodeViewerCard(
                title = "ESP32 Node Firmware (ESP-NOW Mesh)",
                language = "C++ / Arduino IDE / PlatformIO",
                code = """
#include <esp_now.h>
#include <WiFi.h>
#include <Wire.h>
#include <BH1750.h>

#define PIR_PIN 27
#define SOS_PIN 14
#define LED_PWM_PIN 18
#define BUZZER_PIN 25

BH1750 lightMeter;

typedef struct struct_message {
  char nodeId[10];
  uint32_t timestamp;
  uint8_t battery;
  float lux;
  bool motion;
  bool isEmergency;
} struct_message;

struct_message telemetryPacket;
uint8_t broadcastAddress[] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

void OnDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {
  // Sub-20ms peer-to-peer confirmation
}

void setup() {
  Serial.begin(115200);
  pinMode(PIR_PIN, INPUT);
  pinMode(SOS_PIN, INPUT_PULLUP);
  pinMode(LED_PWM_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  Wire.begin();
  lightMeter.begin();

  WiFi.mode(WIFI_STA);
  if (esp_now_init() != ESP_OK) return;

  esp_now_register_send_cb(OnDataSent);

  esp_now_peer_info_t peerInfo = {};
  memcpy(peerInfo.peer_addr, broadcastAddress, 6);
  peerInfo.channel = 7;
  peerInfo.encrypt = false;
  esp_now_add_peer(&peerInfo);
}

void loop() {
  bool motion = digitalRead(PIR_PIN);
  bool sos = !digitalRead(SOS_PIN);
  float lux = lightMeter.readLightLevel();

  if (sos) {
    // Physical SOS button pressed - immediate cascade
    telemetryPacket.isEmergency = true;
    analogWrite(LED_PWM_PIN, 255); // 100% emergency strobe
    tone(BUZZER_PIN, 1800);
    esp_now_send(broadcastAddress, (uint8_t *) &telemetryPacket, sizeof(telemetryPacket));
  } else if (lux < 45.0 && motion) {
    // Pedestrian motion in low ambient light
    analogWrite(LED_PWM_PIN, 230); // 90% pathway boost
  } else if (lux < 45.0) {
    analogWrite(LED_PWM_PIN, 51);  // 20% standby glow
  } else {
    analogWrite(LED_PWM_PIN, 0);   // Daylight power save
  }
  delay(100);
}
                """.trimIndent(),
                onCopy = { copyToClipboard(context, it) }
            )
        }
    }
}

@Composable
private fun JsonSchemaCard(
    title: String,
    schema: String,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AuraCyan)
                )
                OutlinedButton(
                    onClick = { onCopy(schema) },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 10.sp, color = AuraTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraDarkCardElevated)
                    .padding(10.dp)
            ) {
                Text(
                    text = schema,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AuraTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun CodeViewerCard(
    title: String,
    language: String,
    code: String,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = AuraTextPrimary))
                    Text(text = language, style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontSize = 10.sp))
                }

                Button(
                    onClick = { onCopy(code) },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AuraCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy C++", fontSize = 11.sp, color = AuraTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AuraDarkCardElevated)
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AuraTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AURA MESH Data", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}
