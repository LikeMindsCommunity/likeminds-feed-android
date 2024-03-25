package com.likeminds.feed.android.core.ui.widgets.headerview.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedHeaderViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.widgets.headerview.style.LMFeedHeaderViewStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener
import com.likeminds.feed.android.core.utils.user.LMFeedUserImageUtil
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData

class LMFeedHeaderView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private lateinit var style: LMFeedHeaderViewStyle

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding: LmFeedHeaderViewBinding =
        LmFeedHeaderViewBinding.inflate(inflater, this, true)

    /**
     * sets [LMFeedHeaderViewStyle] to [LMFeedHeaderView]
     */
    fun setStyle(headerViewStyle: LMFeedHeaderViewStyle) {

        headerViewStyle.apply {
            style = this

            //sets background color
            setBackgroundColor(ContextCompat.getColor(context, backgroundColor))

            //sets the elevation of the header view
            this@LMFeedHeaderView.elevation = resources.getDimension(elevation)

            //configure header view elements
            configureTitle(titleTextStyle)
            configureSubtitle(subtitleTextStyle)
            configureNavigationIcon(navigationIconStyle)
            configureSearchIcon(searchIconStyle)
            configureSubmitText(submitTextStyle)
            configureUserProfile(userProfileStyle)
            configureNotificationIcon(notificationIconStyle)
            configureNotificationCountText(notificationCountTextStyle)
        }
    }

    private fun configureTitle(titleTextStyle: LMFeedTextStyle) {
        binding.tvHeaderTitle.setStyle(titleTextStyle)
    }

    private fun configureSubtitle(subtitleTextStyle: LMFeedTextStyle?) {
        binding.tvHeaderSubtitle.apply {
            if (subtitleTextStyle == null) {
                hide()
            } else {
                setStyle(subtitleTextStyle)
            }
        }
    }

    private fun configureNavigationIcon(navigationIconStyle: LMFeedIconStyle?) {
        binding.ivHeaderNavigation.apply {
            if (navigationIconStyle == null) {
                hide()
            } else {
                setStyle(navigationIconStyle)
                show()
            }
        }
    }

    private fun configureSearchIcon(searchIconStyle: LMFeedIconStyle?) {
        binding.ivHeaderSearch.apply {
            if (searchIconStyle == null) {
                hide()
            } else {
                setStyle(searchIconStyle)
                show()
            }
        }
    }

    private fun configureSubmitText(submitTextStyle: LMFeedTextStyle?) {
        binding.tvHeaderSubmit.apply {
            if (submitTextStyle == null) {
                hide()
            } else {
                setStyle(submitTextStyle)
                show()
            }
        }
    }

    private fun configureUserProfile(userProfileStyle: LMFeedImageStyle?) {
        binding.ivUserProfile.apply {
            if (userProfileStyle == null) {
                hide()
            } else {
                setStyle(userProfileStyle)
            }
        }
    }

    private fun configureNotificationIcon(notificationIconStyle: LMFeedIconStyle?) {
        binding.ivHeaderNotification.apply {
            if (notificationIconStyle == null) {
                hide()
            } else {
                show()
                setStyle(notificationIconStyle)
            }
        }
    }

    private fun configureNotificationCountText(notificationCountTextStyle: LMFeedTextStyle?) {
        binding.tvHeaderNotificationCount.apply {
            if (notificationCountTextStyle == null) {
                hide()
            } else {
                setStyle(notificationCountTextStyle)
            }
        }
    }

    /**
     * Sets title text in the header view.
     *
     * @param title Text for the title in the header.
     */
    fun setTitleText(title: String) {
        binding.tvHeaderTitle.text = title
    }

    /**
     * Sets subtitle text in the header view.
     *
     * @param subtitle Text for the subtitle in the header.
     */
    fun setSubTitleText(subtitle: String) {
        binding.tvHeaderSubtitle.apply {
            binding.tvHeaderSubtitle.text = subtitle
            show()
        }
    }

    /**
     * Sets the navigation icon click listener
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setNavigationIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivHeaderNavigation.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets submit text in the header view.
     *
     * @param submitText Text for the submit text in the header.
     */
    fun setSubmitText(submitText: String) {
        binding.tvHeaderSubmit.text = submitText
    }

    /**
     * Sets the submit button style as per its configuration (disabled/enabled)
     *
     * @param isEnabled - whether to enable/disable the submit button
     * @param showProgress - whether to show progress bar in plae of the submit button
     */
    fun setSubmitButtonEnabled(isEnabled: Boolean, showProgress: Boolean = false) {
        binding.apply {
            if (showProgress) {
                pbSubmit.show()
                tvHeaderSubmit.hide()
            } else {
                pbSubmit.hide()
                tvHeaderSubmit.show()
                if (isEnabled) {
                    tvHeaderSubmit.isEnabled = true
                    style.activeSubmitColor?.let {
                        tvHeaderSubmit.setTextColor(ContextCompat.getColor(context, it))
                    }
                } else {
                    tvHeaderSubmit.isEnabled = false
                    style.submitTextStyle?.textColor?.let {
                        tvHeaderSubmit.setTextColor(ContextCompat.getColor(context, it))
                    }
                }
            }
        }
    }

    fun setNotificationCountText(count: Int) {
        binding.apply {
            ivHeaderNotification.show()
            tvHeaderNotificationCount.show()
            when (count) {
                0 -> {
                    tvHeaderNotificationCount.hide()
                }

                in 1..99 -> {
                    configureNotificationBadge(count.toString())
                }

                else -> {
                    configureNotificationBadge(context.getString(R.string.lm_feed_nine_nine_plus))
                }
            }
        }
    }

    fun setNotificationIconVisibility(isVisible: Boolean) {
        binding.ivHeaderNotification.isVisible = isVisible
        binding.tvHeaderNotificationCount.isVisible = isVisible
    }

    /**
     * Configure the notification badge based on the text length and visibility
     * @param text Text to show on the counter, eg - 99+, 8, etc
     */
    private fun configureNotificationBadge(text: String) {
        binding.tvHeaderNotificationCount.apply {
            if (text.length > 2) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 7f)
            } else {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            }
            this.text = text
            visibility = View.VISIBLE
        }
    }

    /**
     * Sets user profile in image view.
     *
     * @param user - data of the user.
     */
    fun setUserProfileImage(user: LMFeedUserViewData) {
        var userProfileViewStyle = style.userProfileStyle ?: return

        if (userProfileViewStyle.placeholderSrc == null) {
            userProfileViewStyle = userProfileViewStyle.toBuilder().placeholderSrc(
                LMFeedUserImageUtil.getNameDrawable(
                    user.sdkClientInfoViewData.uuid,
                    user.name,
                    userProfileViewStyle.isCircle,
                ).first
            ).build()
        }

        binding.ivUserProfile.apply {
            show()
            setImage(user.imageUrl, userProfileViewStyle)
        }
    }

    /**
     * Sets the search icon click listener
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setSearchIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivHeaderSearch.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets the submit button click listener
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setSubmitButtonClickListener(listener: LMFeedOnClickListener) {
        binding.tvHeaderSubmit.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets the click listener on user profile view in the header view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setUserProfileClickListener(listener: LMFeedOnClickListener) {
        binding.ivUserProfile.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets the click listener on the notification icon in the header view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setNotificationIconClickListener(listener: LMFeedOnClickListener) {
        binding.ivHeaderNotification.setOnClickListener {
            listener.onClick()
        }
    }
}