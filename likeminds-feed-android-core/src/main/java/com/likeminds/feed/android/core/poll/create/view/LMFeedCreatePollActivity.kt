package com.likeminds.feed.android.core.poll.create.view

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
import com.likeminds.feed.android.core.databinding.LmFeedActivityPollResultsBinding
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil

open class LMFeedCreatePollActivity : AppCompatActivity() {

    private lateinit var binding: LmFeedActivityPollResultsBinding
    private var poll: LMFeedPollViewData? = null

    companion object {
        const val TAG = "LMFeedCreatePollActivity"
        const val LM_FEED_CREATE_POLL_BUNDLE = "LM_FEED_CREATE_POLL_BUNDLE"
        const val LM_FEED_CREATE_POLL_EXTRAS = "LM_FEED_CREATE_POLL_EXTRAS"
        const val LM_FEED_CREATE_POLL_RESULT = "LM_FEED_CREATE_POLL_RESULT"

        @JvmStatic
        fun start(context: Context, poll: LMFeedPollViewData? = null) {
            val intent = Intent(context, LMFeedCreatePollActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_CREATE_POLL_EXTRAS, poll)
            intent.putExtra(LM_FEED_CREATE_POLL_BUNDLE, bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, poll: LMFeedPollViewData? = null): Intent {
            val intent = Intent(context, LMFeedCreatePollActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_CREATE_POLL_EXTRAS, poll)
            intent.putExtra(LM_FEED_CREATE_POLL_BUNDLE, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //assign binding
        binding = LmFeedActivityPollResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        ViewCompat.setOnApplyWindowInsetsListener(binding.clCreatePoll) { view, windowInsets ->
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

        //inflate fragment
        inflateCreatePollFragment()
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
        val bundle = intent.getBundleExtra(LM_FEED_CREATE_POLL_BUNDLE)

        //assign to global variable
        if (bundle != null) {
            poll = LMFeedExtrasUtil.getParcelable(
                bundle,
                LM_FEED_CREATE_POLL_EXTRAS,
                LMFeedPollViewData::class.java
            )
        } else {
            poll = null
        }
    }

    private fun inflateCreatePollFragment(){
        val createPollFragment = LMFeedCreatePollFragment.getInstance(poll)

        supportFragmentManager.beginTransaction()
            .replace(binding.containerPollResults.id, createPollFragment)
            .commit()
    }
}