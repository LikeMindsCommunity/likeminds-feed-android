package com.likeminds.feed.android.core.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.likeminds.feed.android.core.activityfeed.view.LMFeedActivityFeedFragment
import com.likeminds.feed.android.core.databinding.LmFeedSearchFragmentBinding

open class LMFeedSearchFragment : Fragment() {
    private lateinit var binding: LmFeedSearchFragmentBinding

    companion object {
        const val TAG = "LMFeedSearchFragment"

        fun getInstance(): LMFeedSearchFragment {
            return LMFeedSearchFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedSearchFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.apply {
//            searchBarViewSearch.setNavigationIconClickListener {
//                onNavigationIconClick()
//            }
        }
    }

}