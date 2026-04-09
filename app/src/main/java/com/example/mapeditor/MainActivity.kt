package com.example.mapeditor // Assure-toi que cela correspond au nom de ton projet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

// --- THEME "DARK FANTASY" ---
val ColorBg = Color(0xFF141419)
val ColorGrid = Color(0xFF505050)
val ColorBtnNormal = Color(0xFF32323C)
val ColorBtnActive = Color(0xFFB48C32)
val ColorBorderGold = Color(0xFFA07828)
val ColorText = Color(0xFFDCDCC8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapEditorAndroidApp()
        }
    }
}

@Composable
fun MapEditorAndroidApp() {
    // État de l'outil actuel (Placer ou Gommer)
    var currentTool by remember { mutableStateOf("PLACE") }
    
    // Configuration de la grille (10x10 pour commencer sur mobile)
    val gridSize = 10
    
    // Matrice de la carte (0 = vide, 1 = tuile posée)
    var grid by remember { mutableStateOf(Array(gridSize) { IntArray(gridSize) { 0 } }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
    ) {
        // --- MENU HAUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorBtnNormal)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Map Editor VTT", color = ColorText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { /* TODO: Logique d'export VTT sur mobile */ },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBtnActive)
            ) {
                Text("EXPORT", color = ColorText, fontWeight = FontWeight.Bold)
            }
        }

        // --- ZONE DE DESSIN (GRILLE) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Gestion du tactile
                        detectTapGestures { offset ->
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val cellSize = min(canvasWidth / gridSize, canvasHeight / gridSize)
                            
                            // Calcul de la case touchée
                            val x = (offset.x / cellSize).toInt()
                            val y = (offset.y / cellSize).toInt()

                            // Si on touche bien dans la grille
                            if (x in 0 until gridSize && y in 0 until gridSize) {
                                // Copie de la grille pour forcer la mise à jour visuelle (Recomposition)
                                val newGrid = grid.map { it.clone() }.toTypedArray()
                                if (currentTool == "PLACE") {
                                    newGrid[x][y] = 1
                                } else {
                                    newGrid[x][y] = 0
                                }
                                grid = newGrid
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val cellSize = min(canvasWidth / gridSize, canvasHeight / gridSize)

                // 1. Dessin des tuiles posées
                for (x in 0 until gridSize) {
                    for (y in 0 until gridSize) {
                        if (grid[x][y] == 1) {
                            drawRect(
                                color = Color.LightGray, // Remplacera par des images plus tard
                                topLeft = Offset(x * cellSize, y * cellSize),
                                size = Size(cellSize, cellSize)
                            )
                        }
                    }
                }

                // 2. Dessin de la grille
                for (i in 0..gridSize) {
                    // Lignes verticales
                    drawLine(
                        color = ColorGrid,
                        start = Offset(i * cellSize, 0f),
                        end = Offset(i * cellSize, gridSize * cellSize),
                        strokeWidth = 2f
                    )
                    // Lignes horizontales
                    drawLine(
                        color = ColorGrid,
                        start = Offset(0f, i * cellSize),
                        end = Offset(gridSize * cellSize, i * cellSize),
                        strokeWidth = 2f
                    )
                }
            }
        }

        // --- MENU OUTILS (BAS) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FantasyButton(text = "POSER", isActive = currentTool == "PLACE") {
                currentTool = "PLACE"
            }
            FantasyButton(text = "GOMME", isActive = currentTool == "ERASE") {
                currentTool = "ERASE"
            }
        }
    }
}

// Composant Bouton réutilisable au style RPG
@Composable
fun FantasyButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    val bgColor = if (isActive) ColorBtnActive else ColorBtnNormal
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = CutCornerShape(8.dp),
        modifier = Modifier.border(2.dp, ColorBorderGold, CutCornerShape(8.dp))
    ) {
        Text(text, color = ColorText, fontWeight = FontWeight.Bold)
    }
}