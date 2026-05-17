package com.hafiz.nimmaguru

import android.app.Application
import com.google.firebase.FirebaseApp

class NimmaGuruApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
