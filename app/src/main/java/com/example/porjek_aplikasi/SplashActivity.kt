package com.example.porjek_aplikasi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is already logged in
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
            val loggedInUsername = sharedPreferences.getString("logged_in_username", null)
            
            if (isLoggedIn && loggedInUsername != null) {
                // User is already logged in, go directly to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USERNAME", loggedInUsername)
                startActivity(intent)
            } else {
                // User not logged in, go to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
