package com.example.starboundlibrary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        auth = Firebase.auth
        val userName = intent.getStringExtra("userName")
        binding.hello.text = "Hello There, $userName!"
    }

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