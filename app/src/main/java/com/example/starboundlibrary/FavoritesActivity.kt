package com.example.starboundlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
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