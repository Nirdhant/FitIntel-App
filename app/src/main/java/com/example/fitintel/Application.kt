package com.example.fitintel

import android.app.Application
import com.example.gemini.FirebaseGeminiLogic

class FitIntelApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseGeminiLogic.init(this)
    }
}