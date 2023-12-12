package com.example.starboundlibrary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityPdfViewBinding

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
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