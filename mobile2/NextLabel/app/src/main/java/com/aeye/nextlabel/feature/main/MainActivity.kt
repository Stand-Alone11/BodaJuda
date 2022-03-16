package com.aeye.nextlabel.feature.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aeye.nextlabel.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setHomeFragment()
    }

    fun setHomeFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.area_fragment, HomeFragment())
        fragmentTransaction.commit()
    }
}