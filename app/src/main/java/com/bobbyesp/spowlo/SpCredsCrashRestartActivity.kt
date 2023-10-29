package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SpCredsCrashRestartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restart the app by launching the main activity
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("spotifyCredsCrash", true)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}