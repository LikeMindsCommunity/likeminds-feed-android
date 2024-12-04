package com.likeminds.feed.android.core.report.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentReportBinding
import com.likeminds.feed.android.core.report.adapter.LMFeedReportTagAdapterListener
import com.likeminds.feed.android.core.report.model.*
import com.likeminds.feed.android.core.report.viewmodel.LMFeedReportViewModel
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import java.util.Locale

open class LMFeedReportFragment : Fragment(), LMFeedReportTagAdapterListener {

    private lateinit var binding: LmFeedFragmentReportBinding

    private lateinit var reportExtras: LMFeedReportExtras

    private val reportViewModel: LMFeedReportViewModel by viewModels()

    private var tagSelected: LMFeedReportTagViewData? = null
    private lateinit var reasonOrTag: String

    companion object {
        const val TAG = "LMFeedReportFragment"
        const val LM_FEED_REPORT_RESULT = "LM_FEED_REPORT_RESULT"

        fun getInstance(reportExtras: LMFeedReportExtras): LMFeedReportFragment {
            val reportFragment = LMFeedReportFragment()
            val bundle = Bundle()
            bundle.putParcelable(LMFeedReportActivity.LM_FEED_REPORT_EXTRAS, reportExtras)
            reportFragment.arguments = bundle
            return reportFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    private fun receiveExtras() {
        reportExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LMFeedReportActivity.LM_FEED_REPORT_EXTRAS,
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
        }
        return binding.root
    }

    //customizes the header view in the report fragment
    protected open fun customizeReportFragmentHeaderView(headerViewReport: LMFeedHeaderView) {
        headerViewReport.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_report_abuse))
        }
    }

    //customizes the header text in the report fragment
    protected open fun customizeReportHeaderText(tvReportHeader: LMFeedTextView) {
        tvReportHeader.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportHeaderStyle)
        }
    }

    //customizes the sub header text in the report fragment
    protected open fun customizeReportSubHeaderText(tvReportSubHeader: LMFeedTextView) {
        tvReportSubHeader.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportSubHeaderStyle)
        }
    }

    //customizes the input box for report reason in the report fragment
    protected open fun customizeReportReasonInput(etReason: LMFeedEditText) {
        etReason.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportReasonInputStyle)
        }
    }

    //customizes the report button in the report fragment
    protected open fun customizeReportButton(btnPostReport: LMFeedButton) {
        btnPostReport.apply {
            setStyle(LMFeedStyleTransformer.reportFragmentViewStyle.reportButtonStyle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
        fetchData()
        observeData()
    }

    private fun initUI() {
        initRecyclerView()
        initViewAsType()
    }

    private fun initRecyclerView() {
        binding.rvReportTags.setAdapter(this)
    }

    //set headers and sub header as per report type
    private fun initViewAsType() {
        binding.apply {
            when (reportExtras.entityType) {
                REPORT_TYPE_POST -> {
                    tvReportSubHeader.text = getString(
                        R.string.lm_feed_report_sub_header,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                }

                REPORT_TYPE_COMMENT -> {
                    tvReportSubHeader.text = getString(
                        R.string.lm_feed_report_sub_header,
                        getString(
                            R.string.lm_feed_s_comment,
                            LMFeedCommunityUtil.getCommentVariable()
                                .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                        )
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

        binding.headerViewReport.setOnClickListener {
            onNavigationIconClick()
        }
    }

    //processes the user submission of the report
    protected open fun onReportSubmitted() {
        binding.apply {
            //get selected tag
            tagSelected = rvReportTags.reportTags()
                .map { it as LMFeedReportTagViewData }
                .find { it.isSelected }

            //get reason for [edittext]
            val reason = etReason.text?.trim().toString()
            val isOthersSelected = tagSelected?.name?.contains("Others", true)

            //if no tag is selected
            if (tagSelected == null) {
                LMFeedViewUtils.showShortSnack(
                    root,
                    getString(R.string.lm_feed_selected_at_least_one_report_tag)
                )
                return
            }

            //if [Others] is selected but reason is empty
            if (isOthersSelected == true && reason.isEmpty()) {
                LMFeedViewUtils.showShortSnack(
                    root,
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
    }

    //processes the navigation icon click
    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun fetchData() {
        reportViewModel.getReportTags()
    }

    private fun observeData() {
        reportViewModel.listOfTagViewData.observe(viewLifecycleOwner) { tags ->
            binding.rvReportTags.replaceReportTags(tags)
        }

        reportViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            LMFeedViewUtils.showErrorMessageToast(requireContext(), error)
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }

        reportViewModel.postReportResponse.observe(viewLifecycleOwner) { success ->
            if (success) {
                Log.d(LOG_TAG, "report send successfully")

                //send analytics events
                sendReportEvent()

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

    //send report event depending upon which type of the report is created
    private fun sendReportEvent() {
        when (reportExtras.entityType) {
            REPORT_TYPE_POST -> {
                // sends post reported event
                LMFeedAnalytics.sendPostReportedEvent(
                    reportExtras.entityId,
                    reportExtras.uuid,
                    LMFeedViewUtils.getPostTypeFromViewType(reportExtras.postViewType),
                    reasonOrTag
                )
            }

            REPORT_TYPE_COMMENT -> {
                // sends comment reported event
                LMFeedAnalytics.sendCommentReportedEvent(
                    reportExtras.postId,
                    reportExtras.uuid,
                    reportExtras.entityId,
                    reasonOrTag
                )
            }

            REPORT_TYPE_REPLY -> {
                // sends reply reported event
                LMFeedAnalytics.sendReplyReportedEvent(
                    reportExtras.postId,
                    reportExtras.uuid,
                    reportExtras.parentCommentId,
                    reportExtras.entityId,
                    reasonOrTag
                )
            }
        }
    }

    override fun onReportTagSelected(reportTagViewData: LMFeedReportTagViewData) {
        super.onReportTagSelected(reportTagViewData)

        binding.apply {
            //check if [Others] is selected, edit text for reason should be visible
            etReason.isVisible = reportTagViewData.name.contains("Others", true)

            //replace list in adapter and only highlight selected tag
            rvReportTags.replaceReportTags(
                rvReportTags.reportTags()
                    .map {
                        (it as LMFeedReportTagViewData).toBuilder()
                            .isSelected(it.id == reportTagViewData.id)
                            .build()
                    })
        }
    }
}