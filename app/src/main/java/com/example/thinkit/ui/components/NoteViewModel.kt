package com.example.thinkit.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkit.ui.model.Note
import com.example.thinkit.ui.model.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {

    // Liste des notes observée par l'interface
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        // Charger les notes automatiquement au démarrage
        viewModelScope.launch {
            noteDao.getAllNotes().collect { list ->
                _notes.value = list
            }
        }
    }

    // Sauvegarder (Ajout ou Modification)
    fun saveNote(title: String, description: String, color: Long, id: String? = null) {
        viewModelScope.launch {
            val note = Note(
                id = id ?: UUID.randomUUID().toString(),
                title = title,
                description = description,
                couleur = color
            )
            noteDao.insertOrUpdate(note)
        }
    }

    // Supprimer une note
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }
}