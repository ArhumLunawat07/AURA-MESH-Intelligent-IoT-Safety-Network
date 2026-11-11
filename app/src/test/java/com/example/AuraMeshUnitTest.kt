package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AuraMeshDatabase
import com.example.data.model.IncidentSeverity
import com.example.data.model.IncidentStatus
import com.example.data.model.NodeStatus
import com.example.data.repository.AuraMeshRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuraMeshUnitTest {

    private lateinit var db: AuraMeshDatabase
    private lateinit var repository: AuraMeshRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AuraMeshDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AuraMeshRepository(context, db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `test initial node seeding contains 5 healthy nodes`() = runBlocking {
        // Wait briefly for seeding coroutine
        var nodes = repository.allNodes.first()
        var retries = 0
        while (nodes.isEmpty() && retries < 10) {
            kotlinx.coroutines.delay(100)
            nodes = repository.allNodes.first()
            retries++
        }

        assertTrue(nodes.isNotEmpty())
        assertEquals(5, nodes.size)
        assertTrue(nodes.all { it.status == NodeStatus.HEALTHY })
    }

    @Test
    fun `test emergency SOS cascades to neighbor nodes`() = runBlocking {
        // Ensure seeded
        var nodes = repository.allNodes.first()
        while (nodes.isEmpty()) {
            kotlinx.coroutines.delay(50)
            nodes = repository.allNodes.first()
        }

        repository.triggerEmergencySos("NODE-01", IncidentSeverity.CRITICAL)
        kotlinx.coroutines.delay(200)

        val updatedNodes = repository.allNodes.first().associateBy { it.id }
        val originNode = updatedNodes["NODE-01"]
        assertNotNull(originNode)
        assertEquals(NodeStatus.EMERGENCY, originNode?.status)
        assertEquals(100, originNode?.lightBrightness)
        assertTrue(originNode?.isEmergencyStrobe == true)

        val incidents = repository.activeIncidents.first()
        assertTrue(incidents.isNotEmpty())
        assertEquals("NODE-01", incidents.first().originatingNodeId)
        assertEquals(IncidentStatus.ACTIVE, incidents.first().status)
    }

    @Test
    fun `test safe route graph calculation`() = runBlocking {
        // Ensure seeded
        var nodes = repository.allNodes.first()
        while (nodes.isEmpty()) {
            kotlinx.coroutines.delay(50)
            nodes = repository.allNodes.first()
        }

        val route = repository.calculateSafeRoute("NODE-01", "NODE-05")
        assertNotNull(route)
        assertEquals("NODE-01", route.pathNodeIds.first())
        assertEquals("NODE-05", route.pathNodeIds.last())
        assertEquals(100, route.coveragePercent)
        assertFalse(route.hasOfflineNode)
    }

    @Test
    fun `test offline WAN disconnect retains local mesh network state`() = runBlocking {
        assertTrue(repository.networkState.value.wanConnected)
        repository.toggleWanInternet()
        assertFalse(repository.networkState.value.wanConnected)
        assertTrue(repository.networkState.value.localGatewayConnected)
    }
}
