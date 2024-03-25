package com.likeminds.feed.android.core.post.create.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityCreatePostBinding
import com.likeminds.feed.android.core.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.emptyExtrasException

class LMFeedCreatePostActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityCreatePostBinding
    private lateinit var lmFeedCreatePostExtras: LMFeedCreatePostExtras

    companion object {
        const val TAG = "LMFeedCreatePostActivity"
        const val LM_FEED_CREATE_POST_EXTRAS = "LM_FEED_CREATE_POST_EXTRAS"
        const val LM_FEED_CREATE_POST_BUNDLE = "lm_feed_bundle"

        @JvmStatic
        fun start(context: Context, extras: LMFeedCreatePostExtras) {
            val intent = Intent(context, LMFeedCreatePostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_CREATE_POST_EXTRAS, extras)
            intent.putExtra(LM_FEED_CREATE_POST_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedCreatePostExtras): Intent {
            val intent = Intent(context, LMFeedCreatePostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_CREATE_POST_EXTRAS, extras)
            intent.putExtra(LM_FEED_CREATE_POST_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //assign binding
        binding = LmFeedActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //parse extras
        assignExtras()

        //inflate create post fragment
        inflateCreatePostFragment()
    }

    //check for extras and handle the flow
    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_CREATE_POST_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            lmFeedCreatePostExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_CREATE_POST_EXTRAS,
                LMFeedCreatePostExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
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

    private fun inflateCreatePostFragment() {
        val createPostFragment =
            LMFeedCreatePostFragment.getInstance(createPostExtras = lmFeedCreatePostExtras)

        supportFragmentManager.beginTransaction()
            .replace(binding.containerCreatePost.id, createPostFragment, TAG)
            .commit()
    }
}