package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.TelemetryLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {
    @Query("SELECT * FROM telemetry_logs ORDER BY timestampEpochMs DESC LIMIT 200")
    fun getRecentTelemetry(): Flow<List<TelemetryLog>>

    @Insert
    suspend fun insertLog(log: TelemetryLog)

    @Query("DELETE FROM telemetry_logs WHERE id NOT IN (SELECT id FROM telemetry_logs ORDER BY timestampEpochMs DESC LIMIT 500)")
    suspend fun pruneOldLogs()

    @Query("SELECT COUNT(*) FROM telemetry_logs WHERE eventType = 'MOTION_DETECTED'")
    suspend fun getMotionTriggerCount(): Int
}
