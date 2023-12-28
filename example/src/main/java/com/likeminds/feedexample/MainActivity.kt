package com.likeminds.feedexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val containerViewId = R.id.frame_layout
        val fragment = MainFragment.getInstance()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.setReorderingAllowed(true)
        Log.d("PUI", "showing feed")
        transaction.commitNowAllowingStateLoss()
    }
}