package com.example.starboundlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.databinding.ActivityPdfListAdminBinding

class PdfListAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfListAdminBinding
    private var categoryId = ""
    private var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_list_admin)

        // get from intent
        val intent = intent
        category = intent.getStringExtra("category")!!
        categoryId = intent.getStringExtra("categoryId")!!


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