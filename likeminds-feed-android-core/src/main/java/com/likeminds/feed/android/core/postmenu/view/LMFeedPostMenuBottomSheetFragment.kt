package com.likeminds.feed.android.core.postmenu.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.likeminds.feed.android.core.databinding.LmFeedPostMenuBottomSheetFragmentBinding
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuExtras
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.emptyExtrasException

open class LMFeedPostMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var postMenuExtras: LMFeedPostMenuExtras

    private var postMenuListener: LMFeedPostMenuBottomSheetListener? = null

    companion object {
        private const val TAG = "LMFeedPostMenuBottomSheetFragment"
        private const val LM_FEED_POST_MENU_EXTRAS = "LM_FEED_POST_MENU_EXTRAS"

        @JvmStatic
        fun newInstance(
            fragmentManager: FragmentManager,
            postMenuExtras: LMFeedPostMenuExtras
        ) {
            LMFeedPostMenuBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LM_FEED_POST_MENU_EXTRAS, postMenuExtras)
                }
            }.show(fragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedPostMenuBottomSheetFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        arguments?.let {
            postMenuExtras = LMFeedExtrasUtil.getParcelable(
                it,
                LM_FEED_POST_MENU_EXTRAS,
                LMFeedPostMenuExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedPostMenuBottomSheetFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initUI() {
        try {
            postMenuListener = parentFragment as LMFeedPostMenuBottomSheetListener?
        } catch (e: Exception) {
            throw ClassCastException("Calling fragment must implement LMFeedPostMenuBottomSheetListener interface")
        }
    }

    private fun initListeners() {
        //onPostMenuClicked()
    }

    protected open fun onPostMenuClicked(position: Int) {
        postMenuListener?.onPostMenuItemClicked(
            postMenuExtras.postId,
            postMenuExtras.menuItems[position]
        )
        dismiss()
    }
}

interface LMFeedPostMenuBottomSheetListener {
    //callback when user clicks on the post menu item
    fun onPostMenuItemClicked(
        postId: String,
        menuItem: LMFeedPostMenuItemViewData
    )
}