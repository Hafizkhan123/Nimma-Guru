package com.hafiz.nimmaguru.data.model

data class Guru(
    val id: String = "",
    val name: String = "",
    val skills: List<String> = emptyList(),
    val freeHours: String = "",
    val village: String = "",
    val bio: String = "",
    val appreciationCount: Int = 0,
    val photoUrl: String = "",
    val userId: String = ""
)

data class ClassSession(
    val id: String = "",
    val guruId: String = "",
    val guruName: String = "",
    val subject: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = ""
)

data class ThankYouNote(
    val id: String = "",
    val guruId: String = "",
    val studentName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
