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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    var selectedColor by remember { mutableLongStateOf(noteToEdit?.couleur ?: 0xFFF7F9E7) }
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
                        val noteIdString = noteToEdit?.id?.toString()
                        onNoteSaved(title, description, selectedColor, noteIdString)
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

            // ROUE CHROMATIQUE
            Image(
                painter = painterResource(id = R.drawable.color_wheel),
                modifier = Modifier
                    .size(60.dp)
                    .clickable { showColorPicker = true },
                contentDescription = "Choisir une couleur"
            )
        }

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
    // Liste de suggestions rapides
    val quickColors = listOf(
        0xFFFFD1DF, 0xFFE0D7FF, 0xFFD1FFEA, 0xFFFFF4D1, 0xFFF7F9E7,
        0xFFB2EBF2, 0xFFFFE0B2, 0xFFFFFFFF, 0xFFE0E0E0, 0xFFCFD8DC
    )

    // État pour le slider de couleur personnalisée
    var hue by remember { mutableFloatStateOf(0f) }
    val customColor = Color.hsv(hue, 0.2f, 0.95f) // Couleurs douces (Pastels) pour le fond

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
                Text("Couleur personnalisée", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(20.dp))

                // 1. LE SLIDER ARC-EN-CIEL (Choix libre)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red, Color.Yellow, Color.Green,
                                    Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                                )
                            )
                        )
                )
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    modifier = Modifier.fillMaxWidth()
                )

                // Aperçu de la couleur choisie via le slider
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(customColor)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable { onColorSelected(customColor.toArgb().toLong()) }
                )
                Text("Appuyer pour choisir cette teinte", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                // 2. LA GRILLE (Choix rapides)
                Text("Suggestions rapides", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(110.dp)
                ) {
                    items(quickColors) { colorLong ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(35.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(
                                    width = if (initialColor == colorLong) 2.dp else 0.5.dp,
                                    color = if (initialColor == colorLong) Color.Black else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(colorLong) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Annuler") }
                }
            }
        }
    }
}