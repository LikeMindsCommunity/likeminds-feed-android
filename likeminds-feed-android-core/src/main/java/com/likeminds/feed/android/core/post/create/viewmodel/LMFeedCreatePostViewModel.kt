package com.likeminds.feed.android.core.post.create.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.customgallery.media.model.*
import com.likeminds.customgallery.media.util.MediaUtils
import com.likeminds.customgallery.utils.file.util.FileUtil
import com.likeminds.feed.android.core.post.create.model.LMFeedFileUploadViewData
import com.likeminds.feed.android.core.post.create.util.LMFeedPostAttachmentUploadWorker
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.coroutine.launchIO
import com.likeminds.feed.android.core.utils.user.LMFeedUserPreferences
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.LMResponse
import com.likeminds.likemindsfeed.helper.model.DecodeUrlRequest
import com.likeminds.likemindsfeed.helper.model.DecodeUrlResponse
import com.likeminds.likemindsfeed.post.model.AddPostRequest
import com.likeminds.likemindsfeed.post.model.AddTemporaryPostRequest
import com.likeminds.likemindsfeed.topic.model.GetTopicRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.collections.set

class LMFeedCreatePostViewModel : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _postAdded = MutableLiveData<Boolean>()
    val postAdded: LiveData<Boolean> = _postAdded

    private val _decodeUrlResponse by lazy {
        MutableLiveData<LMFeedLinkOGTagsViewData>()
    }

    val decodeUrlResponse: LiveData<LMFeedLinkOGTagsViewData> by lazy {
        _decodeUrlResponse
    }

    private var temporaryPostId: Long? = null

    private val _loggedInUser: MutableLiveData<LMFeedUserViewData> by lazy { MutableLiveData<LMFeedUserViewData>() }
    val loggedInUser: LiveData<LMFeedUserViewData> by lazy { _loggedInUser }

    private val _showTopicFilter by lazy { MutableLiveData<Boolean>() }
    val showTopicFilter: LiveData<Boolean> by lazy { _showTopicFilter }

    sealed class ErrorMessageEvent {
        data class GetLoggedInUser(val errorMessage: String?) : ErrorMessageEvent()

        data class GetTopic(val errorMessage: String?) : ErrorMessageEvent()

        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()

        data class DecodeUrl(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    // call to get logged user details
    fun getLoggedInUser() {
        viewModelScope.launchIO {
            val response = lmFeedClient.getLoggedInUserWithRights()

            if (response.success) {
                //get user
                val loggedInUser = response.data?.user

                //convert it to [LMFeedUserViewData]
                val userViewData = LMFeedViewDataConvertor.convertUser(loggedInUser)

                //send to UI
                _loggedInUser.postValue(userViewData)
            } else {
                //send error message to UI
                errorEventChannel.send(ErrorMessageEvent.GetLoggedInUser(response.errorMessage))
            }
        }
    }

    // calls DecodeUrl API
    fun decodeUrl(url: String) {
        viewModelScope.launchIO {
            val request = DecodeUrlRequest.Builder().url(url).build()

            val response = lmFeedClient.decodeUrl(request)
            postDecodeUrlResponse(response)
        }
    }

    //processes and posts the DecodeUrl response in LiveData
    private fun postDecodeUrlResponse(response: LMResponse<DecodeUrlResponse>) {
        viewModelScope.launchIO {
            if (response.success) {
                // processes link og tags if API call was successful
                val data = response.data ?: return@launchIO
                val ogTags = data.ogTags
                _decodeUrlResponse.postValue(LMFeedViewDataConvertor.convertLinkOGTags(ogTags))
            } else {
                // posts error message if API call failed
                errorEventChannel.send(ErrorMessageEvent.DecodeUrl(response.errorMessage))
            }
        }
    }

    //calls to topics api and check whether to show topics view or not
    fun getAllTopics() {
        viewModelScope.launchIO {
            val request = GetTopicRequest.Builder()
                .page(1)
                .pageSize(10)
                .isEnabled(true)
                .build()

            val response = lmFeedClient.getTopics(request)

            if (response.success) {
                val topics = response.data?.topics
                if (topics.isNullOrEmpty()) {
                    _showTopicFilter.postValue(false)
                } else {
                    _showTopicFilter.postValue(true)
                }
            } else {
                _showTopicFilter.postValue(false)
                errorEventChannel.send(ErrorMessageEvent.GetTopic(response.errorMessage))
            }
        }
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(
        context: Context,
        postTextContent: String?,
        fileUris: List<SingleUriData>? = null,
        ogTags: LMFeedLinkOGTagsViewData? = null,
        selectedTopics: ArrayList<LMFeedTopicViewData>? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }

            val topicIds = selectedTopics?.map {
                it.id
            }

            if (fileUris != null) {
                // if the post has upload-able attachments
                temporaryPostId = System.currentTimeMillis()
                val postId = temporaryPostId ?: 0
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                val uploadData = startMediaUploadWorker(
                    context,
                    postId,
                    updatedFileUris.size
                )

                // adds post data in local db
                storePost(
                    uploadData,
                    updatedText,
                    updatedFileUris,
                    selectedTopics
                )
            } else {
                // if the post does not have any upload-able attachments
                val requestBuilder = AddPostRequest.Builder()
                    .text(updatedText)

                if (ogTags != null) {
                    // if the post has ogTags
                    requestBuilder.attachments(LMFeedViewDataConvertor.convertAttachments(ogTags))
                }

                if (!topicIds.isNullOrEmpty()) {
                    //if user has selected any topics
                    requestBuilder.topicIds(topicIds)
                }

                val request = requestBuilder.build()

                val response = lmFeedClient.addPost(request)
                if (response.success) {
                    // sends post creation completed event
                    sendPostCreationCompletedEvent(
                        updatedText,
                        ogTags,
                        selectedTopics
                    )
                    _postAdded.postValue(true)
                } else {
                    errorEventChannel.send(ErrorMessageEvent.AddPost(response.errorMessage))
                }
            }
        }
    }

    /**
     * Includes attachment's meta data such as dimensions, thumbnails, etc
     * @param context
     * @param files List<SingleUriData>?
     */
    private fun includeAttachmentMetaData(
        context: Context,
        files: List<SingleUriData>,
    ): List<LMFeedFileUploadViewData> {
        return files.map {
            // generates localFilePath from the ContentUri provided by client
            val fileUploadViewData = LMFeedViewDataConvertor.convertFileUploadViewData(it)

            val localFilePath =
                FileUtil.getRealPath(context, it.uri)

            val userPreferences = LMFeedUserPreferences(context)
            val loggedInUUID = userPreferences.getUUID()

            // generates awsFolderPath to upload the file
            val awsFolderPath = generateAWSFolderPathFromFileName(it.mediaName, loggedInUUID)
            val builder = fileUploadViewData.toBuilder().localFilePath(localFilePath.path)
                .awsFolderPath(awsFolderPath)
            when (fileUploadViewData.fileType) {
                IMAGE -> {
                    val dimensions = FileUtil.getImageDimensions(context, fileUploadViewData.uri)
                    builder.width(dimensions.first)
                        .thumbnailUri(fileUploadViewData.uri)
                        .height(dimensions.second)
                        .build()
                }

                VIDEO -> {
                    val thumbnailUri =
                        FileUtil.getVideoThumbnailUri(context, fileUploadViewData.uri)
                    if (thumbnailUri != null) {
                        builder.thumbnailUri(thumbnailUri).build()
                    } else {
                        builder.build()
                    }
                }

                else -> {
                    val thumbnailUri =
                        MediaUtils.getDocumentPreview(context, fileUploadViewData.uri)
                    val format = FileUtil.getFileExtensionFromFileName(fileUploadViewData.mediaName)
                    builder
                        .thumbnailUri(thumbnailUri)
                        .format(format)
                        .build()
                }
            }
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        postId: Long,
        filesCount: Int
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest = LMFeedPostAttachmentUploadWorker.getInstance(postId, filesCount)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    //add post:{} into local db
    private fun storePost(
        uploadData: Pair<WorkContinuation, String>,
        text: String?,
        fileUris: List<LMFeedFileUploadViewData>,
        selectedTopics: ArrayList<LMFeedTopicViewData>?
    ) {
        viewModelScope.launchIO {
            val uuid = uploadData.second
            val temporaryPostId = temporaryPostId ?: -1

            val post = LMFeedViewDataConvertor.convertPost(
                temporaryPostId,
                uuid,
                text,
                fileUris
            )

            val topics = LMFeedViewDataConvertor.convertTopics(selectedTopics?.toList())

            //create add temporary post request
            val request = AddTemporaryPostRequest.Builder()
                .post(post)
                .topics(topics)
                .postThumbnail(fileUris.first().thumbnailUri.toString())
                .build()

            // add it to local db
            lmFeedClient.addTemporaryPost(request)
            _postAdded.postValue(false)
            uploadData.first.enqueue()
        }
    }

    /**
     * @param fileName - Name of the file to be uploaded
     * @return awsFolderPath - Generates and returns AWS folder path where file will be uploaded
     */
    private fun generateAWSFolderPathFromFileName(
        fileName: String?,
        userUniqueId: String?
    ): String {
        val fileNameWithoutExtension = fileName?.substringBeforeLast(".")
        val extension = FileUtil.getFileExtensionFromFileName(fileName)
        return "post/$userUniqueId/" + fileNameWithoutExtension + "-" + System.currentTimeMillis() + "." + extension
    }

    /**
     * Triggers when the user opens post is created successfully
     **/
    private fun sendPostCreationCompletedEvent(
        postText: String?,
        ogTags: LMFeedLinkOGTagsViewData?,
        topics: List<LMFeedTopicViewData>?
    ) {
        val map = hashMapOf<String, String>()
        //todo: member tagging
//        val taggedUsers = MemberTaggingDecoder.decodeAndReturnAllTaggedMembers(postText)

//        if (taggedUsers.isNotEmpty()) {
//            map["user_tagged"] = "yes"
//            map["tagged_users_count"] = taggedUsers.size.toString()
//            val taggedUserIds =
//                taggedUsers.joinToString {
//                    it.first
//                }
//            map["tagged_users_uuid"] = taggedUserIds
//        } else {
//            map["user_tagged"] = "no"
//        }

        if (ogTags != null) {
            map["link_attached"] = "yes"
            map["link"] = ogTags.url ?: ""
        } else {
            map["link_attached"] = "no"
        }

        if (!topics.isNullOrEmpty()) {
            val topicsNameString = topics.joinToString(", ") { it.name }
            map["topics_added"] = "yes"
            map["topics"] = topicsNameString
        } else {
            map["topics_added"] = "no"
        }

        map["image_attached"] = "no"
        map["video_attached"] = "no"
        map["document_attached"] = "no"

        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_CREATION_COMPLETED,
            map
        )
    }

    /**
     * Triggers event when the user clicks on add attachment
     * @param type - type of attachment
     */
    fun sendClickedOnAttachmentEvent(type: String) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.CLICKED_ON_ATTACHMENT,
            mapOf(
                "type" to type
            )
        )
    }

    /**
     * Triggers when the user attaches any media
     * @param data - list of uris od all the attached media
     **/
    fun sendMediaAttachedEvent(data: List<SingleUriData>) {
        // counts number of images in attachments
        val imageCount = data.count {
            it.fileType == IMAGE
        }
        // counts number of videos in attachments
        val videoCount = data.count {
            it.fileType == VIDEO
        }
        // counts number of documents in attachments
        val docsCount = data.count {
            it.fileType == PDF
        }

        // sends image attached event if imageCount > 0
        if (imageCount > 0) {
            sendImageAttachedEvent(imageCount)
        }
        // sends image attached event if videoCount > 0
        if (videoCount > 0) {
            sendVideoAttachedEvent(videoCount)
        }
        // sends image attached event if docsCount > 0
        if (docsCount > 0) {
            sendDocumentAttachedEvent(docsCount)
        }
    }

    /**
     * Triggers when the user attaches image
     * @param imageCount - number of attached images
     **/
    private fun sendImageAttachedEvent(imageCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.IMAGE_ATTACHED_TO_POST,
            mapOf(
                "image_count" to imageCount.toString()
            )
        )
    }

    /**
     * Triggers when the user attaches video
     * @param videoCount - number of attached videos
     **/
    private fun sendVideoAttachedEvent(videoCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.VIDEO_ATTACHED_TO_POST,
            mapOf(
                "video_count" to videoCount.toString()
            )
        )
    }

    /**
     * Triggers when the user attaches document
     * @param documentCount - number of attached documents
     **/
    private fun sendDocumentAttachedEvent(documentCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.DOCUMENT_ATTACHED_TO_POST,
            mapOf(
                "document_count" to documentCount.toString()
            )
        )
    }
}