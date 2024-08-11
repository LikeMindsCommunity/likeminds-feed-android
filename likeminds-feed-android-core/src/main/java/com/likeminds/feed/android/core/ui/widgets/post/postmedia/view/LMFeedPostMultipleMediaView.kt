package com.likeminds.feed.android.core.ui.widgets.post.postmedia.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.likeminds.feed.android.core.databinding.LmFeedPostMultipleMediaViewBinding
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostMultipleMediaViewStyle
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedMultipleMediaPostAdapter
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.utils.base.model.ITEM_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feed.android.core.utils.base.model.ITEM_MULTIPLE_MEDIA_VIDEO

class LMFeedPostMultipleMediaView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val inflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private val binding = LmFeedPostMultipleMediaViewBinding.inflate(inflater, this, true)

    val viewpagerMultipleMedia = binding.viewpagerMultipleMedia

    private lateinit var multipleMediaAdapter: LMFeedMultipleMediaPostAdapter

    //sets provided [postMultipleMediaStyle] to the multiple media type post
    fun setStyle(postMultipleMediaStyle: LMFeedPostMultipleMediaViewStyle) {

        //configures multiple media post indicator
        configureIndicator(postMultipleMediaStyle)
    }

    private fun configureIndicator(postMultipleMediaStyle: LMFeedPostMultipleMediaViewStyle) {
        binding.dotsIndicator.apply {
            //sets slide mode for the indicator
            postMultipleMediaStyle.indicatorSlideMode?.let { slideMode ->
                setSlideMode(slideMode)
            }

            //sets active/inactive color for indicator
            setSliderColor(
                context.getColor(postMultipleMediaStyle.indicatorInActiveColor),
                context.getColor(postMultipleMediaStyle.indicatorActiveColor)
            )

            //sets the active/inactive width for the indicator
            setSliderWidth(
                resources.getDimension(
                    postMultipleMediaStyle.indicatorInactiveWidth
                        ?: postMultipleMediaStyle.indicatorActiveWidth
                ),
                resources.getDimension(postMultipleMediaStyle.indicatorActiveWidth)
            )

            //sets the style of the indicator
            postMultipleMediaStyle.indicatorStyle?.let { indicatorStyle ->
                setIndicatorStyle(indicatorStyle)
            }

            //sets spacing between the indicators
            postMultipleMediaStyle.indicatorSpacing?.let { spacing ->
                setIndicatorGap(resources.getDimension(spacing))
            }

            //sets the height of the indicator (works for indicator style = [DASH or ROUND_RECT])
            postMultipleMediaStyle.indicatorHeight?.let { indicatorHeight ->
                setSliderHeight(resources.getDimension(indicatorHeight))
            }
        }
    }

    //sets the view pager to the multiple media type post with the provided listeners and attachments
    fun setViewPager(
        parentPosition: Int,
        listener: LMFeedPostAdapterListener,
        attachments: List<LMFeedAttachmentViewData>,
        isMediaRemovable: Boolean = false
    ) {
        binding.apply {
            viewpagerMultipleMedia.isSaveEnabled = false

            //set adapter to the view pager
            multipleMediaAdapter = LMFeedMultipleMediaPostAdapter(
                parentPosition,
                listener,
                isMediaRemovable
            )
            viewpagerMultipleMedia.adapter = multipleMediaAdapter

            val multipleMediaOnPageChangeCallback = object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    listener.onPostMultipleMediaPageChangeCallback(position, parentPosition)
                }
            }

            //registers page change callback
            viewpagerMultipleMedia.registerOnPageChangeCallback(multipleMediaOnPageChangeCallback)

            val updatedAttachments = getUpdatedAttachmentsForMultipleMedia(attachments)

            //replaces all the items in the multiple media post adapter
            multipleMediaAdapter.replace(updatedAttachments)

            //setups the indicator with the view pager
            dotsIndicator.setupWithViewPager(viewpagerMultipleMedia)
        }
    }

    /**
     * Removes the media at the provided index
     * @param position: index from which the media is to be removed
     */
    fun removeMedia(position: Int) {
        multipleMediaAdapter.removeIndex(position)
    }

    //returns the updated attachments list for multiple media view
    private fun getUpdatedAttachmentsForMultipleMedia(attachments: List<LMFeedAttachmentViewData>): List<LMFeedAttachmentViewData> {
        return attachments
            .filter {
                it.attachmentType == IMAGE || it.attachmentType == VIDEO
            }
            .map {
                when (it.attachmentType) {
                    IMAGE -> {
                        it.toBuilder().dynamicViewType(ITEM_MULTIPLE_MEDIA_IMAGE).build()
                    }

                    VIDEO -> {
                        it.toBuilder().dynamicViewType(ITEM_MULTIPLE_MEDIA_VIDEO).build()
                    }

                    else -> {
                        it
                    }
                }
            }
    }
}