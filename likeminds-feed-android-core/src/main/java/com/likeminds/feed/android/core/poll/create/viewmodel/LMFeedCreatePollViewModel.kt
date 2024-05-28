package com.likeminds.feed.android.core.poll.create.viewmodel

import androidx.lifecycle.*
import com.likeminds.feed.android.core.databinding.LmFeedItemCreatePollOptionBinding
import com.likeminds.feed.android.core.poll.create.model.LMFeedCreatePollOptionViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.LMFeedClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LMFeedCreatePollViewModel : ViewModel() {

    val lmFeedClient = LMFeedClient.getInstance()

    var pollExpiryTime: Long? = null
        private set

    private val pollOptionItemBindingMap by lazy {
        LinkedHashMap<Int, LmFeedItemCreatePollOptionBinding>()
    }

    private val _loggedInUser by lazy { MutableLiveData<LMFeedUserViewData>() }
    val loggedInUser: LiveData<LMFeedUserViewData> by lazy { _loggedInUser }

    private val errorEventChannel = Channel<ErrorEvent>(Channel.BUFFERED)
    val errorEvent = errorEventChannel.receiveAsFlow()

    sealed class ErrorEvent {
        data class GetLoggedInUserError(val message: String?) : ErrorEvent()
    }

    companion object {
        const val MULTIPLE_OPTION_STATE_MAX = "At max"
        const val MULTIPLE_OPTION_STATE_EXACTLY = "Exactly"
        const val MULTIPLE_OPTION_STATE_LEAST = "At least"
    }

    // Get the logged in user
    fun getLoggedInUser() {
        viewModelScope.launchIO {
            val response = lmFeedClient.getLoggedInUserWithRights()
            if (response.success) {
                // Get the logged in user
                val loggedInUser = response.data?.user

                // Convert the user to view data
                val userViewData = LMFeedViewDataConvertor.convertUser(loggedInUser)

                // Set the view data to send to the UI
                _loggedInUser.postValue(userViewData)
            } else {
                // Send the error event
                errorEventChannel.send(ErrorEvent.GetLoggedInUserError(response.errorMessage))
            }
        }
    }

    // set the poll expiry time
    fun setPollExpiryTime(expiryTime: Long) {
        pollExpiryTime = expiryTime
    }

    //create the initial poll option list and clear the binding map
    fun createInitialPollOptionList(): List<LMFeedCreatePollOptionViewData> {
        // Clear the map
        pollOptionItemBindingMap.clear()

        // create the list
        return listOf(
            getEmptyPollOption(),
            getEmptyPollOption(),
        )
    }

    //create a empty poll option view data
    fun getEmptyPollOption(): LMFeedCreatePollOptionViewData {
        return LMFeedCreatePollOptionViewData.Builder().build()
    }

    //add the binding to the map
    fun addBindingToMap(position: Int, binding: LmFeedItemCreatePollOptionBinding) {
        pollOptionItemBindingMap[position] = binding
    }

    //return multi state option list
    fun getMultipleOptionStateList(): ArrayList<String> {
        return arrayListOf(
            MULTIPLE_OPTION_STATE_MAX,
            MULTIPLE_OPTION_STATE_EXACTLY,
            MULTIPLE_OPTION_STATE_LEAST
        )
    }

    //return multi option list
    fun getMultipleOptionNoList(): ArrayList<String> {
        return arrayListOf(
            "Select option",
            "1 option",
            "2 options",
            "3 options",
            "4 options",
            "5 options",
            "6 options",
            "7 options",
            "8 options",
            "9 options",
            "10 options"
        )
    }
}