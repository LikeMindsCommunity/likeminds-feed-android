package com.likeminds.feed.android.core.activityfeed.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.clActivityFeed) { view, windowInsets ->
            val innerPadding = windowInsets.getInsets(
                // Notice we're using systemBars, not statusBar
                WindowInsetsCompat.Type.systemBars()
                        // Notice we're also accounting for the display cutouts
                        or WindowInsetsCompat.Type.displayCutout()
            )
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            view.setPadding(0, innerPadding.top, 0, innerPadding.bottom)

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        //inflates activity feed fragment
        inflateActivityFeedFragment()
    }

    private fun inflateActivityFeedFragment() {
        //gets activity feed fragment instance
        val activityFeedFragment = LMFeedActivityFeedFragment.getInstance()

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