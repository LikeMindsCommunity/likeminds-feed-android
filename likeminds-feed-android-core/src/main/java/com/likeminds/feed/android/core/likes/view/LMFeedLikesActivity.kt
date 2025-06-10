package com.likeminds.feed.android.core.likes.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityLikesBinding
import com.likeminds.feed.android.core.likes.model.LMFeedLikesScreenExtras
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity
import com.likeminds.feed.android.core.utils.*

class LMFeedLikesActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityLikesBinding

    private lateinit var likesScreenExtras: LMFeedLikesScreenExtras

    companion object {
        const val LM_FEED_LIKES_SCREEN_EXTRAS = "LM_FEED_LIKES_SCREEN_EXTRAS"
        private const val LM_FEED_LIKES_SCREEN_BUNDLE = "lm_feed_bundle"
        const val TAG = "LMFeedLikesActivity"

        @JvmStatic
        fun start(context: Context, extras: LMFeedLikesScreenExtras) {
            val intent = Intent(context, LMFeedLikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra(LM_FEED_LIKES_SCREEN_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedLikesScreenExtras): Intent {
            val intent = Intent(context, LMFeedLikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra(LM_FEED_LIKES_SCREEN_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityLikesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.clLikes) { view, windowInsets ->
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

        //inflates likes fragment
        inflateLikesFragment()
    }

    private fun assignExtras() {
        //get bundle
        val bundle = intent.getBundleExtra(LM_FEED_LIKES_SCREEN_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            likesScreenExtras = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_LIKES_SCREEN_EXTRAS,
                LMFeedLikesScreenExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        } else {
            redirectActivity()
        }
    }

    private fun inflateLikesFragment() {
        //gets likes fragment instance
        val likesFragment =
            LMFeedLikesFragment.getInstance(likesScreenExtras = likesScreenExtras)

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerLikes.id,
                likesFragment,
                LMFeedPostDetailActivity.TAG
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