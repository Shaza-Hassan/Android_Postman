package com.shaza.androidpostman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shaza.androidpostman.home.view.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, HomeFragment.newInstance())
                .commit()
        }
    }
}