package com.likeminds.feed.android.core.report.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentReportBinding
import com.likeminds.feed.android.core.report.model.*
import com.likeminds.feed.android.core.report.view.LMFeedReportActivity.Companion.LM_FEED_REPORT_EXTRAS
import com.likeminds.feed.android.core.report.viewmodel.LMFeedReportViewModel
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.utils.*
import java.util.Locale

open class LMFeedReportFragment : Fragment() {

    private lateinit var binding: LmFeedFragmentReportBinding

    private lateinit var reportExtras: LMFeedReportExtras

    private val reportViewModel: LMFeedReportViewModel by viewModels()

    private var tagSelected: LMFeedReportTagViewData? = null
    private lateinit var reasonOrTag: String

    companion object {
        const val TAG = "ReportFragment"
        const val LM_FEED_REPORT_RESULT = "LM_FEED_REPORT_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    private fun receiveExtras() {
        reportExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_REPORT_EXTRAS,
            LMFeedReportExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentReportBinding.inflate(layoutInflater)

        binding.apply {
            customizeReportFragmentHeaderView(headerViewReport)
            customizeReportHeaderText(tvReportHeader)
            customizeReportSubHeaderText(tvReportSubHeader)
            customizeReportReasonInput(etReason)
            customizeReportButton(btnPostReport)

            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.reportFragmentViewStyle.backgroundColor
            backgroundColor?.let { color ->
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
            }

            return root
        }
    }

    protected open fun customizeReportHeaderText(tvReportHeader: LMFeedTextView) {
        tvReportHeader.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportHeaderStyle)
        }
    }

    protected open fun customizeReportSubHeaderText(tvReportSubHeader: LMFeedTextView) {
        tvReportSubHeader.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportSubHeaderStyle)
        }
    }

    protected open fun customizeReportReasonInput(etReason: LMFeedEditText) {
        etReason.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportReasonInputStyle)
        }
    }

    protected open fun customizeReportButton(btnPostReport: LMFeedButton) {
        btnPostReport.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportButtonStyle)
        }
    }

    protected open fun customizeReportFragmentHeaderView(headerViewReport: LMFeedHeaderView) {
        headerViewReport.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_report_abuse))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewAsType()
        initListeners()
        fetchData()
        observeData()
    }

    //set headers and sub header as per report type
    private fun initViewAsType() {
        binding.apply {
            when (reportExtras.entityType) {
                //todo: post as variable
                REPORT_TYPE_POST -> {
                    tvReportSubHeader.text = getString(
                        R.string.lm_feed_report_sub_header,
//                            lmFeedHelperViewModel.getPostVariable()
//                                .pluralizeOrCapitalize(WordAction.ALL_SMALL_SINGULAR)
                    )
                }

                REPORT_TYPE_COMMENT -> {
                    tvReportSubHeader.text = getString(
                        R.string.lm_feed_report_sub_header,
                        getString(R.string.lm_feed_comment).lowercase(Locale.getDefault())
                    )
                }

                REPORT_TYPE_REPLY -> {
                    tvReportSubHeader.text = getString(
                        R.string.lm_feed_report_sub_header,
                        getString(R.string.lm_feed_reply).lowercase(Locale.getDefault())
                    )
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnPostReport.setOnClickListener {
            onReportSubmitted()
        }
    }

    protected open fun onReportSubmitted() {
        //get selected tag
        // todo:
//            tagSelected = mAdapter.items()
//                .map { it as ReportTagViewData }
//                .find { it.isSelected }

        //get reason for [edittext]
        val reason = binding.etReason.text?.trim().toString()
        val isOthersSelected = tagSelected?.name?.contains("Others", true)

        //if no tag is selected
        if (tagSelected == null) {
            LMFeedViewUtils.showShortSnack(
                binding.root,
                getString(R.string.lm_feed_selected_at_least_one_report_tag)
            )
            return
        }

        //if [Others] is selected but reason is empty
        if (isOthersSelected == true && reason.isEmpty()) {
            LMFeedViewUtils.showShortSnack(
                binding.root,
                getString(R.string.lm_feed_please_enter_a_reason)
            )
            return
        }

        // update [reasonOrTag] with tag value or reason
        reasonOrTag = if (isOthersSelected == true) {
            reason
        } else {
            tagSelected?.name ?: reason
        }

        //call post api
        reportViewModel.postReport(
            reportExtras.entityId,
            reportExtras.uuid,
            reportExtras.entityType,
            tagSelected?.id,
            reason
        )
    }

    private fun fetchData() {
        reportViewModel.getReportTags()
    }

    private fun observeData() {
        reportViewModel.listOfTagViewData.observe(viewLifecycleOwner) { tags ->
            //todo: replace items
//            mAdapter.replace(tags)
        }

        reportViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            LMFeedViewUtils.showErrorMessageToast(requireContext(), error)
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }

        reportViewModel.postReportResponse.observe(viewLifecycleOwner) { success ->
            if (success) {
                Log.d(LOG_TAG, "report send successfully")

                //todo: analytics
                //send analytics events
//                sendReportEvent()

                val intent = Intent().apply {
                    putExtra(
                        LM_FEED_REPORT_RESULT,
                        LMFeedReportType.getEntityType(this@LMFeedReportFragment.reportExtras.entityType)
                    )
                }
                //set result, from where the result is coming.
                requireActivity().setResult(Activity.RESULT_OK, intent)
                requireActivity().finish()
            }
        }
    }
}