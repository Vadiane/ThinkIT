package com.example.thinkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.thinkit.ui.components.NoteViewModel
import com.example.thinkit.ui.model.Note
import com.example.thinkit.ui.model.NoteDao
import com.example.thinkit.ui.model.NoteDatabase
import com.example.thinkit.ui.theme.ThinkITTheme

// Factory pour créer le ViewModel avec Room
class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialisation UNIQUE de la base de données Room au démarrage
        val db = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java, "notes_db"
        )
            .fallbackToDestructiveMigration() // Essentiel pour éviter les crashs de version
            .build()

        // 2. Création du ViewModel
        val viewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(db.noteDao())
        )[NoteViewModel::class.java]

        setContent {
            ThinkITTheme {
                val navController = rememberNavController()

                // État pour la note en cours d'édition
                var currentNoteToEdit by remember { mutableStateOf<Note?>(null) }

                NavHost(navController = navController, startDestination = "main") {

                    // Écran de la liste des notes
                    composable("main") {
                        MainScreen(viewModel = viewModel) { note ->
                            currentNoteToEdit = note
                            navController.navigate("add")
                        }
                    }

                    // Écran d'ajout ou de modification
                    composable("add") {
                        AddNoteScreen(
                            noteToEdit = currentNoteToEdit,
                            onNoteSaved = { title, description, color, id ->
                                viewModel.saveNote(title, description, color, id)
                                currentNoteToEdit = null // On nettoie l'état
                                navController.popBackStack() // On revient à la liste
                            },
                            onBack = {
                                currentNoteToEdit = null // On nettoie l'état
                                navController.popBackStack() // On revient simplement en arrière
                            }
                        )
                    }
                }
            }
        }
    }
}