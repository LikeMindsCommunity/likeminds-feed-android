package com.likeminds.feed.android.core.utils.mediauploader

import android.content.Context
import androidx.work.*
import com.likeminds.feed.android.core.LMFeedCoreApplication
import com.likeminds.feed.android.core.utils.mediauploader.model.*
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedWorkerUtil.getIntOrNull
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedWorkerUtil.getLongOrNull
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.Attachment
import com.likeminds.likemindsfeed.post.util.AttachmentUtil.getAttachmentValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.*

abstract class LMFeedMediaUploadWorker(
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    protected val transferUtility by lazy { LMFeedCoreApplication.getTransferUtility(appContext) }
    protected val lmFeedClient by lazy { LMFeedClient.getInstance() }

    private val progressMap by lazy { HashMap<Int, Pair<Long, Long>>() }
    protected var uploadedCount = 0
    protected val failedIndex by lazy { ArrayList<Int>() }
    protected lateinit var uploadList: ArrayList<LMFeedGenericFileRequest>

    abstract fun checkArgs()
    abstract fun init()
    abstract fun uploadFiles(continuation: Continuation<Int>)

    companion object {
        const val ARG_MEDIA_INDEX_LIST = "ARG_MEDIA_INDEX_LIST"
        const val ARG_PROGRESS = "ARG_PROGRESS"

        fun getProgress(workInfo: WorkInfo): Pair<Long, Long>? {
            val progress = workInfo.progress.getLongArray(ARG_PROGRESS)
            if (progress == null || progress.size != 2) {
                return null
            }
            return Pair(progress[0], progress[1])
        }
    }

    override suspend fun doWork(): Result {
        try {
            checkArgs()
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return withContext(Dispatchers.IO) {
            val result = suspendCoroutine<Int> { continuation ->
                uploadFiles(continuation)
            }
            return@withContext when (result) {
                WORKER_SUCCESS -> {
                    Result.success()
                }

                WORKER_RETRY -> {
                    Result.retry()
                }

                WORKER_FAILURE -> {
                    getFailureResult(failedIndex.toIntArray())
                }

                else -> {
                    getFailureResult(failedIndex.toIntArray())
                }
            }
        }
    }

    private fun getFailureResult(failedArrayIndex: IntArray = IntArray(0)): Result {
        return Result.failure(
            Data.Builder()
                .putIntArray(ARG_MEDIA_INDEX_LIST, failedArrayIndex)
                .build()
        )
    }

    protected fun setProgress(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        progressMap[id] = Pair(bytesCurrent, bytesTotal)
        var averageBytesCurrent = 0L
        var averageBytesTotal = 0L
        progressMap.values.forEach {
            averageBytesCurrent += it.first
            averageBytesTotal += it.second
        }
        if (averageBytesCurrent > 0L && averageBytesTotal > 0L) {
            setProgressAsync(
                Data.Builder()
                    .putLongArray(ARG_PROGRESS, longArrayOf(averageBytesCurrent, averageBytesTotal))
                    .build()
            )
        }
    }

    fun require(key: String) {
        if (!containsParam(key)) {
            throw Error("$key is required")
        }
    }

    protected fun getLongParam(key: String): Long {
        return params.inputData.getLongOrNull(key)
            ?: throw Error("$key is required")
    }

    protected fun getIntParam(key: String): Int {
        return params.inputData.getIntOrNull(key)
            ?: throw Error("$key is required")
    }

    private fun containsParam(key: String): Boolean {
        return params.inputData.keyValueMap.containsKey(key)
    }

    protected fun createAWSRequestList(
        attachmentsToUpload: List<Attachment>?
    ): ArrayList<LMFeedGenericFileRequest> {
        val awsFileRequestList = ArrayList<LMFeedGenericFileRequest>()
        attachmentsToUpload?.mapIndexed { index, attachment ->
            val attachmentMeta = attachment.attachmentMeta
            val request = LMFeedGenericFileRequest.Builder()
                .name(attachmentMeta.name)
                .fileType(attachment.attachmentType.getAttachmentValue())
                .awsFolderPath(attachmentMeta.awsFolderPath!!)
                .localFilePath(attachmentMeta.localFilePath)
                .index(index)
                .pageCount(attachmentMeta.pageCount)
                .duration(attachmentMeta.duration)
                .size(attachmentMeta.size)
                .build()
            awsFileRequestList.add(request)
        }
        return awsFileRequestList
    }

    protected fun checkWorkerComplete(
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        if (totalFilesToUpload == uploadedCount + failedIndex.size) {
            if (totalFilesToUpload == uploadedCount) {
                continuation.resume(WORKER_SUCCESS)
            } else {
                continuation.resume(WORKER_FAILURE)
            }
        }
    }
}