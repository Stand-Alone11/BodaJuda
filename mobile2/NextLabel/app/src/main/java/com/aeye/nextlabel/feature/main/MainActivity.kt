package com.aeye.nextlabel.feature.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.aeye.nextlabel.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingHomeFragment()
    }

    override fun onResume() {
        super.onResume()
        settingButton()
        settingNavBar()
    }

    fun settingHomeFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.area_fragment, HomeFragment())
        fragmentTransaction.commit()
    }

    fun settingButton() {
        val labelingBtn = findViewById<Button>(R.id.btn_labeling)

        labelingBtn?.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.area_fragment, CameraFragment())
            fragmentTransaction.commit()
        }
    }

    fun settingNavBar() {
        val homeTxt = findViewById<TextView>(R.id.nav_home)
        val labelingTxt = findViewById<TextView>(R.id.nav_labeling)

        homeTxt?.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.area_fragment, HomeFragment())
            fragmentTransaction.commit()
        }
        labelingTxt?.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.area_fragment, CameraFragment())
            fragmentTransaction.commit()
        }
    }
}