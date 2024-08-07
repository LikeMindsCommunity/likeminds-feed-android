package com.likeminds.feed.android.core.utils.attachments

import android.util.Base64
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData
import com.likeminds.feed.android.core.post.create.model.LMFeedFileUploadViewData
import com.likeminds.feed.android.core.post.model.LMFeedAttachmentMetaViewData
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findIntOrDefault
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findJSONObjectOrNull
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findLongOrDefault
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.findStringOrDefault
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor.convertPoll
import com.likeminds.feed.android.core.utils.LMFeedViewDataConvertor.convertWidget
import com.likeminds.feed.android.core.utils.mediauploader.utils.LMFeedAWSKeys
import com.likeminds.likemindsfeed.poll.util.PollUtil.getPollMultiSelectStateValue
import com.likeminds.likemindsfeed.poll.util.PollUtil.getPollTypeValue
import com.likeminds.likemindsfeed.sdk.model.User
import com.likeminds.likemindsfeed.widgets.model.Widget
import org.json.JSONArray
import org.json.JSONObject

object LMFeedAttachmentsUtil {
    const val NAME_KEY = "name"
    const val URL_KEY = "url"
    const val SIZE_KEY = "size"
    const val HEIGHT_KEY = "height"
    const val WIDTH_KEY = "width"
    const val DURATION_KEY = "duration"
    const val PAGE_COUNT_KEY = "page_count"
    const val FORMAT_KEY = "format"
    const val OG_TAG_KEY = "og_tags"
    const val TITLE_KEY = "title"
    const val DESCRIPTION_KEY = "description"
    const val IMAGE_KEY = "image"
    const val ENTITY_ID_KEY = "entity_id"

    const val POLL_ALLOW_ADD_OPTION_KEY = "allow_add_option"
    const val POLL_TYPE_KEY = "poll_type"
    const val POLL_MULTIPLE_SELECT_STATE_KEY = "multiple_select_state"
    const val POLL_MULTIPLE_SELECT_NUMBER_KEY = "multiple_select_number"
    const val POLL_EXPIRY_TIME_KEY = "expiry_time"
    const val POLL_IS_ANONYMOUS_KEY = "is_anonymous"
    const val POLL_OPTIONS_KEYS = "options"

    const val AWS_FOLDER_PATH_KEY = "aws_folder_path"
    const val LOCAL_FILE_PATH_KEY = "local_file_path"
    const val URI_KEY = "uri"

    fun convertAttachmentMetaToImage(attachmentMeta: JSONObject): LMFeedAttachmentMetaViewData {
        //get all the required data
        val name = attachmentMeta.findStringOrDefault(NAME_KEY, "")
        val url = attachmentMeta.findStringOrDefault(URL_KEY, "")
        val size = attachmentMeta.findLongOrDefault(SIZE_KEY, 0L)
        val height = attachmentMeta.findIntOrDefault(HEIGHT_KEY, 0)
        val width = attachmentMeta.findIntOrDefault(WIDTH_KEY, 0)

        // create object and return
        return LMFeedAttachmentMetaViewData.Builder()
            .name(name)
            .url(url)
            .size(size)
            .height(height)
            .width(width)
            .build()
    }

    fun convertImageToJSONObject(attachmentMeta: LMFeedAttachmentMetaViewData): JSONObject {
        return JSONObject().apply {
            put(NAME_KEY, attachmentMeta.name)
            put(URL_KEY, attachmentMeta.url)
            put(SIZE_KEY, attachmentMeta.size)
            put(HEIGHT_KEY, attachmentMeta.height)
            put(WIDTH_KEY, attachmentMeta.width)
        }
    }

    fun convertAttachmentMetaToVideo(attachmentMeta: JSONObject): LMFeedAttachmentMetaViewData {
        //get all the required data
        val duration = attachmentMeta.findIntOrDefault(DURATION_KEY, 0)
        val name = attachmentMeta.findStringOrDefault(NAME_KEY, "")
        val url = attachmentMeta.findStringOrDefault(URL_KEY, "")
        val size = attachmentMeta.findLongOrDefault(SIZE_KEY, 0L)
        val height = attachmentMeta.findIntOrDefault(HEIGHT_KEY, 0)
        val width = attachmentMeta.findIntOrDefault(WIDTH_KEY, 0)

        // create object and return
        return LMFeedAttachmentMetaViewData.Builder()
            .duration(duration)
            .name(name)
            .url(url)
            .size(size)
            .height(height)
            .width(width)
            .build()
    }

    fun convertVideoToJSONObject(attachmentMeta: LMFeedAttachmentMetaViewData): JSONObject {
        return JSONObject().apply {
            put(DURATION_KEY, attachmentMeta.duration)
            put(NAME_KEY, attachmentMeta.name)
            put(URL_KEY, attachmentMeta.url)
            put(SIZE_KEY, attachmentMeta.size)
            put(HEIGHT_KEY, attachmentMeta.height)
            put(WIDTH_KEY, attachmentMeta.width)
        }
    }

    fun convertAttachmentMetaToDocument(attachmentMeta: JSONObject): LMFeedAttachmentMetaViewData {
        //get all the required data
        val format = attachmentMeta.findStringOrDefault(FORMAT_KEY, "")
        val name = attachmentMeta.findStringOrDefault(NAME_KEY, "")
        val pageCount = attachmentMeta.findIntOrDefault(PAGE_COUNT_KEY, 0)
        val size = attachmentMeta.findLongOrDefault(SIZE_KEY, 0L)
        val url = attachmentMeta.findStringOrDefault(URL_KEY, "")

        // create object and return
        return LMFeedAttachmentMetaViewData.Builder()
            .format(format)
            .name(name)
            .pageCount(pageCount)
            .size(size)
            .url(url)
            .build()
    }

    fun convertDocumentToJSONObject(attachmentMeta: LMFeedAttachmentMetaViewData): JSONObject {
        return JSONObject().apply {
            put(FORMAT_KEY, attachmentMeta.format)
            put(PAGE_COUNT_KEY, attachmentMeta.pageCount)
            put(NAME_KEY, attachmentMeta.name)
            put(SIZE_KEY, attachmentMeta.size)
            put(URL_KEY, attachmentMeta.url)
        }
    }

    fun convertAttachmentMetaToLinkOGTag(attachmentMeta: JSONObject): LMFeedAttachmentMetaViewData {
        val ogTags = attachmentMeta.findJSONObjectOrNull(OG_TAG_KEY)
        return if (ogTags != null) {
            val title = ogTags.findStringOrDefault(TITLE_KEY, "")
            val image = ogTags.findStringOrDefault(IMAGE_KEY, "")
            val description = ogTags.findStringOrDefault(DESCRIPTION_KEY, "")
            val url = ogTags.findStringOrDefault(URL_KEY, "")

            LMFeedAttachmentMetaViewData.Builder()
                .ogTags(
                    LMFeedLinkOGTagsViewData.Builder()
                        .title(title)
                        .image(image)
                        .description(description)
                        .url(url)
                        .build()
                )
                .build()
        } else {
            LMFeedAttachmentMetaViewData.Builder()
                .build()
        }
    }

    fun convertLinkOGTagToJSONObject(ogTags: LMFeedLinkOGTagsViewData): JSONObject {
        val ogTagJSONObject = JSONObject().apply {
            put(TITLE_KEY, ogTags.title)
            put(IMAGE_KEY, ogTags.image)
            put(DESCRIPTION_KEY, ogTags.description)
            put(URL_KEY, ogTags.url)
        }
        return JSONObject().apply {
            put(OG_TAG_KEY, ogTagJSONObject)
        }
    }

    fun convertAttachmentMetaToCustomWidget(
        attachmentMeta: JSONObject,
        widgetsMap: Map<String, Widget>
    ): LMFeedAttachmentMetaViewData {
        val entityId = attachmentMeta.findStringOrDefault(ENTITY_ID_KEY, "")
        val widget = widgetsMap[entityId]
        val widgetViewData = convertWidget(widget)
        return LMFeedAttachmentMetaViewData.Builder()
            .widgetViewData(widgetViewData)
            .build()
    }

    fun convertCustomWidgetToJSONObject(attachmentMeta: LMFeedAttachmentMetaViewData): JSONObject {
        return JSONObject().apply {
            //todo add logic
        }
    }

    fun convertAttachmentMetaToPoll(
        attachmentMeta: JSONObject,
        widgetsMap: Map<String, Widget>,
        usersMap: Map<String, User>
    ): LMFeedAttachmentMetaViewData {
        val entityId = attachmentMeta.findStringOrDefault(ENTITY_ID_KEY, "")
        return LMFeedAttachmentMetaViewData.Builder()
            .poll(
                convertPoll(
                    entityId,
                    usersMap,
                    widgetsMap
                )
            )
            .build()
    }

    fun convertPollToJSONObject(poll: LMFeedPollViewData): JSONObject {
        return JSONObject().apply {
            if (poll.id.isNotEmpty()) {
                put(ENTITY_ID_KEY, poll.id)
            }

            put(TITLE_KEY, poll.title)
            put(POLL_EXPIRY_TIME_KEY, poll.expiryTime)

            val pollOptionsTexts = poll.options.map { it.text }
            val options = JSONArray(pollOptionsTexts)
            put(POLL_OPTIONS_KEYS, options)

            put(POLL_ALLOW_ADD_OPTION_KEY, poll.allowAddOption)
            put(
                POLL_MULTIPLE_SELECT_STATE_KEY,
                poll.multipleSelectState.getPollMultiSelectStateValue()
            )
            put(POLL_MULTIPLE_SELECT_NUMBER_KEY, poll.multipleSelectNumber)
            put(POLL_IS_ANONYMOUS_KEY, poll.isAnonymous)
            put(POLL_TYPE_KEY, poll.pollType.getPollTypeValue())
        }
    }

    fun convertFileUploadToJSONObject(fileUri: LMFeedFileUploadViewData): JSONObject {
        return JSONObject().apply {
            val url = String(
                Base64.decode(
                    LMFeedAWSKeys.getBucketBaseUrl(),
                    Base64.DEFAULT
                )
            ) + fileUri.awsFolderPath

            put(URL_KEY, url)
            put(FORMAT_KEY, fileUri.format)
            put(AWS_FOLDER_PATH_KEY, fileUri.awsFolderPath)
            put(LOCAL_FILE_PATH_KEY, fileUri.localFilePath)
            put(URI_KEY, fileUri.uri)
            put(NAME_KEY, fileUri.mediaName)
            put(PAGE_COUNT_KEY, fileUri.pdfPageCount)
            put(SIZE_KEY, fileUri.size)
            put(HEIGHT_KEY, fileUri.height)
            put(WIDTH_KEY, fileUri.width)
            put(DURATION_KEY, fileUri.duration)
        }
    }
}