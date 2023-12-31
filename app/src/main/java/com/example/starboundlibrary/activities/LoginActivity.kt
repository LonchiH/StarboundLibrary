package com.example.starboundlibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.starboundlibrary.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {
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
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }

        userEmail = binding.logEmail
        userPassword = binding.logPassword
        signBtn = binding.signBtn
        loadingProgress = binding.progressBar

        loadingProgress.visibility = View.INVISIBLE

        signBtn.setOnClickListener {

            signBtn.visibility = View.INVISIBLE
            loadingProgress.visibility = View.VISIBLE

            validateData()

        }
    }

    private var email = ""
    private var password = ""
    private fun validateData() {
        email = binding.logEmail.editText?.text.toString().trim()
        password = binding.logPassword.editText?.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toasty.normal(this, "Enter your Email...").show()
            signBtn.visibility = View.VISIBLE
            loadingProgress.visibility = View.INVISIBLE
        } else if(password.isEmpty()) {
            Toasty.normal(this, "Enter your Password...").show()
            signBtn.visibility = View.VISIBLE
            loadingProgress.visibility = View.INVISIBLE
        } else {
            signInUserAccount(email, password)
        }
    }

    private fun signInUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener {e ->
                Toasty.normal(this, "Login failed due to ${e.message}", Toast.LENGTH_LONG).show()
                loadingProgress.visibility = View.INVISIBLE
                signBtn.visibility = View.VISIBLE
            }
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("userType").value
                    val userName = snapshot.child("name").value as? String
                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java).putExtra("userName", userName))
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}