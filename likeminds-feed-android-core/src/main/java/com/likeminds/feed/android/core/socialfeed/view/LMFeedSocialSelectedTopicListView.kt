package com.likeminds.feed.android.core.socialfeed.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.*
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialSelectedTopicAdapter
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedSocialSelectedTopicAdapterListener
import com.likeminds.feed.android.core.utils.base.LMFeedBaseViewType

class LMFeedSocialSelectedTopicListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    val linearLayoutManager: LinearLayoutManager

    private lateinit var selectedTopicAdapter: LMFeedSocialSelectedTopicAdapter

    init {
        setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        layoutManager = linearLayoutManager

        if (itemAnimator is SimpleItemAnimator) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    //sets the adapter with the provided [listener] to the selected topic recycler view
    fun setAdapter(listener: LMFeedSocialSelectedTopicAdapterListener) {
        selectedTopicAdapter = LMFeedSocialSelectedTopicAdapter(listener)
        adapter = selectedTopicAdapter
    }

    //returns the list of all the selected topics in the adapter
    fun getAllSelectedTopics(): List<LMFeedBaseViewType> {
        return selectedTopicAdapter.items()
    }

    //replaces all the selected topics with the provided [selectedTopics] in the adapter
    fun replaceSelectedTopics(selectedTopics: List<LMFeedTopicViewData>) {
        selectedTopicAdapter.replace(selectedTopics)
    }

    //removes the selected topics at the provided [position] in the adapter and notifies the recycler view
    fun removeSelectedTopicAndNotify(position: Int) {
        selectedTopicAdapter.removeIndexWithNotifyDataSetChanged(position)
    }

    //clear all the selected topics in the adapter and notifies the recycler view
    fun clearAllSelectedTopicsAndNotify() {
        selectedTopicAdapter.clearAndNotify()
    }
}