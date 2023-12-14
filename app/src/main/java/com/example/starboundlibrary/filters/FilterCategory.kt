package com.example.starboundlibrary.filters

import android.annotation.SuppressLint
import android.widget.Filter
import com.example.starboundlibrary.adapters.AdapterCategory
import com.example.starboundlibrary.models.ModelCategory

class FilterCategory: Filter {
    //arraylist to search
    private var filterList: ArrayList<ModelCategory>

    // adapter in which filter need to be implemented
    private var adapterCategory: AdapterCategory

    // constructor
    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        // should not be null and empty
        if(constraint!= null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filteredModel:ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size){
                // validate
                if (filterList[i].category.uppercase().contains(constraint)){
                    filteredModel.add(filterList[i])
                }
            }
            results.count = filteredModel.size
            results.values = filteredModel
        } else {
            // if it is either null or empty
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        // notify changes
        adapterCategory.notifyDataSetChanged()
    }
}
