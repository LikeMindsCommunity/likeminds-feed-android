package com.likeminds.feed.android.core.post.edit.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityEditPostBinding
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.utils.*

class LMFeedEditPostActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityEditPostBinding

    private lateinit var editPostExtras: LMFeedEditPostExtras

    companion object {
        const val LM_FEED_EDIT_POST_EXTRAS = "LM_FEED_EDIT_POST_EXTRAS"
        const val LM_FEED_EDIT_POST_BUNDLE = "lm_feed_edit_post_bundle"
        const val TAG = "LMFeedEditPostActivity"

        @JvmStatic
        fun start(context: Context, extras: LMFeedEditPostExtras) {
            val intent = Intent(context, LMFeedEditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_EDIT_POST_EXTRAS, extras)
            intent.putExtra(LM_FEED_EDIT_POST_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedEditPostExtras): Intent {
            val intent = Intent(context, LMFeedEditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_EDIT_POST_EXTRAS, extras)
            intent.putExtra(LM_FEED_EDIT_POST_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.clEditPost) { view, windowInsets ->
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

        //inflates edit post fragment
        inflateEditPostFragment()
    }

    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_EDIT_POST_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            editPostExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_EDIT_POST_EXTRAS,
                LMFeedEditPostExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        } else {
            redirectActivity()
        }

    }

    private fun inflateEditPostFragment() {
        //gets post detail fragment instance
        val editPostFragment =
            LMFeedEditPostFragment.getInstance(editPostExtras = editPostExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerEditPost.id,
                editPostFragment,
                TAG
            )
            .commit()
    }

    private fun redirectActivity() {
        LMFeedViewUtils.showSomethingWentWrongToast(this)
        supportFragmentManager.popBackStack()
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(R.anim.lm_feed_slide_from_left, R.anim.lm_feed_slide_to_right)
    }
}