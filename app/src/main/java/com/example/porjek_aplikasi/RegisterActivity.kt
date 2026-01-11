package com.example.porjek_aplikasi

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        tilUsername = findViewById(R.id.til_username)
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        tvLogin = findViewById(R.id.tv_login)

        // Register button click
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword)) {
                // Save user credentials to SharedPreferences with username-specific keys
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                
                // Store credentials per-username to support multiple accounts
                editor.putString("user_${username}_password", password)
                editor.putString("user_${username}_email", email)
                editor.apply()
                
                // Registration successful - navigate to LoginActivity (not MainActivity)
                Toast.makeText(this, "Registrasi berhasil! Silakan login dengan akun Anda.", Toast.LENGTH_LONG).show()
                
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("USERNAME", username) // Pre-fill username in login form
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // Login text click
        tvLogin.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun validateInput(username: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        // Validate username
        if (username.isEmpty()) {
            tilUsername.error = "Username tidak boleh kosong"
            isValid = false
        } else if (username.length < 3) {
            tilUsername.error = "Username minimal 3 karakter"
            isValid = false
        } else {
            tilUsername.error = null
        }

        // Validate email
        if (email.isEmpty()) {
            tilEmail.error = "Email tidak boleh kosong"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Format email tidak valid"
            isValid = false
        } else {
            tilEmail.error = null
        }

        // Validate password
        if (password.isEmpty()) {
            tilPassword.error = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "Password minimal 6 karakter"
            isValid = false
        } else {
            tilPassword.error = null
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
            isValid = false
        } else if (confirmPassword != password) {
            tilConfirmPassword.error = "Password tidak cocok"
            isValid = false
        } else {
            tilConfirmPassword.error = null
        }

        if (!isValid) {
            Toast.makeText(this, "Mohon lengkapi semua field dengan benar", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }
}
