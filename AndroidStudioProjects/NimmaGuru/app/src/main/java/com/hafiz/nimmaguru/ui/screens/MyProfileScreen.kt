package com.hafiz.nimmaguru.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hafiz.nimmaguru.data.model.Guru
import com.hafiz.nimmaguru.data.repository.FirestoreRepository
import com.hafiz.nimmaguru.data.repository.GeminiRepository
import com.hafiz.nimmaguru.ui.theme.SaffronOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(onLogout: () -> Unit) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val repo = remember { FirestoreRepository() }
    val geminiRepo = remember { GeminiRepository() }
    val scope = rememberCoroutineScope()

    // Form state
    var name by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var village by remember { mutableStateOf("") }
    var freeHours by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedSkills by remember { mutableStateOf(setOf<String>()) }
    var existingGuruId by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var isGeneratingBio by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val allSkills = listOf("Math", "Science", "English", "Carpentry", "Computers", "History", "Art", "Music")

    // Load existing profile
    val userId = currentUser?.uid ?: ""
    val existingProfile by repo.getGuruByUserId(userId).collectAsState(initial = null)

    LaunchedEffect(existingProfile) {
        existingProfile?.let { guru ->
            if (existingGuruId.isEmpty()) {
                existingGuruId = guru.id
                name = guru.name
                village = guru.village
                freeHours = guru.freeHours
                bio = guru.bio
                selectedSkills = guru.skills.toSet()
            }
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Profile saved successfully! ✅")
            saveSuccess = false
        }
    }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            errorMsg = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Guru Profile") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, "Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User info card
            if (currentUser != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SaffronOrange.copy(0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(50), color = SaffronOrange) {
                            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    (currentUser.displayName ?: "G").take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Column {
                            Text(currentUser.displayName ?: "Guru", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(currentUser.email ?: "", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        }
                    }
                }
            }

            Text("Profile Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            // Village
            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text("Village / Area") },
                placeholder = { Text("e.g. Rajajinagar, Bengaluru") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            // Free Hours
            OutlinedTextField(
                value = freeHours,
                onValueChange = { freeHours = it },
                label = { Text("Available Hours") },
                placeholder = { Text("e.g. Weekends 10am-12pm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            // Skill selection
            Text("Subjects I can Teach", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            allSkills.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { skill ->
                        FilterChip(
                            selected = skill in selectedSkills,
                            onClick = {
                                selectedSkills = if (skill in selectedSkills)
                                    selectedSkills - skill else selectedSkills + skill
                            },
                            label = { Text(skill, fontSize = 15.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Bio with AI generation
            Text("About Me (Bio)", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                placeholder = { Text("Write something about yourself or use AI to generate...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )

            // Generate Bio with AI
            OutlinedButton(
                onClick = {
                    if (name.isBlank() || selectedSkills.isEmpty()) {
                        errorMsg = "Please enter your name and select at least one skill first."
                        return@OutlinedButton
                    }
                    isGeneratingBio = true
                    scope.launch {
                        val result = geminiRepo.generateBio(
                            name = name,
                            skills = selectedSkills.toList(),
                            village = village.ifBlank { "Karnataka" }
                        )
                        result.onSuccess { generatedBio ->
                            bio = generatedBio
                        }.onFailure {
                            errorMsg = "AI generation failed. Check your API key and internet."
                        }
                        isGeneratingBio = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isGeneratingBio
            ) {
                if (isGeneratingBio) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generating bio...", fontSize = 16.sp)
                } else {
                    Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Bio with AI ✨", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank() || village.isBlank()) {
                        errorMsg = "Name and Village are required."
                        return@Button
                    }
                    isSaving = true
                    scope.launch {
                        try {
                            val guru = Guru(
                                id = existingGuruId,
                                name = name.trim(),
                                village = village.trim(),
                                freeHours = freeHours.trim(),
                                bio = bio.trim(),
                                skills = selectedSkills.toList(),
                                userId = userId
                            )
                            existingGuruId = repo.saveGuru(guru)
                            saveSuccess = true
                        } catch (e: Exception) {
                            errorMsg = "Save failed: ${e.message}"
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        if (existingGuruId.isEmpty()) "Create Profile" else "Update Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
