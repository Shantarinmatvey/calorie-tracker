package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.TrackerRepository
import com.example.ui.TrackerAppUI
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TrackerViewModel
import com.example.viewmodel.TrackerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize core database and repository
        val database = AppDatabase.getDatabase(this)
        val repository = TrackerRepository(database.trackerDao())

        // Obtain TrackerViewModel utilizing the provider factory
        val viewModel = ViewModelProvider(
            this,
            TrackerViewModelFactory(repository)
        )[TrackerViewModel::class.java]

        setContent {
            MyApplicationTheme(dynamicColor = false) {
                TrackerAppUI(viewModel = viewModel)
            }
        }
    }
}
