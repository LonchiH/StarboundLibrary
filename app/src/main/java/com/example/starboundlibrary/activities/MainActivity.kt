package com.example.starboundlibrary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_nav)/*previous layout= activity_main*/
/*hello*/
        bottomNavigationView = findViewById(R.id.bottom)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.bottom_home ->{
                    replaceFragment(Home())
                true
            }
                R.id.bottom_favorite ->{
                replaceFragment(Favorite())
                true
            }
                R.id.bottom_profile ->{
                replaceFragment(Profile())
                true
            }
                else -> false
            }

        }
        replaceFragment(Home())

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.bottomNav, fragment).commit()
    }
}