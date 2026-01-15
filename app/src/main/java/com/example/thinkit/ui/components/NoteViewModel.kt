package com.example.thinkit.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkit.ui.model.Note
import com.example.thinkit.ui.model.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    // Transformation du Flow de Room en StateFlow pour l'interface
    val notes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveNote(title: String, description: String, color: Long, id: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = if (id == null) {
                // Création : l'ID 0 avec autoGenerate=true créera un nouvel ID
                Note(title = title, description = description, couleur = color)
            } else {
                // Modification : on convertit l'ID existant
                Note(id = id.toInt(), title = title, description = description, couleur = color)
            }
            noteDao.insertOrUpdate(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.delete(note)
        }
    }
}