package com.example.thinkit

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// CORRECTION DES IMPORTS
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thinkit.ui.components.NoteViewModel
import com.example.thinkit.ui.model.Note

@Composable
fun MainScreen(viewModel: NoteViewModel, onNavigateToAdd: (Note?) -> Unit) {
    // Utiliser collectAsState avec une valeur initiale vide pour éviter les flashs blancs
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAdd(null) },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    // Attention : l'offset peut cacher le bouton sur certains écrans
                    .offset(y = (10).dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFF7F9E7)
    ) { padding ->
        // On utilise fillMaxSize() avec le padding du Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Applique le padding du Scaffold
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Text(
                "LISTES DES NOTES",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            if (notes.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Aucune note pour l'instant", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                    // Ajouter un petit padding en bas pour que la dernière note
                    // ne soit pas cachée par le bouton flottant
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNavigateToAdd(note) },
                            onDelete = { noteToDelete = note }
                        )
                    }
                }
            }
        }

        // Dialogue de suppression
        if (noteToDelete != null) {
            AlertDialog(
                onDismissRequest = { noteToDelete = null },
                title = { Text("Supprimer ?") },
                text = { Text("Voulez-vous vraiment supprimer cette note définitivement ?") },
                confirmButton = {
                    TextButton(onClick = {
                        noteToDelete?.let { viewModel.deleteNote(it) }
                        noteToDelete = null
                    }) {
                        Text("Supprimer", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteToDelete = null }) {
                        Text("Annuler", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        // On s'assure que la couleur de fond est bien appliquée
        colors = CardDefaults.cardColors(containerColor = Color(note.couleur))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    note.description,
                    fontSize = 16.sp,
                    maxLines = 3, // Augmenté à 3 pour voir un extrait
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}