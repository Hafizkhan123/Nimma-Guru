
package com.hafiz.nimmaguru.ui.screens
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hafiz.nimmaguru.data.model.Guru
import com.hafiz.nimmaguru.data.repository.FirestoreRepository
import com.hafiz.nimmaguru.ui.theme.SaffronOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onGuruClick: (String) -> Unit) {
    val repo = remember { FirestoreRepository() }
    val skillOptions = listOf("All", "Math", "Science", "English", "Carpentry", "Computers", "History")
    var selectedSkill by remember { mutableStateOf("All") }

    val gurus by repo.getGurus(selectedSkill.takeIf { it != "All" })
        .collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text("ನಿಮ್ಮ ಗುರು", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SaffronOrange)
                    Text("Find a Community Guru", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Skill Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(skillOptions) { skill ->
                FilterChip(
                    selected = selectedSkill == skill,
                    onClick = { selectedSkill = skill },
                    label = { Text(skill, fontSize = 15.sp) },
                    modifier = Modifier.height(40.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronOrange,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }

        // Results count
        Text(
            text = "${gurus.size} Guru${if (gurus.size != 1) "s" else ""} found",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Guru List
        if (gurus.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧑‍🏫", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Gurus found", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "Be the first to register!",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gurus) { guru ->
                    GuruCard(guru = guru, onClick = { onGuruClick(guru.id) })
                }
            }
        }
    }
}

@Composable
fun GuruCard(guru: Guru, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            if (guru.photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = guru.photoUrl,
                    contentDescription = "Profile photo of ${guru.name}",
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(color = SaffronOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = guru.name.take(2).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronOrange
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(guru.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    guru.village,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Skill tags
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(guru.skills.take(3)) { skill ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = skill,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Appreciation count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = SaffronOrange, modifier = Modifier.size(20.dp))
                Text(
                    text = "${guru.appreciationCount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SaffronOrange
                )
            }
        }
    }
}

