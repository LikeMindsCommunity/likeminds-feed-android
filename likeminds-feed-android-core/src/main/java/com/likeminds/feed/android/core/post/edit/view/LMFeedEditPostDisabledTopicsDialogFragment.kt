package com.likeminds.feed.android.core.post.edit.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedDialogFragmentEditPostDisabledTopicsBinding
import com.likeminds.feed.android.core.delete.view.LMFeedAdminDeleteDialogFragment
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostDisabledTopicsDialogExtras
import com.likeminds.feed.android.core.ui.widgets.alertdialog.view.LMFeedAlertDialogView
import com.likeminds.feed.android.core.utils.*

open class LMFeedEditPostDisabledTopicsDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "LMFeedEditPostDisabledTopicsDialogFragment"
        private const val LM_FEED_DISABLE_TOPICS_EXTRAS = "LM_FEED_DISABLE_TOPICS_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            extras: LMFeedEditPostDisabledTopicsDialogExtras
        ) {
            LMFeedAdminDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LM_FEED_DISABLE_TOPICS_EXTRAS, extras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedDialogFragmentEditPostDisabledTopicsBinding
    private lateinit var extras: LMFeedEditPostDisabledTopicsDialogExtras

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            extras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_DISABLE_TOPICS_EXTRAS,
                LMFeedEditPostDisabledTopicsDialogExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedDialogFragmentEditPostDisabledTopicsBinding.inflate(layoutInflater)

        binding.apply {
            customizeDisabledTopicsDialog(alertDialogDisabledTopics)
            return root
        }
    }

    protected open fun customizeDisabledTopicsDialog(alertDialogDisabledTopics: LMFeedAlertDialogView) {
        val disabledTopicsDialog =
            LMFeedStyleTransformer.editPostFragmentViewStyle.disabledTopicsAlertDialogStyle

        alertDialogDisabledTopics.setStyle(disabledTopicsDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initUI() {
        setDisableTopicsDialogTitle()
        setDisableTopicsDialogSubtitle()
        setPositiveButtonText()
    }

    protected open fun setDisableTopicsDialogTitle() {
        binding.alertDialogDisabledTopics.setAlertTitle(extras.title)
    }

    protected open fun setDisableTopicsDialogSubtitle() {
        binding.alertDialogDisabledTopics.setAlertSubtitle(extras.subtitle)
    }

    protected open fun setPositiveButtonText() {
        binding.alertDialogDisabledTopics.setAlertPositiveButtonText(getString(R.string.lm_feed_okay))
    }

    private fun initListeners() {
        setOnDialogPositiveButtonClickListener()
    }

    protected open fun setOnDialogPositiveButtonClickListener() {
        binding.alertDialogDisabledTopics.setPositiveButtonClickListener {
            dismiss()
        }
    }
}