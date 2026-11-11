package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmergencyIncident
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestampEpochMs DESC")
    fun getAllIncidents(): Flow<List<EmergencyIncident>>

    @Query("SELECT * FROM incidents WHERE status != 'RESOLVED' ORDER BY timestampEpochMs DESC")
    fun getActiveIncidents(): Flow<List<EmergencyIncident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: EmergencyIncident)

    @Update
    suspend fun updateIncident(incident: EmergencyIncident)

    @Query("UPDATE incidents SET status = :status, responderName = :responder WHERE id = :incidentId")
    suspend fun updateIncidentStatus(incidentId: String, status: String, responder: String)

    @Query("UPDATE incidents SET status = 'RESOLVED', resolutionNotes = :notes WHERE id = :incidentId")
    suspend fun resolveIncident(incidentId: String, notes: String)

    @Query("SELECT COUNT(*) FROM incidents WHERE status != 'RESOLVED'")
    suspend fun getActiveIncidentCount(): Int
}
