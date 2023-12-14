package com.example.starboundlibrary.utils

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.starboundlibrary.adapters.AdapterPdfUser
import com.example.starboundlibrary.databinding.FragmentBookUserBinding
import com.example.starboundlibrary.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class BookUserFragment : Fragment {

    private lateinit var binding: FragmentBookUserBinding

    public companion object {
        // receive data from activity to load books
        public fun newInstance(categoryId: String, category: String, uid: String): BookUserFragment {
            val fragment = BookUserFragment()

            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get args that we passed in newInstance method
        val args = arguments
        if(args != null){
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookUserBinding.inflate(LayoutInflater.from(context), container, false)

        // load pdf according to category
        if (category == "All") {
            // load all
            loadAllBooks()
        } else if (category == "Most Viewed") {
            // load most viewed
            loadMostViewedDownloadedBooks("viewsCount")
        } else if (category == "Most Downloaded") {
            // load most viewed
            loadMostViewedDownloadedBooks("downloadsCount")
        } else {
            // load selected category
            loadCategorizedBooks()
        }
        // search
        binding.searchEt.addTextChangedListener { object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterPdfUser.filter.filter(s)
                } catch (e: Exception) {

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        } }
        return binding.root
    }

    private fun loadAllBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelPdf::class.java)
                    // add
                    pdfArrayList.add(model!!)
                }
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy).limitToLast(10)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelPdf::class.java)
                    // add
                    pdfArrayList.add(model!!)
                }
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadCategorizedBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(ModelPdf::class.java)
                        // add
                        pdfArrayList.add(model!!)
                    }
                    adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}