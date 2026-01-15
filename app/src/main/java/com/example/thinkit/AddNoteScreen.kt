package com.example.thinkit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thinkit.ui.model.Note

@Composable
fun AddNoteScreen(
    noteToEdit: Note?,
    onNoteSaved: (String, String, Long, String?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var description by remember { mutableStateOf(noteToEdit?.description ?: "") }

    // État de la couleur (initialisée avec la couleur de la note ou Blanc par défaut)
    var selectedColor by remember { mutableStateOf(noteToEdit?.couleur ?: 0xFFFFFFFF) }

    // État pour afficher/masquer le sélecteur de couleurs
    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Retour")
                }
                IconButton(onClick = {
                    if (title.isNotBlank()) {
                        onNoteSaved(title, description, selectedColor, noteToEdit?.id)
                        onBack()
                    }
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Sauvegarder")
                }
            }
        },
        containerColor = Color(selectedColor)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) Text("Titre", fontSize = 40.sp, color = Color.Gray.copy(0.5f))
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = TextStyle(fontSize = 20.sp, color = Color.DarkGray),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) Text("Commencez à écrire...", fontSize = 20.sp, color = Color.Gray.copy(0.5f))
                        innerTextField()
                    }
                )
            }

            // BOUTON ROUE CHROMATIQUE : Ouvre le dialogue de sélection
            Image(
                painter = painterResource(id = R.drawable.color_wheel),
                modifier = Modifier
                    .size(60.dp)
                    .clickable { showColorPicker = true }, // Ouvre le sélecteur
                contentDescription = "Choisir une couleur"
            )
        }

        // Affiche le dialogue si showColorPicker est vrai
        if (showColorPicker) {
            ColorPickerDialog(
                initialColor = selectedColor,
                onColorSelected = { color ->
                    selectedColor = color
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Long,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // Liste élargie de couleurs pour donner du choix à l'utilisateur
    val availableColors = listOf(
        0xFFFFD1DF, 0xFFE0D7FF, 0xFFD1FFEA, 0xFFFFF4D1, 0xFFF7F9E7,
        0xFFB2EBF2, 0xFFFFE0B2, 0xFFF8BBD0, 0xFFDCEDC8, 0xFFD1C4E9,
        0xFFFFFFFF, 0xFFE0E0E0, 0xFFFFCCBC, 0xFFCFD8DC, 0xFFF0F4C3
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choisir la couleur de fond", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))

                // Grille de couleurs
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(availableColors) { colorLong ->
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(
                                    width = if (initialColor == colorLong) 3.dp else 1.dp,
                                    color = if (initialColor == colorLong) Color.Black else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(colorLong) }
                        )
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Annuler")
                }
            }
        }
    }
}