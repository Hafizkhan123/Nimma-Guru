package com.hafiz.nimmaguru.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.hafiz.nimmaguru.data.model.ClassSession
import com.hafiz.nimmaguru.data.model.Guru
import com.hafiz.nimmaguru.data.model.ThankYouNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = Firebase.firestore

    // ─── Guru Operations ───────────────────────────────────────────────

    fun getGurus(skillFilter: String? = null): Flow<List<Guru>> = callbackFlow {
        val query = if (skillFilter != null && skillFilter != "All")
            db.collection("gurus").whereArrayContains("skills", skillFilter)
        else
            db.collection("gurus")

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Guru>()?.copy(id = doc.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun getGuruByUserId(userId: String): Flow<Guru?> = callbackFlow {
        val listener = db.collection("gurus")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val guru = snapshot?.documents?.firstOrNull()?.let { doc ->
                    doc.toObject<Guru>()?.copy(id = doc.id)
                }
                trySend(guru)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveGuru(guru: Guru): String {
        return if (guru.id.isEmpty()) {
            val ref = db.collection("gurus").add(guru).await()
            ref.id
        } else {
            db.collection("gurus").document(guru.id).set(guru).await()
            guru.id
        }
    }

    suspend fun deleteGuru(guruId: String) {
        db.collection("gurus").document(guruId).delete().await()
    }

    // ─── Thank You Notes ───────────────────────────────────────────────

    fun getThankYouNotes(guruId: String): Flow<List<ThankYouNote>> = callbackFlow {
        val listener = db.collection("thankYouNotes")
            .whereEqualTo("guruId", guruId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val notes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ThankYouNote>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(notes)
            }
        awaitClose { listener.remove() }
    }

    suspend fun postThankYouNote(note: ThankYouNote) {
        db.collection("thankYouNotes").add(note).await()
        db.collection("gurus").document(note.guruId)
            .update("appreciationCount", FieldValue.increment(1)).await()
    }

    // ─── Class Sessions ────────────────────────────────────────────────

    fun getSessions(): Flow<List<ClassSession>> = callbackFlow {
        val listener = db.collection("sessions")
            .orderBy("date")
            .addSnapshotListener { snapshot, _ ->
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<ClassSession>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addSession(session: ClassSession) {
        db.collection("sessions").add(session).await()
    }

    // ─── Wall of Fame ──────────────────────────────────────────────────

    fun getTopGurus(): Flow<List<Guru>> = callbackFlow {
        val listener = db.collection("gurus")
            .orderBy("appreciationCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Guru>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}
