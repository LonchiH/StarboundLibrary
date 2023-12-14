package com.example.starboundlibrary.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.starboundlibrary.activities.PdfDetailActivity
import com.example.starboundlibrary.databinding.RowPdfFavoritesBinding
import com.example.starboundlibrary.models.ModelPdf
import com.example.starboundlibrary.utils.MyApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterPdfFavorites: RecyclerView.Adapter<AdapterPdfFavorites.HolderPdfFavorites> {
    private val context: Context
    private var booksArrayList = ArrayList<ModelPdf>()

    private lateinit var binding: RowPdfFavoritesBinding

    constructor(context: Context, booksArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.booksArrayList = booksArrayList
    }

    inner class HolderPdfFavorites(itemView: View): RecyclerView.ViewHolder(itemView){
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfFavorites {
        binding = RowPdfFavoritesBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfFavorites((binding.root))
    }

    override fun getItemCount(): Int {
        return booksArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfFavorites, position: Int) {
        val model = booksArrayList[position]
        loadBookDetails(model, holder)

        // handle click
        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }

        binding.removeFavorite.setOnClickListener{
            MyApplication.removeFromFavorite(context, model.id)
        }
    }

    private fun loadBookDetails(model: ModelPdf, holder: AdapterPdfFavorites.HolderPdfFavorites) {
        val bookId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val title = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val url = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    model.isFavorite = true
                    model.timestamp = timestamp.toLong()
                    model.title = title
                    model.description = description
                    model.categoryId = categoryId
                    model.uid = uid
                    model.url = url
                    model.viewsCount = viewsCount.toLong()
                    model.downloadsCount = downloadsCount.toLong()

                    // format date
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    // load pdf
                    MyApplication.loadPdfFromUrlSinglePage("$url", "$title", binding.pdfView, binding.progressBar, null)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}