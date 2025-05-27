package com.likeminds.feed.android.core.search.view

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

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clSearch) { view, windowInsets ->
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

        //inflates search feed fragment
        inflateSearchFeedFragment()
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
