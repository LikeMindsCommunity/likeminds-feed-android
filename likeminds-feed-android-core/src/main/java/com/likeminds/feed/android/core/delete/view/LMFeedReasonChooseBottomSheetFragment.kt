package com.likeminds.feed.android.core.delete.view

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.likeminds.feed.android.core.databinding.LmFeedReasonChooseBottomSheetFragmentBinding
import com.likeminds.feed.android.core.delete.adapter.LMFeedReasonChooseAdapterListener
import com.likeminds.feed.android.core.delete.model.LMFeedReasonChooseViewData
import com.likeminds.feed.android.core.delete.viewmodel.LMFeedReasonChooseViewModel
import com.likeminds.feed.android.core.utils.LMFeedViewUtils

class LMFeedReasonChooseBottomSheetFragment : BottomSheetDialogFragment(),
    LMFeedReasonChooseAdapterListener {

    companion object {
        private const val TAG = "LMFeedReasonChooseBottomSheetFragment"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager) =
            LMFeedReasonChooseBottomSheetFragment().show(fragmentManager, TAG)
    }

    private lateinit var binding: LmFeedReasonChooseBottomSheetFragmentBinding

    private var reasonChooseDialogListener: LMFeedReasonChooseBottomSheetListener? = null

    private val reasonChooseViewModel: LMFeedReasonChooseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedReasonChooseBottomSheetFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        fetchData()
        observeData()
    }

    private fun initUI() {
        try {
            reasonChooseDialogListener = parentFragment as LMFeedReasonChooseBottomSheetListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement LMFeedReasonChooseBottomSheetListener interface")
        }

        (binding.root.parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.rvReasons.setAdapter(this)
    }

    private fun fetchData() {
        reasonChooseViewModel.getReportTags()
    }

    private fun observeData() {
        // observes [listOfTagViewData] and replaces items in sheet
        reasonChooseViewModel.listOfTagViewData.observe(viewLifecycleOwner) { tags ->
            binding.rvReasons.replaceReasons(tags)
        }

        // observes [errorMessage] and shows error toast
        reasonChooseViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            LMFeedViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            dismiss()
        }
    }

    override fun onReasonSelected(position: Int, reasonViewData: LMFeedReasonChooseViewData) {
        reasonChooseDialogListener?.onReasonSelected(position, reasonViewData)
        dismiss()
    }
}

interface LMFeedReasonChooseBottomSheetListener {
    fun onReasonSelected(position: Int, reasonViewData: LMFeedReasonChooseViewData)
}