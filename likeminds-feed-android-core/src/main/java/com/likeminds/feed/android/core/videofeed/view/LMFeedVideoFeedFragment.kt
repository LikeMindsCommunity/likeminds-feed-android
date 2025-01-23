package com.likeminds.feed.android.core.videofeed.view

import androidx.fragment.app.Fragment
import com.likeminds.feed.android.core.utils.feed.LMFeedType
import com.likeminds.feed.android.core.utils.feed.LMFeedType.PERSONALISED_FEED
import com.likeminds.feed.android.core.utils.feed.LMFeedType.UNIVERSAL_FEED
import com.likeminds.feed.android.core.videofeed.model.LMFeedVideoFeedConfig

open class LMFeedVideoFeedFragment : Fragment() {

    companion object {

        @JvmStatic
        fun getInstance(
            feedType: LMFeedType,
            config: LMFeedVideoFeedConfig? = null
        ): Fragment {
            return when (feedType) {
                PERSONALISED_FEED -> LMFeedVideoFeedPersonalizedFragment(config)
                UNIVERSAL_FEED -> LMFeedVideoFeedUniversalFragment(config)
            }
        }
    }
}