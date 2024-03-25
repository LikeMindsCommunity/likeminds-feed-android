package com.likeminds.feed.android.core.topicselection.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityTopicSelectionBinding
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionExtras
import com.likeminds.feed.android.core.utils.*

class LMFeedTopicSelectionActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityTopicSelectionBinding

    private lateinit var topicSelectionExtras: LMFeedTopicSelectionExtras

    companion object {
        const val TAG = "LMFeedTopicSelectionActivity"
        const val LM_FEED_TOPIC_SELECTION_EXTRAS = "LM_FEED_TOPIC_SELECTION_EXTRAS"
        private const val LM_FEED_TOPIC_SELECTION_BUNDLE = "lm_feed_bundle"
        const val LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS = "LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: LMFeedTopicSelectionExtras) {
            val intent = Intent(context, LMFeedTopicSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_TOPIC_SELECTION_EXTRAS, extras)
            intent.putExtra(LM_FEED_TOPIC_SELECTION_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedTopicSelectionExtras): Intent {
            val intent = Intent(context, LMFeedTopicSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_TOPIC_SELECTION_EXTRAS, extras)
            intent.putExtra(LM_FEED_TOPIC_SELECTION_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityTopicSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //parse extras
        assignExtras()

        //inflates topic selection fragment
        inflateTopicSelectionFragment()
    }

    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_TOPIC_SELECTION_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            topicSelectionExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_TOPIC_SELECTION_EXTRAS,
                LMFeedTopicSelectionExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        } else {
            redirectActivity()
        }
    }

    private fun inflateTopicSelectionFragment() {
        //gets topic selection fragment instance
        val topicSelectionFragment =
            LMFeedTopicSelectionFragment.getInstance(topicSelectionExtras = topicSelectionExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerTopicSelection.id,
                topicSelectionFragment,
                TAG
            )
            .commit()
    }

    private fun redirectActivity() {
        LMFeedViewUtils.showShortToast(this, getString(R.string.lm_feed_request_not_processed))
        supportFragmentManager.popBackStack()
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(R.anim.lm_feed_slide_from_left, R.anim.lm_feed_slide_to_right)
    }
}