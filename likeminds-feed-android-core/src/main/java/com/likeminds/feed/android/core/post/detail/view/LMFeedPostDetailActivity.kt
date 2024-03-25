package com.likeminds.feed.android.core.post.detail.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityPostDetailBinding
import com.likeminds.feed.android.core.post.detail.model.LMFeedPostDetailExtras
import com.likeminds.feed.android.core.utils.*

class LMFeedPostDetailActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityPostDetailBinding

    private lateinit var postDetailExtras: LMFeedPostDetailExtras

    companion object {
        const val LM_FEED_POST_DETAIL_EXTRAS = "LM_FEED_POST_DETAIL_EXTRAS"
        const val LM_FEED_POST_DETAIL_BUNDLE = "lm_feed_bundle"
        const val TAG = "LMFeedPostDetailActivity"

        @JvmStatic
        fun start(context: Context, extras: LMFeedPostDetailExtras) {
            val intent = Intent(context, LMFeedPostDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POST_DETAIL_EXTRAS, extras)
            intent.putExtra(LM_FEED_POST_DETAIL_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedPostDetailExtras): Intent {
            val intent = Intent(context, LMFeedPostDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_POST_DETAIL_EXTRAS, extras)
            intent.putExtra(LM_FEED_POST_DETAIL_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //parse extras
        assignExtras()

        //inflates post detail fragment
        inflatePostDetailFragment()
    }

    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_POST_DETAIL_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            postDetailExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_POST_DETAIL_EXTRAS,
                LMFeedPostDetailExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        } else {
            //close activity
            redirectActivity()
        }
    }

    private fun inflatePostDetailFragment() {
        //gets post detail fragment instance
        val postDetailFragment =
            LMFeedPostDetailFragment.getInstance(postDetailExtras = postDetailExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerPostDetail.id,
                postDetailFragment,
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