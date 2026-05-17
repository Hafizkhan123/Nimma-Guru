package com.hafiz.nimmaguru.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hafiz.nimmaguru.data.repository.FirestoreRepository
import com.hafiz.nimmaguru.ui.theme.SaffronOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallOfFameScreen() {
    val repo = remember { FirestoreRepository() }
    val topGurus by repo.getTopGurus().collectAsState(initial = emptyList())

    val rankColors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFC0C0C0), // Silver
        Color(0xFFCD7F32)  // Bronze
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.EmojiEvents, null, tint = SaffronOrange)
                    Text("Wall of Fame", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        )

        if (topGurus.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏆", fontSize = 64.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No Gurus yet!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Be the first to create a profile\nand start teaching.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }
            }
        } else {
            // Top 3 podium section
            if (topGurus.size >= 1) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SaffronOrange.copy(0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆 Top Guru", fontSize = 16.sp, color = SaffronOrange)
                        Spacer(Modifier.height(8.dp))
                        Text(topGurus[0].name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${topGurus[0].appreciationCount} appreciations",
                            fontSize = 18.sp,
                            color = SaffronOrange
                        )
                        Text(topGurus[0].skills.take(3).joinToString(", "), fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(topGurus) { index, guru ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (index < 3) 4.dp else 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Rank badge
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (index < rankColors.size) rankColors[index].copy(0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(
                                    Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (index < 3) {
                                        Text(
                                            listOf("🥇", "🥈", "🥉")[index],
                                            fontSize = 24.sp
                                        )
                                    } else {
                                        Text(
                                            "#${index + 1}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(guru.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    guru.village,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                                Text(
                                    guru.skills.take(2).joinToString(", "),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                            }

                            // Appreciation count
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    null,
                                    tint = if (index < 3) SaffronOrange else MaterialTheme.colorScheme.onSurface.copy(0.5f),
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "${guru.appreciationCount}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index < 3) SaffronOrange else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
