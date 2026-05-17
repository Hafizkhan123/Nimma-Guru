package com.hafiz.nimmaguru.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hafiz.nimmaguru.data.model.ThankYouNote
import com.hafiz.nimmaguru.data.repository.FirestoreRepository
import com.hafiz.nimmaguru.ui.theme.SaffronOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruDetailScreen(guruId: String, onBack: () -> Unit) {
    val repo = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth

    val gurus by repo.getGurus().collectAsState(initial = emptyList())
    val guru = gurus.find { it.id == guruId }
    val notes by repo.getThankYouNotes(guruId).collectAsState(initial = emptyList())

    var showThankYouDialog by remember { mutableStateOf(false) }
    var thankYouMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(guru?.name ?: "Guru Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (guru == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SaffronOrange.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (guru.photoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = guru.photoUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(96.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(96.dp).clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(shape = CircleShape, color = SaffronOrange) {
                                        Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                guru.name.take(2).uppercase(),
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(guru.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "📍 ${guru.village}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                            )
                            Text(
                                "🕐 Free hours: ${guru.freeHours}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Favorite, null, tint = SaffronOrange, modifier = Modifier.size(20.dp))
                                Text(
                                    "${guru.appreciationCount} thank-yous received",
                                    fontSize = 16.sp,
                                    color = SaffronOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Bio
                if (guru.bio.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("About", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text(guru.bio, fontSize = 16.sp, lineHeight = 24.sp)
                            }
                        }
                    }
                }

                // Skills
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Subjects", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            guru.skills.forEach { skill ->
                                Text("• $skill", fontSize = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }

                // Say Thank You Button
                item {
                    Button(
                        onClick = { showThankYouDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange)
                    ) {
                        Text("🙏  Say Thank You", fontSize = 18.sp)
                    }
                }

                // Thank You Notes
                item {
                    Text(
                        "Appreciation Wall (${notes.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (notes.isEmpty()) {
                    item {
                        Text(
                            "No thank-you notes yet. Be the first!",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    items(notes) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    note.studentName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("\"${note.message}\"", fontSize = 16.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Thank You Dialog
    if (showThankYouDialog) {
        AlertDialog(
            onDismissRequest = { showThankYouDialog = false },
            title = { Text("Say Thank You 🙏", fontSize = 20.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Write a message of appreciation:", fontSize = 16.sp)
                    OutlinedTextField(
                        value = thankYouMessage,
                        onValueChange = { thankYouMessage = it },
                        placeholder = { Text("This Guru helped me understand...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (thankYouMessage.isBlank()) return@Button
                        isSending = true
                        val currentUser = auth.currentUser
                        val note = ThankYouNote(
                            guruId = guruId,
                            studentName = currentUser?.displayName ?: "A Student",
                            message = thankYouMessage.trim()
                        )
                        scope.launch {
                            try {
                                repo.postThankYouNote(note)
                                thankYouMessage = ""
                                showThankYouDialog = false
                                snackMessage = "Thank you posted! 🙏"
                            } catch (e: Exception) {
                                snackMessage = "Failed to post. Please try again."
                            } finally {
                                isSending = false
                            }
                        }
                    },
                    enabled = !isSending && thankYouMessage.isNotBlank()
                ) {
                    if (isSending) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showThankYouDialog = false }) { Text("Cancel") }
            }
        )
    }
}
