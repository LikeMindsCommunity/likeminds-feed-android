import com.likeminds.feed.android.core.utils.feed.LMFeedBaseListViewFragment
import com.likeminds.feed.android.core.videofeed.view.CVPF
import com.likeminds.feed.android.core.videofeed.view.LMFeedVideoFeedFragment

class CVF : LMFeedVideoFeedFragment() {
    override var customPersonalizedFeed: LMFeedBaseListViewFragment? = CVPF()
}