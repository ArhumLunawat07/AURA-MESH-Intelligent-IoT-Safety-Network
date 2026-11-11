/**
 * AURA MESH - Central Local Gateway Firmware
 * Target: ESP32-S3 / Raspberry Pi bridge
 * Function: Receives ESP-NOW 2.4GHz packets from field nodes, bridges to local MQTT broker
 *
 * Runs locally even if Internet WAN connection is disconnected.
 */

#include <WiFi.h>
#include <esp_now.h>
#include <PubSubClient.h>

#define MESH_CHANNEL 7
const char* WIFI_SSID = "AURA_LOCAL_GATEWAY_NET";
const char* WIFI_PASS = "AuraMeshSecureLocal2026";
const char* MQTT_SERVER = "192.168.4.1";
const int MQTT_PORT = 1883;

WiFiClient espClient;
PubSubClient mqttClient(espClient);

typedef struct __attribute__((packed)) {
    char nodeId[10];
    uint32_t packetSequence;
    uint32_t timestampEpochMs;
    uint8_t batteryPercent;
    uint16_t solarVoltageMilliVolts;
    float ambientLux;
    bool motionDetected;
    uint8_t currentLightBrightness;
    bool isEmergencyActive;
} AuraMeshPacket;

AuraMeshPacket incomingPacket;

void onDataRecv(const uint8_t *mac, const uint8_t *data, int len) {
    if (len == sizeof(AuraMeshPacket)) {
        memcpy(&incomingPacket, data, sizeof(incomingPacket));

        char topic[64];
        char payload[256];

        if (incomingPacket.isEmergencyActive) {
            snprintf(topic, sizeof(topic), "aura/events/emergency");
            snprintf(payload, sizeof(payload),
                "{\"origin_node_id\":\"%s\",\"seq\":%u,\"battery\":%u,\"emergency\":true}",
                incomingPacket.nodeId, incomingPacket.packetSequence, incomingPacket.batteryPercent);
            mqttClient.publish(topic, payload);
        }

        snprintf(topic, sizeof(topic), "aura/node/%s/telemetry", incomingPacket.nodeId);
        snprintf(payload, sizeof(payload),
            "{\"node_id\":\"%s\",\"lux\":%.1f,\"motion\":%s,\"brightness\":%u,\"battery\":%u}",
            incomingPacket.nodeId, incomingPacket.ambientLux,
            incomingPacket.motionDetected ? "true" : "false",
            incomingPacket.currentLightBrightness, incomingPacket.batteryPercent);

        mqttClient.publish(topic, payload);
    }
}

void setup() {
    Serial.begin(115200);

    WiFi.mode(WIFI_AP_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASS);

    mqttClient.setServer(MQTT_SERVER, MQTT_PORT);

    if (esp_now_init() == ESP_OK) {
        esp_now_register_recv_cb(onDataRecv);
        Serial.println("ESP-NOW to MQTT Gateway Initialized.");
    }
}

void loop() {
    if (!mqttClient.connected()) {
        mqttClient.connect("AuraMeshCentralGateway");
    }
    mqttClient.loop();
    delay(20);
}
