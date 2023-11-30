package com.example.starboundlibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.starboundlibrary.databinding.RowPdfAdminBinding

class AdapterPdfAdmin: RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable {

    private var context: Context
    public var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var binding: RowPdfAdminBinding
    private val filterList: ArrayList<ModelPdf>

    var filter: FilterPdfAdmin? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }


    inner class HolderPdfAdmin(itemView: View): RecyclerView.ViewHolder(itemView){
        val pdf = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.bookTitle
        val more = binding.moreBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size //items count
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        holder.titleTv.text = title

        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdf, holder.progressBar, pagesTv = null)

    }

    override fun getFilter(): Filter {
        if ( filter == null) {
            filter = FilterPdfAdmin(filterList, this)
        }
        return filter as FilterPdfAdmin
    }

}