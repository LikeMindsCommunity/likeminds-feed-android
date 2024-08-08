package com.likeminds.feed.android.core.videofeed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.likeminds.feed.android.core.databinding.LmFeedFragmentVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.viewmodel.LMFeedVideoFeedViewModel

open class LMFeedVideoFeedFragment :
    Fragment(),
    LMFeedPostAdapterListener {

    private lateinit var binding: LmFeedFragmentVideoFeedBinding

    private lateinit var videoFeedAdapter: LMFeedVideoFeedAdapter

    private val videoFeedViewModel: LMFeedVideoFeedViewModel by viewModels()

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

    private var pageToCall: Int = 1

    private var previousTotal: Int = 0

    companion object {
        private const val VIDEO_PRELOAD_THRESHOLD = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentVideoFeedBinding.inflate(layoutInflater)

        binding.vp2VideoFeed.apply {
            for (i in 0 until childCount) {
                if (getChildAt(i) is RecyclerView) {
                    val recyclerView = getChildAt(i) as RecyclerView
                    val itemAnimator = recyclerView.itemAnimator
                    if (itemAnimator != null && itemAnimator is SimpleItemAnimator) {
                        itemAnimator.supportsChangeAnimations = false
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        setAdapter()
        observeResponses()
    }

    private fun fetchData() {
        videoFeedViewModel.getFeed(pageToCall)
    }

    fun setAdapter() {
        videoFeedAdapter = LMFeedVideoFeedAdapter(this, postVideoPreviewAutoPlayHelper)

        binding.vp2VideoFeed.apply {
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (position >= 0 && videoFeedAdapter.items()[position] != null) {
                        videoFeedAdapter.notifyItemChanged(position)
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                    // Call API if the last page is reached
                    val size: Int = videoFeedAdapter.itemCount

                    if (currentItem > 0 && currentItem >= size - VIDEO_PRELOAD_THRESHOLD) {
                        if (videoFeedAdapter.itemCount > previousTotal) {
                            pageToCall++
                            previousTotal = videoFeedAdapter.itemCount
                            videoFeedViewModel.getFeed(pageToCall)
                        } else {
                            // todo: add that you have reached end
                        }
                    }
                }
            })

            adapter = videoFeedAdapter
        }
    }

    private fun observeResponses() {
        videoFeedViewModel.videoFeedResponse.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val posts = response.second

            //todo:
            if (page == 1) {
                videoFeedAdapter.replace(posts)
//                checkPostsAndReplace(posts)
            } else {
                videoFeedAdapter.addAll(posts)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val position = binding.vp2VideoFeed.currentItem

        if (position >= 0
            && videoFeedAdapter.itemCount > position
            && videoFeedAdapter.items()[position] != null
        ) {
            videoFeedAdapter.notifyItemChanged(position)
        }
    }

    override fun onPause() {
        super.onPause()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }
}