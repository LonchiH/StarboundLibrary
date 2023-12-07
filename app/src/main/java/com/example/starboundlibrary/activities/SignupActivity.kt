package com.example.starboundlibrary.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import androidx.appcompat.app.AppCompatActivity
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var loadingProgress: ProgressBar
    private lateinit var regBtn: Button
    private lateinit var userEmail: TextInputLayout
    private lateinit var userPassword: TextInputLayout
    private lateinit var userPassword2: TextInputLayout
    private lateinit var userName: TextInputLayout
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        // Initialize Firebase Auth
        auth = Firebase.auth

        userEmail = binding.regEmail
        userPassword = binding.regPassword
        userPassword2 = binding.regConfirmPassword
        userName = binding.regUsername
        regBtn = binding.regBtn
        loadingProgress = binding.progressBar

        loadingProgress.visibility = View.INVISIBLE

        regBtn.setOnClickListener {

            regBtn.setVisibility(View.INVISIBLE)
            loadingProgress.setVisibility(View.VISIBLE)

            validateData()

        }

    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.regEmail.editText?.text.toString().trim()
        name = binding.regUsername.editText?.text.toString().trim()
        password = binding.regPassword.editText?.text.toString().trim()
        val password2: String = binding.regConfirmPassword.editText?.text.toString().trim()

        if (name.isEmpty()) {
            showToast(this, "Enter your name...")
            regBtn.setVisibility(View.VISIBLE)
            loadingProgress.setVisibility(View.INVISIBLE)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email add pattern
            showToast(this, "Invalid Email Pattern...")
            regBtn.setVisibility(View.VISIBLE)
            loadingProgress.setVisibility(View.INVISIBLE)
        } else if (password.isEmpty()) {
            showToast(this, "Enter password...")
            regBtn.setVisibility(View.VISIBLE)
            loadingProgress.setVisibility(View.INVISIBLE)
        } else if (password2.isEmpty()) {
            showToast(this, "Confirm password...")
            regBtn.setVisibility(View.VISIBLE)
            loadingProgress.setVisibility(View.INVISIBLE)
        } else if (password != password2) {
            showToast(this, "Password doesn't match...")
            regBtn.setVisibility(View.VISIBLE)
            loadingProgress.setVisibility(View.INVISIBLE)
        } else  {
            CreateUserAccount(email, name, password)
        }
    }

    private fun CreateUserAccount(email: String, name: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast(this, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUserInfo(name, auth.getCurrentUser())
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    showToast(this, "Failed to create account")
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
            }
    }

    private fun updateUserInfo(userName: String, currentUser: FirebaseUser?) {

        val timestamp = System.currentTimeMillis()
        val uid = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" // add on profile edit
        hashMap["userType"] = "user" // admin will be manually edited on firebase db
        hashMap["timestamp"] = timestamp


        // set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                showToast(this, "Account created...")
                startActivity(Intent(this@SignupActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener {e ->
                showToast(this, "Failed saving user due to ${e.message}")
            }

    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

//    public override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            reload()
//        }
//    }

    private fun reload() {
        TODO("Not yet implemented")
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.regToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        binding.regToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}