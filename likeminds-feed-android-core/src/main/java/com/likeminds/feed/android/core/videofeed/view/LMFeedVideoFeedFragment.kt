package com.likeminds.feed.android.core.videofeed.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.likeminds.feed.android.core.databinding.LmFeedFragmentVideoFeedBinding
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.socialfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapter
import com.likeminds.feed.android.core.videofeed.adapter.LMFeedVideoFeedAdapterListener

open class LMFeedVideoFeedFragment :
    Fragment(),
    LMFeedVideoFeedAdapterListener {

    private lateinit var binding: LmFeedFragmentVideoFeedBinding

    private lateinit var videoFeedAdapter: LMFeedVideoFeedAdapter

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

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

        setAdapter()
    }

    fun setAdapter() {
        videoFeedAdapter = LMFeedVideoFeedAdapter(this, postVideoPreviewAutoPlayHelper)
        videoFeedAdapter.addAll(
            listOf(LMFeedPostViewData.Builder()
                .mediaViewData(
                    LMFeedMediaViewData.Builder()
                        .attachments(
                            listOf(
                                LMFeedAttachmentViewData.Builder()
                                    .attachmentMeta(
                                        LMFeedAttachmentMetaViewData.Builder()
                                            .url("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                                            .build()
                                    )
                                    .attachmentType(REEL)
                                    .build()
                            )
                        )
                        .build()
                )
                .build(),
                LMFeedPostViewData.Builder()
                    .mediaViewData(
                        LMFeedMediaViewData.Builder()
                            .attachments(
                                listOf(
                                    LMFeedAttachmentViewData.Builder()
                                        .attachmentMeta(
                                            LMFeedAttachmentMetaViewData.Builder()
                                                .url("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
                                                .build()
                                        )
                                        .attachmentType(REEL)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build(),
                LMFeedPostViewData.Builder()
                    .mediaViewData(
                        LMFeedMediaViewData.Builder()
                            .attachments(
                                listOf(
                                    LMFeedAttachmentViewData.Builder()
                                        .attachmentMeta(
                                            LMFeedAttachmentMetaViewData.Builder()
                                                .url("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
                                                .build()
                                        )
                                        .attachmentType(REEL)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build(),
                LMFeedPostViewData.Builder()
                    .mediaViewData(
                        LMFeedMediaViewData.Builder()
                            .attachments(
                                listOf(
                                    LMFeedAttachmentViewData.Builder()
                                        .attachmentMeta(
                                            LMFeedAttachmentMetaViewData.Builder()
                                                .url("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
                                                .build()
                                        )
                                        .attachmentType(REEL)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
            )
        )
        binding.vp2VideoFeed.adapter = videoFeedAdapter
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