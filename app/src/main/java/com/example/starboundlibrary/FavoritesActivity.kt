package com.example.starboundlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.starboundlibrary.adapters.AdapterPdfFavorites
import com.example.starboundlibrary.databinding.ActivityFavoritesBinding
import com.example.starboundlibrary.models.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var booksArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavorites: AdapterPdfFavorites
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        loadFavoritesBook()
    }

    private fun loadFavoritesBook() {
        booksArrayList = ArrayList()
        val firebaseAuth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    booksArrayList.clear()
                    for(ds in snapshot.children){
                        val bookId = "${ds.child("bookId").value}"

                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        booksArrayList.add(modelPdf)
                    }
                    adapterPdfFavorites = AdapterPdfFavorites(this@FavoritesActivity, booksArrayList)
                    binding.favoritesRv.adapter = adapterPdfFavorites
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
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