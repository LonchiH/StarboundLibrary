package com.example.starboundlibrary

import android.app.Application
import android.icu.text.CaseMap.Title
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
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

//        fun loadPdfSize(pdfUrl: String?, pdfTitle: String){
//            val TAG = "PDF_SIZE_TAG"
//
//            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
//            ref.metadata
//                .addOnSuccessListener {
//
//                }
//                .addOnFailureListener {
//
//                }
//        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ){
            val TAG = "PDF_THUMBNAIL_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError {t ->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onPageError { page, t ->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onLoad {nbPages ->
                            progressBar.visibility = View.INVISIBLE

                            if (pagesTv != null){
                                pagesTv.text = "$nbPages"
                            }
                        }
                }
                .addOnFailureListener {

                }
        }

    }

    fun loadCategory() {

    }
}
