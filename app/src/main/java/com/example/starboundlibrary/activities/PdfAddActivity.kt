package com.example.starboundlibrary.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.starboundlibrary.models.ModelCategory
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityPdfAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    // arraylist to hold pdf categories
    private lateinit var categorArrayList: ArrayList<ModelCategory>
    private var pdfUri: Uri? = null
    private val TAG = "PDF_ADD_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //

        // show categories on textview
        binding.categoryTv.setOnClickListener{
            categoryPickDialog()
        }

        // pick pdf intent
        binding.attachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        // start uploading
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""
    private fun validateData() {
        //get data
        title = binding.bookTitle.text.toString().trim()
        description = binding.bookDesc.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        if(title.isEmpty()){
            showToast(this, "Enter Title...")
        } else if (description.isEmpty()){
            showToast(this, "Enter Description...")
        } else if (category.isEmpty()){
            showToast(this, "Pick Category...")
        } else if (pdfUri == null){
            showToast(this, "Pick PDF...")
        }else {
            //begin upload
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        progressDialog.setMessage("Uploading Book...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                uploadPdfToDb(uploadedPdfUrl, timestamp)
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                showToast(this, "Failed to upload due to ${e.message}")
            }
    }

    private fun uploadPdfToDb(uploadedPdfUrl: String, timestamp: Long) {
        progressDialog.setMessage("Uploading pdf info...")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        // db ref
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                showToast(this, "Uploaded...")
                pdfUri = null
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                showToast(this, "Failed to upload due to ${e.message}")
            }
    }

    private fun loadPdfCategories() {
        // init
        categorArrayList = ArrayList()

        // df ref for loading categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear before adding
                categorArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    // add to array List
                    categorArrayList.add(model!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectCategoryId = ""
    private var selectCategoryTitle = ""
    private fun categoryPickDialog(){
        // get string array
        val categoriesArray = arrayOfNulls<String>(categorArrayList.size)
        for (i in categorArrayList.indices){
            categoriesArray[i] = categorArrayList[i].category
        }

        // alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which->
                //get clicked item
                selectCategoryTitle = categorArrayList[which].category
                selectCategoryId = categorArrayList[which].id
                // set category to textview
                binding.categoryTv.text = selectCategoryTitle
            }
            .show()
    }

    private fun pdfPickIntent(){
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }
    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result ->
            if(result.resultCode == RESULT_OK){
                pdfUri = result.data!!.data
            }
            else {
                showToast(this, "Cancelled")
            }
        }
    )

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.regToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        binding.regToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}