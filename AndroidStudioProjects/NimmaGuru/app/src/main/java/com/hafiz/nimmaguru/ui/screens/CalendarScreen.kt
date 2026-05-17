package com.hafiz.nimmaguru.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hafiz.nimmaguru.data.model.ClassSession
import com.hafiz.nimmaguru.data.repository.FirestoreRepository
import com.hafiz.nimmaguru.data.repository.GeminiRepository
import com.hafiz.nimmaguru.ui.theme.SaffronOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val repo = remember { FirestoreRepository() }
    val geminiRepo = remember { GeminiRepository() }
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth

    val sessions by repo.getSessions().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var aiTip by remember { mutableStateOf<String?>(null) }
    var isLoadingTip by remember { mutableStateOf(false) }

    // Add session form state
    var subject by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Class Calendar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Upcoming sessions", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                }
            )
        },
        floatingActionButton = {
            if (auth.currentUser != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = SaffronOrange
                ) {
                    Icon(Icons.Filled.Add, "Add session", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // AI Tip section
            if (aiTip != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("✨", fontSize = 24.sp)
                        Column {
                            Text("AI Teaching Tip", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(aiTip!!, fontSize = 15.sp, lineHeight = 22.sp)
                        }
                    }
                }
            }

            if (sessions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No sessions scheduled", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "Tap + to add a session",
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
                    items(sessions) { session ->
                        SessionCard(session = session, onGetTip = {
                            isLoadingTip = true
                            scope.launch {
                                val result = geminiRepo.generateSessionTip(session.subject)
                                result.onSuccess { tip -> aiTip = tip }
                                    .onFailure { aiTip = "Could not load tip. Check internet." }
                                isLoadingTip = false
                            }
                        })
                    }
                }
            }
        }
    }

    // Add Session Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add a Class Session", fontSize = 20.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val subjectOptions = listOf("Math", "Science", "English", "Carpentry", "Computers", "History")
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subject") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            subjectOptions.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, fontSize = 16.sp) },
                                    onClick = { subject = s; expanded = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        placeholder = { Text("e.g. Sunday, 15 June 2025") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time") },
                        placeholder = { Text("e.g. 10:00 AM - 12:00 PM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Samudaya Bhavana Location") },
                        placeholder = { Text("e.g. Rajajinagar Community Hall") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isBlank() || date.isBlank() || time.isBlank() || location.isBlank()) return@Button
                        isSaving = true
                        scope.launch {
                            try {
                                val guruName = auth.currentUser?.displayName ?: "Guru"
                                val guruProfile = repo.getGuruByUserId(auth.currentUser?.uid ?: "")
                                repo.addSession(
                                    ClassSession(
                                        guruId = auth.currentUser?.uid ?: "",
                                        guruName = guruName,
                                        subject = subject,
                                        date = date,
                                        time = time,
                                        location = location
                                    )
                                )
                                subject = ""; date = ""; time = ""; location = ""
                                showAddDialog = false
                                snackbarHostState.showSnackbar("Session added! ✅")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to add session.")
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange)
                ) {
                    if (isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                    else Text("Add Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SessionCard(session: ClassSession, onGetTip: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SaffronOrange
                ) {
                    Text(
                        session.subject,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onGetTip) {
                    Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Get Tip", fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("👤 ${session.guruName}", fontSize = 16.sp)
            Text("📅 ${session.date}", fontSize = 16.sp)
            Text("🕐 ${session.time}", fontSize = 16.sp)
            Text("📍 ${session.location}", fontSize = 16.sp)
        }
    }
}
