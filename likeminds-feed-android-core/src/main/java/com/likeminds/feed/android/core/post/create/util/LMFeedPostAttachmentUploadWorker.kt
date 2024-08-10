package com.likeminds.feed.android.core.post.create.util

import android.content.Context
import android.util.Log
import androidx.work.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.likeminds.customgallery.utils.file.util.FileUtil
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.post.model.IMAGE
import com.likeminds.feed.android.core.utils.mediauploader.LMFeedMediaUploadWorker
import com.likeminds.feed.android.core.utils.mediauploader.model.*
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedUploadHelper
import com.likeminds.likemindsfeed.post.model.AttachmentType.CUSTOM_WIDGET
import com.likeminds.likemindsfeed.post.model.Post
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class LMFeedPostAttachmentUploadWorker(
    context: Context,
    workerParams: WorkerParameters
) : LMFeedMediaUploadWorker(context, workerParams) {
    private val postId by lazy { getLongParam(ARG_POST_ID) }
    private val totalMediaCount by lazy { getIntParam(ARG_TOTAL_MEDIA_COUNT) }

    private lateinit var postWithAttachments: Post

    companion object {
        const val ARG_POST_ID = "ARG_POST_ID"
        const val ARG_TOTAL_MEDIA_COUNT = "ARG_TOTAL_MEDIA_COUNT"
        const val TAG = "PostAttachmentUploadWorker"

        fun getInstance(postId: Long, totalMediaCount: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<LMFeedPostAttachmentUploadWorker>()
                .setInputData(
                    workDataOf(
                        ARG_POST_ID to postId,
                        ARG_TOTAL_MEDIA_COUNT to totalMediaCount
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag(TAG)
                .build()
        }
    }

    override fun checkArgs() {
        require(ARG_POST_ID)
        require(ARG_TOTAL_MEDIA_COUNT)
    }

    // gets list of attachments from DB
    override fun init() {
        runBlocking {
            val data = lmFeedClient.getTemporaryPost("-$postId").data ?: return@runBlocking
            postWithAttachments = data.post
        }
    }

    // creates/resumes AWS uploads for each attachment
    override fun uploadFiles(continuation: Continuation<Int>) {
        val attachmentsToUpload = if (failedIndex.isNotEmpty()) {
            postWithAttachments.attachments?.filterIndexed { index, _ ->
                failedIndex.contains(index)
            }
        } else {
            //upload only media attachments
            postWithAttachments.attachments?.filter {
                it.attachmentType != CUSTOM_WIDGET
            }
        }

        if (attachmentsToUpload.isNullOrEmpty()) {
            continuation.resume(WORKER_SUCCESS)
            return
        }

        uploadList = createAWSRequestList(attachmentsToUpload)
        uploadList.forEach { request ->
            val resumeAWSFileResponse =
                LMFeedUploadHelper.getInstance().getAWSFileResponse(request.awsFolderPath)
            if (resumeAWSFileResponse != null) {
                resumeAWSUpload(resumeAWSFileResponse, totalMediaCount, continuation, request)
            } else {
                createAWSUpload(request, totalMediaCount, continuation)
            }
        }
    }

    // resumes AWS file upload
    private fun resumeAWSUpload(
        resumeAWSFileResponse: LMFeedAWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>,
        request: LMFeedGenericFileRequest
    ) {
        val resume = transferUtility.resume(resumeAWSFileResponse.transferObserver!!.id)
        if (resume == null) {
            createAWSUpload(request, totalFilesToUpload, continuation)
        } else {
            setTransferObserver(resumeAWSFileResponse, totalFilesToUpload, continuation)
        }
    }

    // creates and starts AWS upload
    private fun createAWSUpload(
        request: LMFeedGenericFileRequest,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val awsFileResponse =
            uploadFile(request, postWithAttachments.uuid)
        if (awsFileResponse != null) {
            LMFeedUploadHelper.getInstance().addAWSFileResponse(awsFileResponse)
            setTransferObserver(awsFileResponse, totalFilesToUpload, continuation)
        }
    }

    /**
     * Starts Uploading file on AWS.
     * @param request A [LMFeedGenericFileRequest] object
     * @return [LMFeedAWSFileResponse] containing aws transfer utility objects and keys
     */
    private fun uploadFile(
        request: LMFeedGenericFileRequest,
        uuid: String? = null
    ): LMFeedAWSFileResponse? {
        val filePath = request.localFilePath ?: return null
        val file = if (request.fileType == IMAGE) {
            FileUtil.compressFile(applicationContext, filePath)
        } else {
            File(filePath)
        }
        val observer = transferUtility.upload(
            request.awsFolderPath,
            file,
            CannedAccessControlList.PublicRead
        )
        return LMFeedAWSFileResponse.Builder()
            .transferObserver(observer)
            .name(request.name ?: "")
            .awsFolderPath(request.awsFolderPath)
            .index(request.index)
            .fileType(request.fileType)
            .width(request.width)
            .height(request.height)
            .pageCount(request.pageCount)
            .size(request.size)
            .duration(request.duration)
            .uuid(uuid)
            .build()
    }

    // sets a transfer listener to uploading
    private fun setTransferObserver(
        awsFileResponse: LMFeedAWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val observer = awsFileResponse.transferObserver!!
        setProgress(observer.id, observer.bytesTransferred, observer.bytesTotal)
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                onStateChanged(awsFileResponse, state, totalFilesToUpload, continuation)
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                setProgress(id, bytesCurrent, bytesTotal)
            }

            override fun onError(id: Int, ex: Exception?) {
                ex?.printStackTrace()
                failedIndex.add(awsFileResponse.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }
        })
    }

    // onStateChanged listener for AWS file upload
    private fun onStateChanged(
        response: LMFeedAWSFileResponse,
        state: TransferState?,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        if (isStopped) {
            return
        }
        when (state) {
            TransferState.COMPLETED -> {
                LMFeedUploadHelper.getInstance().removeAWSFileResponse(response)
                uploadedCount += 1
                checkWorkerComplete(totalFilesToUpload, continuation)
            }

            TransferState.FAILED -> {
                failedIndex.add(response.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }

            else -> {
                Log.d(LOG_TAG, "state: $state")
            }
        }
    }
}