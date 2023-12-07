package com.example.starboundlibrary.utils

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.starboundlibrary.utils.Constants
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.storage.FirebaseStorage
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


    }

}
