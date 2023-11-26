package com.example.starboundlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import androidx.appcompat.app.AppCompatActivity
import com.example.starboundlibrary.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var loadingProgress: ProgressBar
    private lateinit var regBtn: Button
    private lateinit var userEmail: TextInputLayout
    private lateinit var userPassword: TextInputLayout
    private lateinit var userPassword2: TextInputLayout
    private lateinit var userFullName: TextInputLayout
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
        userFullName = binding.regFullName
        userName = binding.regUsername
        regBtn = binding.regBtn
        loadingProgress = binding.progressBar

        loadingProgress.visibility = View.INVISIBLE

        regBtn.setOnClickListener {

            regBtn.setVisibility(View.INVISIBLE)
            loadingProgress.setVisibility(View.VISIBLE)
            val email: String = binding.regEmail.editText?.text.toString()
            val password: String = binding.regPassword.editText?.text.toString()
            val password2: String = binding.regConfirmPassword.editText?.text.toString()
            val fullName: String = binding.regFullName.editText?.text.toString()
            val userName: String = binding.regUsername.editText?.text.toString()

            if(email.isEmpty() || userName.isEmpty() || fullName.isEmpty() || password.isEmpty()  || !password.equals(password2)) {

                // something goes wrong : all fields must be filled
                // we need to display an error message
                showToast(this,"Please Verify all fields")
                regBtn.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);

            }
            else {
                // everything is ok and all fields are filled now we can start creating user account
                // CreateUserAccount method will try to create the user if the email is valid
                CreateUserAccount(email, userName, fullName, password)
            }

        }

    }

    private fun CreateUserAccount(email: String, userName: String, fullName: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    showToast(this, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUserInfo(userName, fullName, auth.getCurrentUser())
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    showToast(this, "createUserWithEmail:failure" + task.exception)
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    updateUI(null)
                }
            }
    }

    private fun updateUserInfo(userName: String, userFullName: String, currentUser: FirebaseUser?) {
        val user = currentUser ?: return // Return if the user is null

        if (userName.isNotBlank()) {
            // Update user display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast(this, "User profile updated successfully")

                        // Update other user information in the Firebase Realtime Database
                        val databaseReference = FirebaseDatabase.getInstance().reference
                        val userReference = databaseReference.child("users").child(user.uid)

                        // Update user name and full name in the "users" node
                        userReference.child("userName").setValue(userName)
                        userReference.child("fullName").setValue(userFullName)

                        // Optionally, you can update other user information here
                    } else {
                        showToast(this, "Failed to update user profile")
                    }
                }
        } else {
            showToast(this, "Username is null or empty")
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