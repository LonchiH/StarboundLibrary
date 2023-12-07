package com.example.starboundlibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityDashboardUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()
        checkUser()
    }

    private fun checkUser() {
        // Retrieve the user name from the intent
        val userName = intent.getStringExtra("userName")

        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            binding.titleTv.text = "Not Logged In"
            finish()
        } else {
            binding.titleTv.text = "Hello there, $userName"
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.regToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_logout_white)
        }

        binding.regToolbar.setNavigationOnClickListener {
            Firebase.auth.signOut()
            onBackPressedDispatcher.onBackPressed()

            val intent = Intent(this, LoginActrivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}