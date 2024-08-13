package com.likeminds.feed.android.core.postmenu.view

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.likeminds.feed.android.core.databinding.LmFeedPostMenuBottomSheetFragmentBinding
import com.likeminds.feed.android.core.postmenu.adapter.LMFeedPostMenuAdapterListener
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuExtras
import com.likeminds.feed.android.core.postmenu.model.LMFeedPostMenuItemViewData
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.emptyExtrasException

open class LMFeedPostMenuBottomSheetFragment :
    BottomSheetDialogFragment(),
    LMFeedPostMenuAdapterListener {

    private lateinit var postMenuExtras: LMFeedPostMenuExtras

    private var postMenuListener: LMFeedPostMenuBottomSheetListener? = null

    private lateinit var binding: LmFeedPostMenuBottomSheetFragmentBinding

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
        initMenuList()
    }

    private fun initUI() {
        try {
            postMenuListener = parentFragment as LMFeedPostMenuBottomSheetListener?
        } catch (e: Exception) {
            throw ClassCastException("Calling fragment must implement LMFeedPostMenuBottomSheetListener interface")
        }

        (binding.root.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    //initializes the menu list
    private fun initMenuList() {
        binding.rvMenu.apply {
            setAdapter(this@LMFeedPostMenuBottomSheetFragment)
            replaceMenuItems(postMenuExtras.menuItems)
        }
    }

    //callback when user clicks on one of the menu item
    override fun onPostMenuItemClicked(position: Int, menuItem: LMFeedPostMenuItemViewData) {
        super.onPostMenuItemClicked(position, menuItem)

        postMenuListener?.onPostMenuItemClicked(postMenuExtras.postId, menuItem)
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