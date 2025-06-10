package com.likeminds.feed.android.core.ui.widgets.poll.view

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.likeminds.feed.android.core.databinding.LmFeedAddPollOptionBottomSheetFragmentBinding
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.widgets.poll.model.LMFeedAddPollOptionExtras
import com.likeminds.feed.android.core.utils.*

open class LMFeedAddPollOptionBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "LMFeedAddPollOptionBottomSheetFragment"
        private const val LM_FEED_ADD_POLL_OPTION_EXTRAS = "LM_FEED_ADD_POLL_OPTION_EXTRAS"

        private lateinit var addPollOptionExtras: LMFeedAddPollOptionExtras

        @JvmStatic
        fun newInstance(
            fragmentManager: FragmentManager,
            addPollOptionExtras: LMFeedAddPollOptionExtras
        ) {
            LMFeedNavigationFragments.getInstance().getAddPollOptionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LM_FEED_ADD_POLL_OPTION_EXTRAS, addPollOptionExtras)
                }
            }.show(fragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedAddPollOptionBottomSheetFragmentBinding

    private var addPollOptionListener: LMFeedAddPollOptionBottomSheetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            addPollOptionExtras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_ADD_POLL_OPTION_EXTRAS,
                LMFeedAddPollOptionExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedAddPollOptionBottomSheetFragmentBinding.inflate(layoutInflater)

        binding.apply {
            customizeAddPollOptionTitle(tvAddPollOptionTitle)
            customizeAddPollOptionSubTitle(tvAddPollOptionSubtitle)
            customizeAddPollOptionInputBox(etOption)
            customizeAddPollOptionSubmitButton(btnSubmitPollOption)
            customizeAddPollOptionCrossIcon(ivCancelAddPollOption)

            //set background color
            val backgroundColor =
                LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.backgroundColor

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

    //customizes the add poll option title text view
    protected open fun customizeAddPollOptionTitle(tvAddPollOptionTitle: LMFeedTextView) {
        tvAddPollOptionTitle.setStyle(
            LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addOptionTitleTextStyle
        )
    }

    //customizes the add poll option subtitle text view
    protected open fun customizeAddPollOptionSubTitle(tvAddPollOptionSubtitle: LMFeedTextView) {
        tvAddPollOptionSubtitle.setStyle(
            LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addOptionSubtitleTextStyle
        )
    }

    //customizes the add poll option input box
    protected open fun customizeAddPollOptionInputBox(etOption: LMFeedEditText) {
        etOption.setStyle(
            LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addOptionInputBoxStyle
        )
    }

    //customizes the add poll option submit button
    protected open fun customizeAddPollOptionSubmitButton(btnSubmitPollOption: LMFeedButton) {
        btnSubmitPollOption.apply {
            setStyle(
                LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addOptionSubmitButtonStyle
            )
            setSubmitButtonEnabled(false)
        }
    }

    //customizes the add poll option cross icon
    protected open fun customizeAddPollOptionCrossIcon(ivCancelAddPollOption: LMFeedIcon) {
        ivCancelAddPollOption.setStyle(
            LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addPollOptionCrossIconStyle
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initUI() {
        try {
            addPollOptionListener = parentFragment as LMFeedAddPollOptionBottomSheetListener?
        } catch (e: Exception) {
            throw ClassCastException("Calling fragment must implement LMFeedReasonChooseBottomSheetListener interface")
        }
    }

    private fun initListeners() {
        binding.apply {
            etOption.addTextChangedListener {
                setSubmitButtonEnabled(it.toString().trim().isNotEmpty())
            }

            ivCancelAddPollOption.setOnClickListener {
                onAddPollOptionCancelled()
            }

            btnSubmitPollOption.setOnClickListener {
                onAddPollOptionSubmitted()
            }
        }
    }

    protected open fun onAddPollOptionCancelled() {
        dismiss()
    }

    protected open fun onAddPollOptionSubmitted() {
        addPollOptionListener?.onAddOptionSubmitted(
            addPollOptionExtras.postId,
            addPollOptionExtras.pollId,
            binding.etOption.text.toString().trim()
        )
        dismiss()
    }

    private fun setSubmitButtonEnabled(isEnabled: Boolean) {
        binding.btnSubmitPollOption.apply {
            this.isEnabled = isEnabled

            val submitButtonStyle =
                LMFeedStyleTransformer.addPollOptionBottomSheetFragmentStyle.addOptionSubmitButtonStyle
            if (isEnabled) {
                setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        submitButtonStyle.backgroundColor
                    )
                )
            } else {
                setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        submitButtonStyle.disabledButtonColor
                    )
                )
            }
        }
    }
}

interface LMFeedAddPollOptionBottomSheetListener {
    //callback when user clicks submit button on add poll option bottom sheet
    fun onAddOptionSubmitted(
        postId: String,
        pollId: String,
        option: String
    )
}