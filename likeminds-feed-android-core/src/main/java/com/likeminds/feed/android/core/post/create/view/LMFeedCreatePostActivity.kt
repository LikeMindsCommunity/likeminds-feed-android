package com.likeminds.feed.android.core.post.create.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        const val RESULT_UPLOAD_POST = Activity.RESULT_FIRST_USER + 1
        const val POST_ATTACHMENTS_LIMIT = 10

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.clCreatePost) { view, windowInsets ->
            val innerPadding = windowInsets.getInsets(
                // Notice we're using systemBars, not statusBar
                WindowInsetsCompat.Type.systemBars()
                        // Notice we're also accounting for the display cutouts
                        or WindowInsetsCompat.Type.displayCutout()
                        // If using EditText, also add
                        or WindowInsetsCompat.Type.ime()
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