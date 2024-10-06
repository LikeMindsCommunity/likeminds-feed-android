package com.likeminds.feed.android.core.delete.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedDialogFragmentAdminDeleteBinding
import com.likeminds.feed.android.core.delete.model.*
import com.likeminds.feed.android.core.ui.widgets.alertdialog.view.LMFeedAlertDialogView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction

open class LMFeedAdminDeleteDialogFragment
    : DialogFragment(),
    LMFeedReasonChooseBottomSheetListener {

    companion object {
        private const val TAG = "LMFeedAdminDeleteDialogFragment"
        private const val LM_FEED_ADMIN_DELETE_EXTRAS = "LM_FEED_ADMIN_DELETE_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            deleteExtras: LMFeedDeleteExtras
        ) {
            LMFeedAdminDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LM_FEED_ADMIN_DELETE_EXTRAS, deleteExtras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedDialogFragmentAdminDeleteBinding
    private lateinit var deleteExtras: LMFeedDeleteExtras

    private var deleteDialogListener: LMFeedAdminDeleteDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            deleteDialogListener = parentFragment as LMFeedAdminDeleteDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement LMFeedAdminDeleteDialogListener interface")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            deleteExtras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_ADMIN_DELETE_EXTRAS,
                LMFeedDeleteExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedDialogFragmentAdminDeleteBinding.inflate(layoutInflater)

        customizeAdminDeleteDialog(binding.alertDialogDelete)
        return binding.root
    }

    //customizes the admin delete alert dialog
    protected open fun customizeAdminDeleteDialog(alertDialogDelete: LMFeedAlertDialogView) {
        val adminDeleteDialogStyle =
            LMFeedStyleTransformer.adminDeleteDialogFragmentStyle.adminDeleteDialogStyle

        alertDialogDelete.setStyle(adminDeleteDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    //sets data as per content type [COMMENT/POST]
    private fun initUI() {
        binding.alertDialogDelete.apply {
            if (deleteExtras.entityType == DELETE_TYPE_POST) {
                setAlertTitle(
                    getString(
                        R.string.lm_feed_delete_s_question,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                )

                setAlertSubtitle(
                    getString(
                        R.string.lm_feed_delete_s_message,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                )
            } else {
                setAlertTitle(
                    getString(
                        R.string.lm_feed_delete_s_comment_question,
                        LMFeedCommunityUtil.getCommentVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                )
                setAlertSubtitle(
                    getString(
                        R.string.lm_feed_delete_s_comment_message,
                        LMFeedCommunityUtil.getCommentVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    )
                )
            }
        }
    }

    private fun initListeners() {
        binding.alertDialogDelete.apply {
            setAlertSelectorClickListener {
                onAdminDeleteAlertSelectorClicked()
            }

            setPositiveButtonClickListener {
                onAdminDeleteAlertPositiveButtonClicked()
            }

            setNegativeButtonClickListener {
                onAdminDeleteAlertNegativeButtonClicked()
            }

            // sets listener to the reason edit text
            etReason.doAfterTextChanged {
                val reason = it.toString().trim()
                setPositiveButtonEnabled(reason.isNotEmpty())
            }
        }
    }

    //processes the admin delete alert dialog box selector clicked
    protected open fun onAdminDeleteAlertSelectorClicked() {
        LMFeedReasonChooseBottomSheetFragment.newInstance(childFragmentManager)
    }

    //processes the admin delete alert dialog box positive button click
    protected open fun onAdminDeleteAlertPositiveButtonClicked() {
        binding.apply {
            val data = reasonData ?: return
            val tag = data.value
            val reason = if (tag == "Others") {
                alertDialogDelete.getAlertInputReason()
            } else {
                tag
            }

            if (reason.isEmpty()) {
                return
            }

            deleteDialogListener?.onEntityDeletedByAdmin(deleteExtras, reason)
            dismiss()
        }
    }

    //processes the admin delete alert dialog box negative button click
    protected open fun onAdminDeleteAlertNegativeButtonClicked() {
        dismiss()
    }

    override fun onReasonSelected(position: Int, reasonViewData: LMFeedReasonChooseViewData) {
        binding.apply {
            reasonData = reasonViewData
            alertDialogDelete.setAlertSelectorText(reasonViewData.value)
            alertDialogDelete.etReason.setText("")

            if (reasonViewData.value == "Others") {
                alertDialogDelete.setAlertInputReasonVisibility(true)
                alertDialogDelete.setAlertInputHint(getString(R.string.lm_feed_enter_reason))
                alertDialogDelete.setPositiveButtonEnabled(false)
            } else {
                alertDialogDelete.setAlertInputReasonVisibility(false)
                alertDialogDelete.setPositiveButtonEnabled(true)
            }
        }
    }
}

interface LMFeedAdminDeleteDialogListener {
    fun onEntityDeletedByAdmin(deleteExtras: LMFeedDeleteExtras, reason: String) {
        //triggered when the entity is deleted by admin
    }
}