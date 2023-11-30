package com.example.starboundlibrary

import android.widget.Filter


class FilterPdfAdmin: Filter {
    // alist which we want to search
    var filterList: ArrayList<ModelPdf>

    // adapter which filter need to be implemented
    var adapterPdfAdmin: AdapterPdfAdmin

    // constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfAdmin: AdapterPdfAdmin) {
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        val results = FilterResults()
        // value should not be null and empty
        if(constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().toLowerCase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){
                if (filterList[i].title.lowercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            // if null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        //apply changes
        adapterPdfAdmin.pdfArrayList = results!!.values as ArrayList<ModelPdf>
    }

}