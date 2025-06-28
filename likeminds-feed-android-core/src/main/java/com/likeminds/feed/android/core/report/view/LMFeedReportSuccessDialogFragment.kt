package com.likeminds.feed.android.core.report.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedDialogFragmentReportSuccessBinding
import com.likeminds.feed.android.core.ui.widgets.alertdialog.view.LMFeedAlertDialogView
import com.likeminds.feed.android.core.utils.LMFeedNavigationFragments
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

open class LMFeedReportSuccessDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "LMFeedReportSuccessDialog"
        private const val LM_FEED_REPORT_ENTITY_TYPE = "LM_FEED_REPORT_ENTITY_TYPE"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            type: String
        ) {
            LMFeedNavigationFragments.getInstance().getReportSuccessDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(LM_FEED_REPORT_ENTITY_TYPE, type)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedDialogFragmentReportSuccessBinding
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            type = it.getString(LM_FEED_REPORT_ENTITY_TYPE) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedDialogFragmentReportSuccessBinding.inflate(layoutInflater)

        customizeReportSuccessDialog(binding.alertDialogReport)
        return binding.root
    }

    //customizes the report success dialog
    protected open fun customizeReportSuccessDialog(alertDialogReport: LMFeedAlertDialogView) {
        val reportSuccessDialogFragmentStyle =
            LMFeedStyleTransformer.reportFragmentViewStyle.reportSuccessDialogFragmentStyle

        alertDialogReport.setStyle(reportSuccessDialogFragmentStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    //set title and sub title as per [type] received in constructor
    private fun initUI() {
        binding.alertDialogReport.apply {
            setPositiveButtonEnabled(true)
            setAlertTitle(getString(R.string.lm_feed_s_is_reported_for_review, type))
            setAlertSubtitle(
                getString(
                    R.string.lm_feed_our_team_will_look_into_your_feedback_and_will_take_appropriate_action_on_this_s,
                    type.pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                )
            )
            setAlertPositiveButtonText(getString(R.string.lm_feed_okay))
            setPositiveButtonClickListener {
                dismiss()
            }
        }
    }
}