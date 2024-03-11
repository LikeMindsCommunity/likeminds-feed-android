package com.likeminds.feed.android.core.ui.widgets.alertdialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.likeminds.feed.android.core.databinding.LmFeedAlertDialogViewBinding
import com.likeminds.feed.android.core.ui.base.styles.*
import com.likeminds.feed.android.core.ui.base.views.LMFeedEditText
import com.likeminds.feed.android.core.ui.widgets.alertdialog.style.LMFeedAlertDialogStyle
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.listeners.LMFeedOnClickListener

class LMFeedAlertDialogView : CardView {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
    }

    //todo: confirm this
    val etReason: LMFeedEditText
        get() = binding.etReason

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedAlertDialogViewBinding.inflate(inflater, this, true)

    private lateinit var style: LMFeedAlertDialogStyle

    fun setStyle(alertDialogStyle: LMFeedAlertDialogStyle) {
        alertDialogStyle.apply {
            style = this

            //sets background color of the alert box
            backgroundColor?.let {
                setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
            }

            //sets elevation to the alert box
            alertBoxElevation?.let {
                cardElevation = resources.getDimension(alertBoxElevation)
            }

            alertBoxCornerRadius?.let {
                radius = resources.getDimension(alertBoxCornerRadius)
            }

            customizeAlertTitle(alertTitleText)
            customizeAlertSubtitle(alertSubtitleText)
            customizeAlertNegativeButton(alertNegativeButtonStyle)
            customizeAlertPositiveButton(alertPositiveButtonStyle)
            customizeAlertSelector(alertSelectorStyle)
            customizeAlertInput(alertInputStyle)
        }
    }

    private fun customizeAlertTitle(alertTitleText: LMFeedTextStyle) {
        binding.tvAlertSubtitle.setStyle(alertTitleText)
    }

    private fun customizeAlertSubtitle(alertSubtitleText: LMFeedTextStyle?) {
        binding.tvAlertSubtitle.apply {
            if (alertSubtitleText == null) {
                hide()
            } else {
                setStyle(alertSubtitleText)
                show()
            }
        }
    }

    private fun customizeAlertNegativeButton(alertNegativeButtonStyle: LMFeedTextStyle?) {
        binding.tvAlertCancel.apply {
            if (alertNegativeButtonStyle == null) {
                hide()
            } else {
                setStyle(alertNegativeButtonStyle)
                show()
            }
        }
    }

    private fun customizeAlertPositiveButton(alertPositiveButtonStyle: LMFeedTextStyle?) {
        binding.tvAlertConfirm.apply {
            if (alertPositiveButtonStyle == null) {
                hide()
            } else {
                setStyle(alertPositiveButtonStyle)
                show()
            }
        }
    }

    private fun customizeAlertSelector(alertSelectorStyle: LMFeedTextStyle?) {
        binding.tvAlertSelector.apply {
            if (alertSelectorStyle == null) {
                hide()
            } else {
                setStyle(alertSelectorStyle)
                show()
            }
        }
    }

    private fun customizeAlertInput(alertInputStyle: LMFeedEditTextStyle?) {
        binding.etReason.apply {
            if (alertInputStyle == null) {
                hide()
            } else {
                setStyle(alertInputStyle)
                show()
            }
        }
    }

    /**
     * Sets title text in the alert dialog.
     *
     * @param title Text for the title in the dialog.
     */
    fun setAlertTitle(title: String) {
        binding.tvAlertTitle.text = title
    }

    /**
     * Sets subtitle text in the alert dialog.
     *
     * @param subtitle Text for the subtitle in the dialog.
     */
    fun setAlertSubtitle(subtitle: String) {
        binding.tvAlertSubtitle.text = subtitle
    }

    /**
     * Sets selector text in the alert dialog.
     *
     * @param selector Text for the selector in the dialog.
     */
    fun setAlertSelectorText(selector: String) {
        binding.tvAlertSelector.text = selector
    }

    /**
     * Sets alert input reason visibility
     *
     * @param isVisible whether to make the input reason visible or hide
     */
    fun setAlertInputReasonVisibility(isVisible: Boolean) {
        binding.cvAlertInput.isVisible = isVisible
    }

    /**
     * Sets click listener on the selector view
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setAlertSelectorClickListener(listener: LMFeedOnClickListener) {
        binding.cvAlertSelector.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the positive button of the alert dialog
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setPositiveButtonClickListener(listener: LMFeedOnClickListener) {
        binding.tvAlertConfirm.setOnClickListener {
            listener.onClick()
        }
    }

    /**
     * Sets click listener on the negative button of the alert dialog
     *
     * @param listener [LMFeedOnClickListener] interface to have click listener
     */
    fun setNegativeButtonClickListener(listener: LMFeedOnClickListener) {
        binding.tvAlertCancel.setOnClickListener {
            listener.onClick()
        }
    }

    fun setPositiveButtonEnabled(isEnabled: Boolean) {
        binding.tvAlertConfirm.apply {
            this.isEnabled = isEnabled
            if (isEnabled) {
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        style.alertActivePositiveButtonColor
                    )
                )
            } else {
                style.alertPositiveButtonStyle?.textColor?.let {
                    setTextColor(ContextCompat.getColor(context, it))
                }
            }
        }
    }

    fun getAlertInputReason(): String {
        return binding.etReason.text.toString()
    }
}