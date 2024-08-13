package com.likeminds.feed.android.core.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.ui.widgets.snackbar.view.LMFeedSnackbar
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.base.model.*

//view related utils class
object LMFeedViewUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun ImageView.setTint(@ColorRes tint: Int) {
        ImageViewCompat.setImageTintList(
            this,
            ColorStateList.valueOf(ContextCompat.getColor(context, tint))
        )
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    // returns shimmer drawable
    fun getShimmer(): ShimmerDrawable {
        val shimmer =
            Shimmer.AlphaHighlightBuilder() // The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1800) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.85f) //the alpha of the underlying children
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build()

        // This is the placeholder for the imageView
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
        return shimmerDrawable
    }

    fun showShortToast(context: Context?, text: String?) {
        if (context == null || text.isNullOrEmpty()) return
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    // shows short length snackbar
    fun showShortSnack(view: View, text: String?, anchorView: View? = null) {
        if (text.isNullOrEmpty()) {
            return
        }
        val snackBar = LMFeedSnackbar.make(view, text)
        anchorView?.let {
            snackBar.setAnchorView(anchorView)
        }
        snackBar.show()
    }

    // shows short toast with "Something went wrong!" message
    fun showSomethingWentWrongToast(context: Context) {
        showShortToast(context, context.getString(R.string.lm_feed_something_went_wrong))
    }

    // shows short toast with error message
    fun showErrorMessageToast(context: Context, errorMessage: String?) {
        showShortToast(
            context,
            errorMessage ?: context.getString(R.string.lm_feed_something_went_wrong)
        )
    }

    // shoes bounce animation on the provided view
    fun showBounceAnim(context: Context, view: View) {
        // bounce animation for save button
        val saveBounceAnim: Animation by lazy {
            AnimationUtils.loadAnimation(
                context,
                R.anim.lm_feed_bounce
            )
        }

        saveBounceAnim.interpolator = LMFeedBounceInterpolator(0.2, 20.0)
        view.startAnimation(saveBounceAnim)
    }

    // returns type of post for analytics from the viewType of post
    fun getPostTypeFromViewType(postViewType: Int?): String {
        return when (postViewType) {
            ITEM_POST_TEXT_ONLY -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_TEXT
            }

            ITEM_POST_SINGLE_IMAGE -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_IMAGE
            }

            ITEM_POST_SINGLE_VIDEO -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_VIDEO
            }

            ITEM_POST_DOCUMENTS -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_DOCUMENT
            }

            ITEM_POST_MULTIPLE_MEDIA -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_IMAGE_VIDEO
            }

            ITEM_POST_LINK -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_LINK
            }

            else -> {
                LMFeedAnalytics.LMFeedKeys.POST_TYPE_TEXT
            }
        }
    }

    //closes the keyboard if it is open
    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //find parent for a particular view
    fun View?.findSuitableParent(): ViewGroup? {
        var view = this
        var fallback: ViewGroup? = null
        do {
            if (view is CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return view
            } else if (view is FrameLayout) {
                if (view.id == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return view
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = view
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        } while (view != null)

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback
    }

    //returns the width and height of the device
    fun getDeviceDimension(context: Context): Pair<Int, Int> {
        val activity = (context as Activity)
        val width: Int
        val height: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }
        return Pair(width, height)
    }
}