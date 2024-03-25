package com.likeminds.feed.android.core.activityfeed.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.databinding.LmFeedActivityActivityFeedBinding

class LMFeedActivityFeedActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityActivityFeedBinding

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, LMFeedActivityFeedActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, LMFeedActivityFeedActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inflates activity feed fragment
        inflateActivityFeedFragment()
    }

    private fun inflateActivityFeedFragment() {
        //gets activity feed fragment instance
        val activityFeedFragment =
            LMFeedActivityFeedFragment.getInstance()

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerActivityFeed.id,
                activityFeedFragment,
                LMFeedActivityFeedFragment.TAG
            )
            .commit()
    }
}