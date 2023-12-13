package com.example.starboundlibrary.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityPdfViewBinding
import com.example.starboundlibrary.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    var bookId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()
    }

    private fun loadBookDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // get book url
                    val pdfUrl = snapshot.child("url").value
                    val title = snapshot.child("title").value

                    binding.title.text = title.toString()

                    // load pdf using url from firebase storage
                    loadBookFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    @SuppressLint("SetTextI18n")
    private fun loadBookFromUrl(pdfUrl: String) {
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        ref.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes ->
                // load pdf
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange{page, pageCount->
                        val currentPage = page+1
                        binding.subtitle.text = "$currentPage/$pageCount"
                    }
                    .onError{t->

                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {e ->
                binding.progressBar.visibility = View.GONE
            }
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