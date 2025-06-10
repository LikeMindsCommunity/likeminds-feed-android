package com.likeminds.feed.android.core.poll.result.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityPollResultsBinding
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollResultsExtras
import com.likeminds.feed.android.core.utils.*

class LMFeedPollResultsActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityPollResultsBinding
    private lateinit var pollResultsExtras: LMFeedPollResultsExtras

    companion object {
        const val LM_FEED_POLL_RESULTS_EXTRAS = "LM_FEED_POLL_RESULTS_EXTRAS"
        const val LM_FEED_POLL_RESULTS_BUNDLE = "lm_feed_bundle"
        const val TAG = "LMFeedPollResultsActivity"

        @JvmStatic
        fun start(context: Context, pollResultsExtras: LMFeedPollResultsExtras) {
            val intent = Intent(context, LMFeedPollResultsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POLL_RESULTS_EXTRAS, pollResultsExtras)
            intent.putExtra(LM_FEED_POLL_RESULTS_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, pollResultsExtras: LMFeedPollResultsExtras): Intent {
            val intent = Intent(context, LMFeedPollResultsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POLL_RESULTS_EXTRAS, pollResultsExtras)
            intent.putExtra(LM_FEED_POLL_RESULTS_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityPollResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.clCreatePoll) { view, windowInsets ->
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

        //parse extras
        assignExtras()

        //inflates poll results fragment
        inflatePollResultsFragment()
    }

    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_POLL_RESULTS_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            pollResultsExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_POLL_RESULTS_EXTRAS,
                LMFeedPollResultsExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        } else {
            //close activity
            redirectActivity()
        }
    }

    private fun redirectActivity() {
        LMFeedViewUtils.showShortToast(this, getString(R.string.lm_feed_request_not_processed))
        supportFragmentManager.popBackStack()
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(R.anim.lm_feed_slide_from_left, R.anim.lm_feed_slide_to_right)
    }

    private fun inflatePollResultsFragment() {
        //gets poll results fragment instance
        val pollResultsFragment = LMFeedPollResultsFragment.getInstance(pollResultsExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerPollResults.id,
                pollResultsFragment,
                LMFeedPollResultsFragment.TAG
            )
            .commit()
    }
}