package com.likeminds.feed.android.core.videofeed.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.likeminds.feed.android.core.databinding.LmFeedFragmentVideoFeedBinding

open class LMFeedVideoFeedFragment :
    Fragment() {

    private lateinit var binding: LmFeedFragmentVideoFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentVideoFeedBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}