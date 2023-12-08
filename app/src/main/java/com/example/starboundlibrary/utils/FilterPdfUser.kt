package com.example.starboundlibrary.utils

import android.widget.Filter
import com.example.starboundlibrary.adapters.AdapterPdfUser
import com.example.starboundlibrary.models.ModelPdf


class FilterPdfUser: Filter {
    // arraylist in which we want to search
    public var filterList: ArrayList<ModelPdf>
    // adapter in which filter need to be implemented
    var adapterPdfUser: AdapterPdfUser

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint

        var results = FilterResults()

        // val to be searched

        if(constraint != null && constraint.isNotEmpty()){


            constraint = constraint.toString().uppercase()
            val filteredModels = ArrayList<ModelPdf>()
            for(i in filterList.indices){
                // validate
                if(filterList[i].title.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                } else {
                    // do nothing
                }
            }
            // return
            results.count = filteredModels.size
            results.values = filteredModels

        } else {
            // return original
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults?) {
        // apply filter
        if (results != null) {
            adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>
        }

        // notify changes
        adapterPdfUser.notifyDataSetChanged()
    }
}