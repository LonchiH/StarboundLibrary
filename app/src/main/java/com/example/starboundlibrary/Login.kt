package com.example.starboundlibrary

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.starboundlibrary.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingProgress: ProgressBar
    private lateinit var signBtn: Button
    private lateinit var userEmail: TextInputLayout
    private lateinit var userPassword: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // go to signup
        binding.btnSignUPPP.setOnClickListener {
            startActivity(Intent(this@Login, Signup::class.java))
        }

        userEmail = binding.logEmail
        userPassword = binding.logPassword
        signBtn = binding.signBtn
        loadingProgress = binding.progressBar

        loadingProgress.visibility = View.INVISIBLE

        signBtn.setOnClickListener {

            signBtn.setVisibility(View.INVISIBLE)
            loadingProgress.setVisibility(View.VISIBLE)
            val email: String = binding.logEmail.editText?.text.toString()
            val password: String = binding.logPassword.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {

                // something goes wrong : all fields must be filled
                // we need to display an error message
                showToast(this, "Please Verify all fields")
                signBtn.setVisibility(View.VISIBLE)
                loadingProgress.setVisibility(View.INVISIBLE)

            } else {
                SigninUserAccount(email, password)
            }

        }
    }

    private fun SigninUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    loadingProgress.setVisibility(View.INVISIBLE);
                    signBtn.setVisibility(View.VISIBLE)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Get user information
            val userName = user.displayName
            val userEmail = user.email

            // Pass user information to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USERNAME", userName)
            intent.putExtra("EMAIL", userEmail)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // This ensures the user cannot go back to the SignUp activity using the back button
        } else {
            // Handle the case where the user is not signed in
            // You can add additional logic here if needed
        }
    }

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}