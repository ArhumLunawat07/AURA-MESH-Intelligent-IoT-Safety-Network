package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IotNode
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeDao {
    @Query("SELECT * FROM nodes ORDER BY id ASC")
    fun getAllNodes(): Flow<List<IotNode>>

    @Query("SELECT * FROM nodes WHERE id = :nodeId LIMIT 1")
    suspend fun getNodeById(nodeId: String): IotNode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNodes(nodes: List<IotNode>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNode(node: IotNode)

    @Update
    suspend fun updateNode(node: IotNode)

    @Query("UPDATE nodes SET lightBrightness = :brightness WHERE id = :nodeId")
    suspend fun updateLightBrightness(nodeId: String, brightness: Int)

    @Query("UPDATE nodes SET lightBrightness = :brightness, isEmergencyStrobe = :isStrobe WHERE id IN (:nodeIds)")
    suspend fun updateNodesEmergency(nodeIds: List<String>, brightness: Int, isStrobe: Boolean)

    @Query("UPDATE nodes SET motionDetected = :motion, lightBrightness = :brightness WHERE id = :nodeId")
    suspend fun updateMotionAndLight(nodeId: String, motion: Boolean, brightness: Int)

    @Query("UPDATE nodes SET ambientLux = :lux")
    suspend fun updateAllAmbientLux(lux: Float)

    @Query("UPDATE nodes SET status = :status WHERE id = :nodeId")
    suspend fun updateNodeStatus(nodeId: String, status: String)

    @Query("SELECT COUNT(*) FROM nodes")
    suspend fun getNodeCount(): Int
}
