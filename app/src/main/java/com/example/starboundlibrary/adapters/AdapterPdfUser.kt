package com.example.starboundlibrary.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.starboundlibrary.activities.PdfDetailActivity
import com.example.starboundlibrary.models.ModelPdf
import com.example.starboundlibrary.databinding.RowPdfUserBinding
import com.example.starboundlibrary.filters.FilterPdfUser
import com.example.starboundlibrary.utils.MyApplication

class AdapterPdfUser: RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>, Filterable{

    private var context: Context

    var pdfArrayList: ArrayList<ModelPdf>
    var filterList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfUserBinding

    private var filter: FilterPdfUser? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfUser(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {

        val model = pdfArrayList[position]
        val bookId = model.id
        val title = model.title
        val description = model.description
        val uid = model.uid
        val url = model.url
        val timestamp = model.timestamp

        // convert time
        val date = MyApplication.formatTimeStamp(timestamp)

        // set data
        holder.titleTb.text = title

        MyApplication.loadPdfFromUrlSinglePage(
            url,
            title,
            holder.pdfView,
            holder.progressBar,
            null
        )

        binding.pdfView.setOnClickListener(){
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", bookId)
            context.startActivity(intent)
        }

    }

    inner class HolderPdfUser(itemView: View): RecyclerView.ViewHolder(itemView){
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var titleTb = binding.bookTitle
    }

    override fun getFilter(): Filter {
        if(filter ==null) {
            filter = FilterPdfUser(filterList, this)
        }
        return filter as FilterPdfUser
    }
}