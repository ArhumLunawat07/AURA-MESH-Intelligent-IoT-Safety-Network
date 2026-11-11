package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.EmergencyIncident
import com.example.data.model.IotNode
import com.example.data.model.TelemetryLog

@Database(
    entities = [IotNode::class, EmergencyIncident::class, TelemetryLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AuraMeshDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
    abstract fun incidentDao(): IncidentDao
    abstract fun telemetryDao(): TelemetryDao

    companion object {
        @Volatile
        private var INSTANCE: AuraMeshDatabase? = null

        fun getDatabase(context: Context): AuraMeshDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraMeshDatabase::class.java,
                    "aura_mesh_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
