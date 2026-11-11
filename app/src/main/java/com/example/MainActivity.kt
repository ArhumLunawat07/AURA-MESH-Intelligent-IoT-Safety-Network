package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.local.AuraMeshDatabase
import com.example.data.repository.AuraMeshRepository
import com.example.ui.AuraMeshApp
import com.example.ui.theme.AuraMeshTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AuraMeshDatabase.getDatabase(applicationContext)
        val repository = AuraMeshRepository(applicationContext, database)

        setContent {
            AuraMeshTheme {
                AuraMeshApp(repository = repository)
            }
        }
    }
}
