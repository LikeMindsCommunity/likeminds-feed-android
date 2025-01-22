package com.likeminds.feed.android.core.videofeed.view

import androidx.fragment.app.Fragment
import com.likeminds.feed.android.core.utils.feed.LMFeedType
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feed.android.core.utils.feed.LMFeedType.UNIVERSAL_FEED
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedConfig

open class LMFeedVideoFeedFragment : Fragment() {

    companion object {
        fun getInstance(
            feedType: LMFeedType,
            config: LMFeedVideoFeedConfig?
        ): Fragment {
            return when (feedType) {
                PERSONALISED_FEED -> LMFeedVideoFeedPersonalizedFragment()
                UNIVERSAL_FEED -> LMFeedVideoFeedUniversalFragment(config)
            }
        }
    }
}