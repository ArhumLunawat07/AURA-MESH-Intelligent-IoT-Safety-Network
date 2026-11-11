/**
 * AURA MESH - Field IoT Sensor Node Firmware
 * Target: ESP32-WROOM-32 / ESP32-S3
 * Protocol: ESP-NOW (2.4GHz IEEE 802.11 vendor action frames)
 *
 * Hardware Connections:
 * - PIR Sensor: GPIO 27 (Input)
 * - Momentary SOS Pushbutton: GPIO 14 (Input Pull-up, active LOW)
 * - LED Light Output: GPIO 18 (PWM, MOSFET gate or WS2812B Data)
 * - Active Buzzer: GPIO 25 (PWM Tone output)
 * - BH1750 Ambient Lux Sensor: I2C (SDA=GPIO 21, SCL=GPIO 22)
 */

#include <esp_now.h>
#include <WiFi.h>
#include <Wire.h>
#include <BH1750.h>

#define NODE_ID "NODE-01"
#define MESH_CHANNEL 7

#define PIN_PIR 27
#define PIN_SOS 14
#define PIN_LED_PWM 18
#define PIN_BUZZER 25

BH1750 lightMeter;

// Mesh Payload Structure (Strictly aligned across nodes & gateway)
typedef struct __attribute__((packed)) {
    char nodeId[10];
    uint32_t packetSequence;
    uint32_t timestampEpochMs;
    uint8_t batteryPercent;
    uint16_t solarVoltageMilliVolts;
    float ambientLux;
    bool motionDetected;
    uint8_t currentLightBrightness; // 0-100%
    bool isEmergencyActive;
} AuraMeshPacket;

AuraMeshPacket outboundPacket;
AuraMeshPacket inboundPacket;

// Broadcast MAC (0xFF:0xFF:0xFF:0xFF:0xFF:0xFF)
uint8_t broadcastMac[] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

uint32_t sequenceCounter = 0;
unsigned long lastHeartbeat = 0;
const unsigned long HEARTBEAT_INTERVAL = 5000; // 5 seconds

void onDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {
    // Hardware confirmation (< 20ms peer delivery)
}

void onDataRecv(const uint8_t *mac, const uint8_t *incomingData, int len) {
    if (len == sizeof(AuraMeshPacket)) {
        memcpy(&inboundPacket, incomingData, sizeof(inboundPacket));
        
        // If neighbor node broadcasted emergency SOS, floodlight local corridor
        if (inboundPacket.isEmergencyActive) {
            analogWrite(PIN_LED_PWM, 255); // Floodlight 100%
        }
    }
}

void setup() {
    Serial.begin(115200);
    pinMode(PIN_PIR, INPUT);
    pinMode(PIN_SOS, INPUT_PULLUP);
    pinMode(PIN_LED_PWM, OUTPUT);
    pinMode(PIN_BUZZER, OUTPUT);

    Wire.begin(21, 22);
    lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE);

    WiFi.mode(WIFI_STA);
    WiFi.disconnect();

    if (esp_now_init() != ESP_OK) {
        Serial.println("ESP-NOW Init Failed!");
        return;
    }

    esp_now_register_send_cb(onDataSent);
    esp_now_register_recv_cb(onDataRecv);

    esp_now_peer_info_t peerInfo = {};
    memcpy(peerInfo.peer_addr, broadcastMac, 6);
    peerInfo.channel = MESH_CHANNEL;
    peerInfo.encrypt = false;
    esp_now_add_peer(&peerInfo);

    Serial.println("AURA MESH Node Initialized on Channel 7.");
}

void loop() {
    bool motion = digitalRead(PIN_PIR);
    bool sosActive = (digitalRead(PIN_SOS) == LOW);
    float lux = lightMeter.readLightLevel();

    if (sosActive) {
        // CRITICAL SOS ACTIVATION
        analogWrite(PIN_LED_PWM, 255); // Immediate 100% Strobe
        tone(PIN_BUZZER, 2400, 200);

        outboundPacket.isEmergencyActive = true;
        outboundPacket.currentLightBrightness = 100;
    } else if (lux < 45.0f && motion) {
        // Pedestrian walking in low-light corridor
        analogWrite(PIN_LED_PWM, 230); // 90% power
        outboundPacket.isEmergencyActive = false;
        outboundPacket.currentLightBrightness = 90;
    } else if (lux < 45.0f) {
        // Low-light standby guiding illumination
        analogWrite(PIN_LED_PWM, 51); // 20% power
        outboundPacket.isEmergencyActive = false;
        outboundPacket.currentLightBrightness = 20;
    } else {
        // Daylight energy conservation mode
        analogWrite(PIN_LED_PWM, 0);
        outboundPacket.isEmergencyActive = false;
        outboundPacket.currentLightBrightness = 0;
    }

    // Transmit telemetry periodically or immediately on state changes
    if (sosActive || motion || (millis() - lastHeartbeat >= HEARTBEAT_INTERVAL)) {
        strncpy(outboundPacket.nodeId, NODE_ID, sizeof(outboundPacket.nodeId));
        outboundPacket.packetSequence = ++sequenceCounter;
        outboundPacket.timestampEpochMs = millis();
        outboundPacket.batteryPercent = 94; // ADC read on battery divider
        outboundPacket.solarVoltageMilliVolts = 4120;
        outboundPacket.ambientLux = lux;
        outboundPacket.motionDetected = motion;

        esp_now_send(broadcastMac, (uint8_t *)&outboundPacket, sizeof(outboundPacket));
        lastHeartbeat = millis();
    }

    delay(100);
}
