package com.example.starboundlibrary.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.starboundlibrary.utils.Constants
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import es.dmoral.toasty.Toasty
import java.util.Calendar
import java.util.Locale

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        @SuppressLint("SetTextI18n")
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView){

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetadata->
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    val kb = bytes/1024
                    val mb = kb/1024
                    if(mb>=1){
                        sizeTv.text = "${String.format("%.2f", mb)} MB"
                    } else if (kb >= 1){
                        sizeTv.text = "${String.format("%.2f", kb)} KB"
                    } else {
                        sizeTv.text = "${String.format("%.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener {

                }
        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ) {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError { t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.e("PDF_VIEW_ERROR", "PDFView error: ${t.message}")
                        }
                        .onPageError { page, t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.e("PDF_PAGE_ERROR", "Failed to load PDF page $page: ${t.message}")
                        }
                        .onLoad { nbPages ->
                            progressBar.visibility = View.INVISIBLE

                            if (pagesTv != null) {
                                pagesTv.text = "$nbPages"
                            }
                        }
                        .load()
                }
                .addOnFailureListener { exception ->
                    progressBar.visibility = View.INVISIBLE
                    Log.e("PDF_LOAD_ERROR", "Failed to load PDF: ${exception.message}")
                }
        }
        fun loadCategory(categoryId: String, categoryTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category: String = "${snapshot.child("category").value}"

                        // set
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        fun incrementBookViewCount(bookId: String){
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var viewsCount = "${snapshot.child("viewsCount").value}"

                        if(viewsCount=="" || viewsCount=="null"){
                            viewsCount = "0"
                        }

                        val newViewsCount = viewsCount.toLong() + 1

                        val hashMap = HashMap<String, Any?>()
                        hashMap["viewsCount"] = newViewsCount

                        // set to db
                        val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                        dbRef.child(bookId)
                            .updateChildren(hashMap)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        public fun removeFromFavorite(context: Context, bookId: String){
            val firebaseAuth = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    showToast(context, "Added to Favorites")
                }
                .addOnFailureListener {e->
                    showToast(context, "${e.message}")
                }
        }

        private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            Toasty.normal(context, message, duration).show()
        }

    }

}
