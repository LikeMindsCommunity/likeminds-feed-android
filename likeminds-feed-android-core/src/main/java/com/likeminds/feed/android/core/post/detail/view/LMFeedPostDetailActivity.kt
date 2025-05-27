package com.likeminds.feed.android.core.post.detail.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedActivityPostDetailBinding
import com.likeminds.feed.android.core.post.detail.model.LMFeedPostDetailExtras
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.emptyExtrasException

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

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clPostDetail) { view, windowInsets ->
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

        //inflates post detail fragment
        inflatePostDetailFragment()
    }

    @SuppressLint("InlinedApi")
    @Suppress("Deprecation")
    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(this, R.color.lm_feed_white)
        } else {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = true

            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.lm_feed_white))
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.statusBars())
        }
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