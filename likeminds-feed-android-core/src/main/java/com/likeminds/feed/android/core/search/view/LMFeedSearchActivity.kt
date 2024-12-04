package com.likeminds.feed.android.core.search.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedSearchActivityBinding
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostActivity
import com.likeminds.feed.android.core.search.model.LMFeedSearchExtras
import com.likeminds.feed.android.core.utils.*

open class LMFeedSearchActivity : AppCompatActivity() {

    lateinit var binding: LmFeedSearchActivityBinding
    private lateinit var lmFeedSearchFragmentExtras: LMFeedSearchExtras

    companion object {
        private const val TAG = "LMFeedSearchActivity"
        const val LM_FEED_SEARCH_EXTRAS = "LM_FEED_SEARCH_EXTRAS"
        private const val LM_FEED_SEARCH_BUNDLE = "LM_FEED_SEARCH_BUNDLE"

        @JvmStatic
        fun start(context: Context, extras: LMFeedSearchExtras) {
            val intent = Intent(context, LMFeedSearchActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_SEARCH_EXTRAS, extras)
            intent.putExtra(LM_FEED_SEARCH_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedSearchExtras): Intent {
            val intent = Intent(context, LMFeedSearchActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_SEARCH_EXTRAS, extras)
            intent.putExtra(LM_FEED_SEARCH_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedSearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //parse extras
        assignExtras()

        //inflates search feed fragment
        inflateSearchFeedFragment()

    }

    //check for extras and handle the flow
    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_SEARCH_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            lmFeedSearchFragmentExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_SEARCH_EXTRAS,
                LMFeedSearchExtras::class.java
            ) ?: throw emptyExtrasException(LMFeedCreatePostActivity.TAG)
        } else {
            //close activity
            redirectActivity(true)
        }
    }

    private fun redirectActivity(isError: Boolean) {
        if (isError) {
            LMFeedViewUtils.showSomethingWentWrongToast(this)
        }
        supportFragmentManager.popBackStack()
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(R.anim.lm_feed_slide_from_left, R.anim.lm_feed_slide_to_right)
    }

    protected open fun inflateSearchFeedFragment() {
        //gets feed search fragment instance
        val feedSearchFragment =
            LMFeedSearchFragment.getInstance(lmFeedSearchFragmentExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerSearch.id,
                feedSearchFragment,
                TAG
            )
            .commit()
    }

}
