package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.IncidentSeverity
import com.example.data.model.IncidentStatus
import com.example.data.model.NodeStatus
import com.example.data.model.TelemetryEventType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromNodeStatus(status: NodeStatus): String = status.name

    @TypeConverter
    fun toNodeStatus(value: String): NodeStatus = try {
        NodeStatus.valueOf(value)
    } catch (_: Exception) {
        NodeStatus.HEALTHY
    }

    @TypeConverter
    fun fromIncidentSeverity(severity: IncidentSeverity): String = severity.name

    @TypeConverter
    fun toIncidentSeverity(value: String): IncidentSeverity = try {
        IncidentSeverity.valueOf(value)
    } catch (_: Exception) {
        IncidentSeverity.MEDIUM
    }

    @TypeConverter
    fun fromIncidentStatus(status: IncidentStatus): String = status.name

    @TypeConverter
    fun toIncidentStatus(value: String): IncidentStatus = try {
        IncidentStatus.valueOf(value)
    } catch (_: Exception) {
        IncidentStatus.ACTIVE
    }

    @TypeConverter
    fun fromTelemetryEventType(type: TelemetryEventType): String = type.name

    @TypeConverter
    fun toTelemetryEventType(value: String): TelemetryEventType = try {
        TelemetryEventType.valueOf(value)
    } catch (_: Exception) {
        TelemetryEventType.HEARTBEAT
    }
}
