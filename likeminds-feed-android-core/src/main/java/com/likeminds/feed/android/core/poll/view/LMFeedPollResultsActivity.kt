package com.likeminds.feed.android.core.poll.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityPollResultsBinding
import com.likeminds.feed.android.core.poll.model.LMFeedPollResultsExtras
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