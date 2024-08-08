package com.likeminds.feed.android.core.videofeed.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.databinding.LmFeedFragmentVideoFeedBinding
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.LMFeedRoute
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
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

    //callback when the user clicks on the post like button
    override fun onPostLikeClicked(position: Int, postViewData: LMFeedPostViewData) {
        val userPreferences = LMFeedUserPreferences(requireContext())
        val loggedInUUID = userPreferences.getUUID()

        //call api
        videoFeedViewModel.likePost(
            postViewData.id,
            postViewData.footerViewData.isLiked,
            loggedInUUID
        )

        val adapterPosition = getIndexAndPostFromAdapter(postViewData.id)?.first ?: return

        //update view pager item
        videoFeedAdapter.update(adapterPosition, postViewData)
    }

    //callback when the user clicks on the link in the post content
    override fun onPostContentLinkClicked(url: String) {
        // creates a route and returns an intent to handle the link
        val intent = LMFeedRoute.handleDeepLink(requireContext(), url)
        if (intent != null) {
            try {
                // starts activity with the intent
                ActivityCompat.startActivity(requireContext(), intent, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //callback when the user clicks on the post author header
    override fun onPostAuthorHeaderClicked(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostAuthorHeaderClicked(position, postViewData)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfile(postViewData.headerViewData.user)
    }

    //callback when the tag of the user is clicked
    override fun onPostTaggedMemberClicked(position: Int, uuid: String) {
        super.onPostTaggedMemberClicked(position, uuid)

        val coreCallback = LMFeedCoreApplication.getLMFeedCoreCallback()
        coreCallback?.openProfileWithUUID(uuid)
    }

    /**
     * Adapter Util Block
     **/

    //get index and post from the adapter using postId
    private fun getIndexAndPostFromAdapter(postId: String): Pair<Int, LMFeedPostViewData>? {
        val index = videoFeedAdapter.items().indexOfFirst {
            (it is LMFeedPostViewData) && (it.id == postId)
        }

        if (index == -1) {
            return null
        }

        val post = videoFeedAdapter.items()[index] as? LMFeedPostViewData ?: return null

        return Pair(index, post)
    }
}