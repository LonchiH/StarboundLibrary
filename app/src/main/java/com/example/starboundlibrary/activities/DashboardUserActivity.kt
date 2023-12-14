package com.example.starboundlibrary.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.starboundlibrary.utils.BookUserFragment
import com.example.starboundlibrary.R
import com.example.starboundlibrary.databinding.ActivityDashboardUserBinding
import com.example.starboundlibrary.models.ModelCategory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()
        checkUser()

        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            this)

        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()

                val modelAll = ModelCategory("01", "All", 1, "")
                val modelMostDownloaded = ModelCategory("01", "Most Downloaded", 1, "")
                val modelMostViewed = ModelCategory("01", "Most Viewed", 1, "")

                // add to list
                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostDownloaded)
                categoryArrayList.add(modelMostViewed)
                viewPagerAdapter.addFragment(
                    BookUserFragment.newInstance(
                        modelAll.id,
                        modelAll.category,
                        modelAll.uid
                    ), modelAll.category
                )
                viewPagerAdapter.addFragment(
                    BookUserFragment.newInstance(
                        modelMostViewed.id,
                        modelMostViewed.category,
                        modelMostViewed.uid
                    ), modelMostViewed.category
                )
                viewPagerAdapter.addFragment(
                    BookUserFragment.newInstance(
                        modelMostDownloaded.id,
                        modelMostDownloaded.category,
                        modelMostDownloaded.uid
                    ), modelMostDownloaded.category
                )

                // refresh list
                viewPagerAdapter.notifyDataSetChanged()

                // load from DB
                for(ds in snapshot.children){
                    // get data in model
                    val model = ds.getValue(ModelCategory::class.java)

                    categoryArrayList.add(model!!)
                    viewPagerAdapter.addFragment(
                        BookUserFragment.newInstance(
                            model.id,
                            model.category,
                            model.uid
                        ), model.category
                    )
                    // refresh list
                    viewPagerAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        //setup adapter
        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context): FragmentPagerAdapter (fm, behavior){
        // hold lists of fragments
        private val fragmentList: ArrayList<BookUserFragment> = ArrayList()
        // list for the category titles
        private val fragmentTitleList: ArrayList<String> = ArrayList()

        private val context: Context

        init {
            this.context = context
        }
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: BookUserFragment, title: String){
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

    private fun checkUser() {
        // Retrieve the user name from the intent
        val userName = intent.getStringExtra("userName")

        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            binding.titleTv.text = "Not Logged In"
            finish()
        } else {
            binding.titleTv.text = "Hello there, $userName"
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.regToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_logout_white)
        }

        binding.regToolbar.setNavigationOnClickListener {
            Firebase.auth.signOut()
            onBackPressedDispatcher.onBackPressed()

            val intent = Intent(this, LoginActrivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}