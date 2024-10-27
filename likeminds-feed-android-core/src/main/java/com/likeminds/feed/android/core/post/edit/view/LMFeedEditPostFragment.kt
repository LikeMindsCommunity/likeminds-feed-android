package com.likeminds.feed.android.core.post.edit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feed.android.core.*
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.databinding.LmFeedFragmentEditPostBinding
import com.likeminds.feed.android.core.databinding.LmFeedItemMultipleMediaVideoBinding
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData
import com.likeminds.feed.android.core.post.create.util.LMFeedCreateEditPostViewStyleUtil
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostDisabledTopicsDialogExtras
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity.Companion.LM_FEED_EDIT_POST_EXTRAS
import com.likeminds.feed.android.core.post.edit.viewmodel.LMFeedEditPostViewModel
import com.likeminds.feed.android.core.post.model.*
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.socialfeed.adapter.LMFeedPostAdapterListener
import com.likeminds.feed.android.core.socialfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.socialfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.socialfeed.util.LMFeedPostBinderUtils.customizePostTopicsGroup
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionExtras
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.theme.LMFeedAppearance
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.poll.view.LMFeedPostPollView
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.view.LMFeedPostHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.*
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getUrlIfExist
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics.LMFeedScreenNames
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.base.model.*
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.membertagging.MemberTaggingUtil
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import com.likeminds.usertagging.UserTagging
import com.likeminds.usertagging.model.TagUser
import com.likeminds.usertagging.model.UserTaggingConfig
import com.likeminds.usertagging.util.UserTaggingDecoder
import com.likeminds.usertagging.util.UserTaggingViewListener
import com.likeminds.usertagging.view.UserTaggingSuggestionListView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.json.JSONObject

open class LMFeedEditPostFragment :
    Fragment(),
    LMFeedPostAdapterListener {

    private lateinit var binding: LmFeedFragmentEditPostBinding
    private lateinit var editPostExtras: LMFeedEditPostExtras

    private var postMediaViewData: LMFeedMediaViewData? = null
    private var ogTags: LMFeedLinkOGTagsViewData? = null
    private var poll: LMFeedPollViewData? = null
    private var widgetData: Pair<String, JSONObject>? = null

    private var post: LMFeedPostViewData? = null

    private lateinit var etPostTextChangeListener: TextWatcher
    private lateinit var memberTagging: UserTaggingSuggestionListView

    private val editPostViewModel: LMFeedEditPostViewModel by viewModels()

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

    // [postPublisher] to publish changes in the post
    private val postEvent by lazy {
        LMFeedPostEvent.getPublisher()
    }

    private val selectedTopic by lazy {
        HashMap<String, LMFeedTopicViewData>()
    }

    private val disabledTopics by lazy {
        HashMap<String, LMFeedTopicViewData>()
    }

    companion object {
        const val TAG = "LMFeedEditPostFragment"

        fun getInstance(editPostExtras: LMFeedEditPostExtras): LMFeedEditPostFragment {
            val editPostFragment = LMFeedEditPostFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_EDIT_POST_EXTRAS, editPostExtras)
            editPostFragment.arguments = bundle
            return editPostFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //receive extras
        receiveExtras()
    }

    private fun receiveExtras() {
        if (arguments == null || arguments?.containsKey(LM_FEED_EDIT_POST_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        editPostExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_EDIT_POST_EXTRAS,
            LMFeedEditPostExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentEditPostBinding.inflate(layoutInflater)

        binding.apply {
            customizeEditPostHeaderView(headerViewEditPost)
            customizePostHeaderView(postHeader)
            customizePostTopicsGroup(postTopicsGroup)
            customizePostComposer(etPostComposer)
            customizePostImageAttachment(singleImageAttachment.postImageView)
            customizePostVideoAttachment(singleVideoAttachment.postVideoView)
            customizePostLinkViewAttachment(linkPreview.postLinkView)
            customizePostDocumentsAttachment(documentsAttachment.postDocumentsMediaView)
            customizePostMultipleMedia(multipleMediaAttachment.multipleMediaView)
            customizeEditPostProgressbar(progressBar.progressView)
            customizePostPollAttachmentView(pollView)
            customizePostHeadingComposer(etPostHeadingComposer)
            customizePostHeadingLimit(tvHeadingLimit)

            return binding.root
        }
    }

    //customizes edit post header view
    protected open fun customizeEditPostHeaderView(headerViewEditPost: LMFeedHeaderView) {
        headerViewEditPost.apply {
            setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.headerViewStyle)

            setTitleText(
                getString(
                    R.string.lm_feed_edit_s,
                    LMFeedCommunityUtil.getPostVariable()
                        .pluralizeOrCapitalize(LMFeedWordAction.FIRST_LETTER_CAPITAL_SINGULAR)
                )
            )

            setSubmitText(getString(R.string.lm_feed_save))
            setSubmitButtonEnabled(true)
        }
    }

    //customizes the header view of the post
    protected open fun customizePostHeaderView(postHeader: LMFeedPostHeaderView) {
        postHeader.apply {
            setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.postHeaderViewStyle)
        }
    }

    //customizes post composer edit text
    protected open fun customizePostComposer(etPostComposer: LMFeedEditText) {
        // sets the hint to the post composer edit text
        val hint = when (LMFeedCoreApplication.selectedTheme) {
            LMFeedTheme.QNA_FEED -> {
                getString(R.string.lm_feed_add_description)
            }

            else -> {
                getString(R.string.lm_feed_write_something_here)
            }
        }
        etPostComposer.hint = hint

        etPostComposer.setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.postComposerStyle)
    }

    //customizes single image view type post
    protected open fun customizePostImageAttachment(postSingleImageView: LMFeedPostImageMediaView) {
        val postImageMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle
                ?: return

        postSingleImageView.setStyle(postImageMediaStyle)
    }

    //customizes single video view type post
    protected open fun customizePostVideoAttachment(postSingleVideoView: LMFeedPostVideoMediaView) {
        val postVideoMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle
                ?: return

        postSingleVideoView.setStyle(postVideoMediaStyle)
    }

    //customizes link preview in the post
    protected open fun customizePostLinkViewAttachment(linkMediaView: LMFeedPostLinkMediaView) {
        val linkAttachmentViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postLinkViewStyle

        val updatedLinkAttachmentViewStyle = linkAttachmentViewStyle?.toBuilder()
            ?.linkRemoveIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_cross)
                    .build()
            )
            ?.build()

        updatedLinkAttachmentViewStyle?.let {
            linkMediaView.setStyle(it)
        }
    }

    //customizes documents type post
    protected open fun customizePostDocumentsAttachment(postDocumentsView: LMFeedPostDocumentsMediaView) {
        val postDocumentsViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postDocumentsMediaStyle
                ?: return

        postDocumentsView.setStyle(postDocumentsViewStyle)
    }

    //customizes multiple media type post
    protected open fun customizePostMultipleMedia(postMultipleMediaView: LMFeedPostMultipleMediaView) {
        val postMultipleMediaViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postMultipleMediaStyle
                ?: return

        postMultipleMediaView.setStyle(postMultipleMediaViewStyle)
    }

    //customizes progress bar in the edit post
    protected open fun customizeEditPostProgressbar(progressBar: LMFeedProgressBar) {
        progressBar.setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.progressBarStyle)
    }

    //customizes poll view in the post
    protected open fun customizePostPollAttachmentView(pollView: LMFeedPostPollView) {
        val updatedPollStyles =
            LMFeedCreateEditPostViewStyleUtil.getUpdatedPollViewStyles(isCreateFlow = false)
        pollView.setStyle(updatedPollStyles)
    }

    //customize post heading composer edit text
    protected open fun customizePostHeadingComposer(etPostHeadingComposer: LMFeedEditText) {
        etPostHeadingComposer.apply {
            val postHeadingComposerStyle =
                LMFeedStyleTransformer.editPostFragmentViewStyle.postHeadingComposerStyle

            if (postHeadingComposerStyle != null) {
                /** sets the ime options to [IME_ACTION_NEXT] and raw input type to [TYPE_TEXT_FLAG_CAP_SENTENCES]
                 * to provide the Next button on keypad and start the sentence with capital letter
                 */
                imeOptions = EditorInfo.IME_ACTION_NEXT
                setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)

                setStyle(postHeadingComposerStyle)
                LMFeedViewUtils.getMandatoryAsterisk(
                    getString(
                        R.string.lm_feed_add_your_s_here,
                        LMFeedCommunityUtil.getPostVariable()
                            .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
                    ),
                    etPostHeadingComposer
                )
                show()
                binding.headingSeparator.show()
            } else {
                hide()
                binding.headingSeparator.hide()
            }
        }
    }

    //customize post heading limit text
    protected open fun customizePostHeadingLimit(tvPostHeadingLimitTextView: LMFeedTextView) {
        tvPostHeadingLimitTextView.apply {
            val postHeadingLimitTextViewStyle =
                LMFeedStyleTransformer.editPostFragmentViewStyle.postHeadingLimitTextStyle

            if (postHeadingLimitTextViewStyle != null) {
                binding.etPostHeadingComposer.filters =
                    arrayOf(InputFilter.LengthFilter(LMFeedAppearance.getPostHeadingLimit()))

                setStyle(postHeadingLimitTextViewStyle)
                setPostHeadingLimitText(this, 0)
                show()
            } else {
                hide()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMemberTagging()
        initListeners()
        fetchData()
        observeData()
    }

    //initializes member tagging view
    private fun initMemberTagging() {
        memberTagging = binding.userTaggingView

        val listener = object : UserTaggingViewListener {
            override fun callApi(page: Int, searchName: String) {
                editPostViewModel.getMembersForTagging(page, searchName)
            }

            override fun onUserTagged(user: TagUser) {
                super.onUserTagged(user)
                LMFeedAnalytics.sendUserTagEvent(
                    user.uuid,
                    memberTagging.getTaggedMemberCount(),
                    LMFeedScreenNames.EDIT_POST
                )
            }
        }

        val config = UserTaggingConfig.Builder()
            .editText(binding.etPostComposer)
            .maxHeightInPercentage(0.4f)
            .color(LMFeedAppearance.getTextLinkColor())
            .hasAtRateSymbol(true)
            .build()

        UserTagging.initialize(memberTagging, config, listener)
    }

    private fun initListeners() {
        binding.apply {
            //initializes post done button click listener
            headerViewEditPost.setSubmitButtonClickListener {
                onSavePostClicked()
            }

            headerViewEditPost.setNavigationIconClickListener {
                onNavigationIconClick()
            }
        }
    }

    //processes the save post click
    protected open fun onSavePostClicked() {
        //check all the necessary conditions before saving a post
        binding.apply {
            val text = etPostComposer.text
            val updatedText = memberTagging.replaceSelectedMembers(text).trim()
            val topics = selectedTopic.values

            val updatedHeading = etPostHeadingComposer.text?.trim().toString()

            //get media attachments from post
            val mediaAttachments = postMediaViewData?.attachments?.filter {
                it.attachmentType != CUSTOM_WIDGET
            }

            if (selectedTopic.isNotEmpty()) {
                if (disabledTopics.isEmpty()) {
                    //call api as all topics are enabled
                    headerViewEditPost.setSubmitButtonEnabled(isEnabled = true, showProgress = true)

                    editPostViewModel.editPost(
                        editPostExtras.postId,
                        updatedText,
                        updatedHeading,
                        attachments = mediaAttachments,
                        ogTags = ogTags,
                        selectedTopics = topics.toList(),
                        poll = poll,
                        widgetData = widgetData
                    )
                } else {
                    //show dialog for disabled topics
                    showDisabledTopicsAlert(disabledTopics.values.toList())
                }
            } else {
                //call api as no topics are enabled
                headerViewEditPost.setSubmitButtonEnabled(isEnabled = true, showProgress = true)

                editPostViewModel.editPost(
                    editPostExtras.postId,
                    updatedText,
                    updatedHeading,
                    attachments = mediaAttachments,
                    ogTags = ogTags,
                    selectedTopics = topics.toList(),
                    poll = poll,
                    widgetData = widgetData
                )
            }
        }
    }

    //processes the navigation icon click
    protected open fun onNavigationIconClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    //show alert for disabled topics
    private fun showDisabledTopicsAlert(disabledTopics: List<LMFeedTopicViewData>) {
        val noOfDisabledTopics = disabledTopics.size

        //create message string
        val topicNameString = disabledTopics.joinToString(", ") { it.name }
        val firstLineMessage = resources.getQuantityString(
            R.plurals.lm_feed_topic_disabled_message_s,
            noOfDisabledTopics,
            LMFeedCommunityUtil.getPostVariable()
                .pluralizeOrCapitalize(LMFeedWordAction.ALL_SMALL_SINGULAR)
        )
        val finalMessage = "$firstLineMessage \n $topicNameString"

        val extras = LMFeedEditPostDisabledTopicsDialogExtras.Builder()
            .title(
                resources.getQuantityString(
                    R.plurals.lm_feed_topic_disabled,
                    noOfDisabledTopics,
                    noOfDisabledTopics
                )
            )
            .subtitle(finalMessage)
            .build()

        LMFeedEditPostDisabledTopicsDialogFragment.showDialog(childFragmentManager, extras)
    }

    private fun fetchData() {
        LMFeedProgressBarHelper.showProgress(binding.progressBar)
        editPostViewModel.getPost(editPostExtras.postId)
    }

    private fun observeData() {
        editPostViewModel.errorEventFlow.onEach { response ->
            when (response) {
                is LMFeedEditPostViewModel.ErrorMessageEvent.EditPost -> {
                    binding.headerViewEditPost.setSubmitButtonEnabled(
                        isEnabled = true,
                        showProgress = false
                    )
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedEditPostViewModel.ErrorMessageEvent.GetPost -> {
                    requireActivity().apply {
                        LMFeedViewUtils.showErrorMessageToast(this, response.errorMessage)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }

                is LMFeedEditPostViewModel.ErrorMessageEvent.GetTopic -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedEditPostViewModel.ErrorMessageEvent.DecodeUrl -> {
                    val postText = binding.etPostComposer.text.toString()
                    val link = postText.getUrlIfExist()
                    if (link != ogTags?.url) {
                        clearPreviewLink()
                    }
                }

                is LMFeedEditPostViewModel.ErrorMessageEvent.TaggingList -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.errorMessage
                    )
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

        editPostViewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }

        editPostViewModel.showTopicFilter.observe(viewLifecycleOwner) { showTopics ->
            if (showTopics) {
                handleTopicSelectionView(true)
                initTopicSelectionView()
            } else {
                handleTopicSelectionView(false)
            }
        }

        // observes decodeUrlResponse and returns link ogTags
        editPostViewModel.decodeUrlResponse.observe(viewLifecycleOwner) { ogTags ->
            this.ogTags = ogTags
            showLinkView()
        }

        editPostViewModel.postDataEventFlow.onEach { response ->
            when (response) {
                is LMFeedEditPostViewModel.PostDataEvent.EditPost -> {
                    // updated post from editPost response

                    val post = response.post

                    // notifies the subscribers about the change in post data
                    postEvent.notify(Pair(post.id, post))

                    requireActivity().apply {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }

                is LMFeedEditPostViewModel.PostDataEvent.GetPost -> {
                    // post from getPost response
                    setPostData(response.post)
                    post = response.post
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // sets the post data in view
    private fun setPostData(post: LMFeedPostViewData) {
        binding.apply {
            initAuthorFrame(post.headerViewData.user)

            val mediaViewData = post.mediaViewData
            val topics = post.topicsViewData

            //get custom widget attachment
            val customWidgetAttachment = mediaViewData.attachments.firstOrNull {
                it.attachmentType == CUSTOM_WIDGET
            }?.attachmentMeta?.widgetViewData

            //get widget data id and metadata
            widgetData = if (customWidgetAttachment != null) {
                val entityId = customWidgetAttachment.id
                val data = customWidgetAttachment.metadata.optJSONObject("meta") ?: JSONObject()
                Pair(
                    entityId,
                    data
                )
            } else {
                null
            }

            LMFeedProgressBarHelper.hideProgress(progressBar)
            nestedScroll.show()

            // decodes the post text and sets to the edit text
            UserTaggingDecoder.decode(
                etPostComposer,
                post.contentViewData.text,
                ContextCompat.getColor(requireContext(), LMFeedAppearance.getTextLinkColor())
            )

            // sets the heading to the heading edit text
            val heading = post.contentViewData.heading
            if (!heading.isNullOrEmpty()) {
                etPostHeadingComposer.setText(heading)
            }

            // sets the cursor to the end and opens keyboard
            etPostComposer.setSelection(etPostComposer.length())
            etPostComposer.focusAndShowKeyboard()

            when (post.viewType) {
                ITEM_POST_SINGLE_IMAGE -> {
                    postMediaViewData = mediaViewData
                    showSingleImagePreview()
                }

                ITEM_POST_SINGLE_VIDEO -> {
                    postMediaViewData = mediaViewData
                    showSingleVideoPreview()
                }

                ITEM_POST_DOCUMENTS -> {
                    postMediaViewData = mediaViewData
                    showDocumentsPreview()
                }

                ITEM_POST_MULTIPLE_MEDIA -> {
                    postMediaViewData = mediaViewData
                    showMultiMediaPreview()
                }

                ITEM_POST_LINK -> {
                    postMediaViewData = mediaViewData
                    ogTags = postMediaViewData?.attachments?.firstOrNull {
                        it.attachmentType == LINK
                    }?.attachmentMeta?.ogTags
                    showLinkView()
                }

                ITEM_POST_POLL -> {
                    val pollAttachment =
                        post.mediaViewData.attachments.firstOrNull()?.attachmentMeta?.poll ?: return
                    val options = pollAttachment.options
                    val updatedOptions = options.map {
                        it.toBuilder().toShowResults(false).build()
                    }

                    poll = pollAttachment.toBuilder()
                        .toShowResults(false)
                        .options(updatedOptions)
                        .build()

                    showPollView()
                }

                ITEM_POST_CUSTOM_WIDGET -> {

                }

                else -> {
                    Log.e(LOG_TAG, "invalid view type")
                }
            }

            if (topics.isNotEmpty()) {
                handleTopicSelectionView(true)

                selectedTopic.clear()
                disabledTopics.clear()

                //filter disabled topics
                topics.forEach { topic ->
                    if (!topic.isEnabled) {
                        disabledTopics[topic.id] = topic
                    }
                }
                addTopicsToGroup(false, topics)
            } else {
                editPostViewModel.getAllTopics()
            }

            initPostComposerTextListener()
            initPostHeadingComposerTextListener()
        }
    }

    //sets data to the author frame
    private fun initAuthorFrame(user: LMFeedUserViewData) {
        binding.postHeader.apply {
            setAuthorName(user.name)
            setAuthorImage(user)
        }
    }

    // adds text watcher on post content edit text
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun initPostComposerTextListener() {
        binding.etPostComposer.apply {
            /**
             * As the scrollable edit text is inside a scroll view,
             * this touch listener handles the scrolling of the edit text.
             * When the edit text is touched and has focus then it disables scroll of scroll-view.
             */
            setOnTouchListener(View.OnTouchListener { v, event ->
                if (hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })

            val postContainsOGTags = postMediaViewData?.attachments?.find {
                it.attachmentType == LINK
            } != null

            if (postContainsOGTags && poll == null) {
                // text watcher with debounce to add delay in api calls for ogTags
                textChanges()
                    .debounce(500)
                    .distinctUntilChanged()
                    .onEach {
                        val text = it?.toString()?.trim()
                        if (!text.isNullOrEmpty()) {
                            getLinkPreview(text)
                        }
                    }
                    .launchIn(lifecycleScope)
            }

            // text watcher to handleSaveButton click-ability
            addTextChangedListener {
                handleSubmitButtonVisibility()
            }
        }
    }

    // adds text change listener on post heading edit text
    @SuppressLint("ClickableViewAccessibility")
    private fun initPostHeadingComposerTextListener() {
        binding.etPostHeadingComposer.apply {
            /**
             * As the scrollable edit text is inside a scroll view,
             * this touch listener handles the scrolling of the edit text.
             * When the edit text is touched and has focus then it disables scroll of scroll-view.
             */
            setOnTouchListener(View.OnTouchListener { v, event ->
                if (hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })

            addTextChangedListener {
                val text = it?.toString()?.trim()
                if (text != null) {
                    setPostHeadingLimitText(binding.tvHeadingLimit, text.length)
                }

                handleSubmitButtonVisibility()
            }
        }
    }

    //sets the heading limit text to the heading limit text view
    protected open fun setPostHeadingLimitText(
        headingTextView: LMFeedTextView,
        headingTextLength: Int
    ) {
        headingTextView.text = getString(
            R.string.lm_feed_heading_limit_text_d,
            headingTextLength,
            LMFeedAppearance.getPostHeadingLimit()
        )
    }

    /**
     * Adds TextWatcher to edit text with Flow operators
     * **/
    @ExperimentalCoroutinesApi
    @CheckResult
    fun EditText.textChanges(): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            etPostTextChangeListener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (this@callbackFlow).trySend(s.toString())
                }
            }
            addTextChangedListener(etPostTextChangeListener)
            awaitClose { removeTextChangedListener(etPostTextChangeListener) }
        }.onStart { emit(text) }
    }

    //fetches link preview for link post type
    private fun getLinkPreview(text: String?) {
        binding.linkPreview.apply {
            if (text.isNullOrEmpty()) {
                clearPreviewLink()
                return
            }
            val link = text.getUrlIfExist()
            if (ogTags != null && link.equals(ogTags?.url)) {
                return
            }
            if (!link.isNullOrEmpty()) {
                if (link == ogTags?.url) {
                    return
                }
                clearPreviewLink()
                editPostViewModel.decodeUrl(link)
            } else {
                clearPreviewLink()
            }
        }
    }

    //handles topics chip group and separator line
    private fun handleTopicSelectionView(showView: Boolean) {
        binding.apply {
            postTopicsGroup.isVisible = showView
            topicSeparator.isVisible = showView
        }
    }

    //start activity -> Topic Selection and check for result with selected topics
    private val topicSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = result.data?.extras
                val resultExtras = LMFeedExtrasUtil.getParcelable(
                    bundle,
                    LMFeedTopicSelectionActivity.LM_FEED_TOPIC_SELECTION_RESULT_EXTRAS,
                    LMFeedTopicSelectionResultExtras::class.java
                ) ?: return@registerForActivityResult

                val selectedTopics = resultExtras.selectedTopics
                if (selectedTopics.isNotEmpty()) {
                    addTopicsToGroup(true, selectedTopics)
                }
            }
        }

    //init initial topic selection view with "Select Topic Chip"
    private fun initTopicSelectionView() {
        binding.postTopicsGroup.apply {
            removeAllChips()
            addChip(
                getString(R.string.lm_feed_select_topics),
                LMFeedStyleTransformer.editPostFragmentViewStyle.selectTopicsChipStyle
            ) {
                val extras = LMFeedTopicSelectionExtras.Builder()
                    .showAllTopicFilter(false)
                    .showEnabledTopicOnly(true)
                    .build()
                val intent = LMFeedTopicSelectionActivity.getIntent(context, extras)

                topicSelectionLauncher.launch(intent)
            }
        }
    }

    //add selected topics to group and add edit chip as well in the end
    private fun addTopicsToGroup(
        isAfterSelection: Boolean,
        newSelectedTopics: List<LMFeedTopicViewData>
    ) {
        if (isAfterSelection) {
            disabledTopics.clear()
            selectedTopic.clear()
        }

        newSelectedTopics.forEach { topic ->
            if (!topic.isEnabled) {
                disabledTopics[topic.id] = topic
            }
            selectedTopic[topic.id] = topic
        }

        val selectedTopics = selectedTopic.values.toList()

        binding.postTopicsGroup.apply {
            removeAllChips()
            selectedTopics.forEach { topic ->
                addChip(topic.name, LMFeedStyleTransformer.postViewStyle.postTopicChipsStyle)
            }
            addChip(chipStyle = LMFeedStyleTransformer.editPostFragmentViewStyle.editTopicsChipStyle) {
                val extras = LMFeedTopicSelectionExtras.Builder()
                    .showAllTopicFilter(false)
                    .selectedTopics(selectedTopics)
                    .showEnabledTopicOnly(true)
                    .disabledTopics(disabledTopics.values.toList())
                    .build()
                val intent = LMFeedTopicSelectionActivity.getIntent(context, extras)

                topicSelectionLauncher.launch(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        when (post?.viewType) {
            ITEM_POST_SINGLE_VIDEO -> {
                showSingleVideoPreview()
            }

            ITEM_POST_MULTIPLE_MEDIA -> {
                showMultiMediaPreview()
            }

            else -> {
                return
            }
        }
    }

    //shows single image preview
    private fun showSingleImagePreview() {
        handleSubmitButtonVisibility()
        val attachmentUrl = postMediaViewData?.attachments?.firstOrNull {
            it.attachmentType == IMAGE
        }?.attachmentMeta?.url ?: return

        binding.singleImageAttachment.apply {
            root.show()

            val postImageMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle
                    ?: return

            postImageView.setImage(attachmentUrl, postImageMediaStyle)
        }
    }

    //shows single video preview
    private fun showSingleVideoPreview() {
        val videoAttachment = postMediaViewData?.attachments?.firstOrNull {
            it.attachmentType == VIDEO
        }
        binding.singleVideoAttachment.apply {
            root.show()
            val meta = videoAttachment?.attachmentMeta
            postVideoPreviewAutoPlayHelper.playVideoInView(postVideoView, url = meta?.url)
        }
    }

    //shows documents preview
    private fun showDocumentsPreview() {
        handleSubmitButtonVisibility()
        binding.documentsAttachment.root.show()
        binding.documentsAttachment.postDocumentsMediaView.apply {

            setShowMoreTextClickListener {
                postMediaViewData?.let {
                    val updatedDocumentMediaViewData = it.toBuilder()
                        .isExpanded(true)
                        .build()

                    setAdapter(
                        0,
                        updatedDocumentMediaViewData,
                        this@LMFeedEditPostFragment
                    )
                }
            }

            val documentMediaViewData = postMediaViewData ?: return
            setAdapter(
                0,
                documentMediaViewData,
                this@LMFeedEditPostFragment
            )
        }
    }

    //shows multimedia preview
    private fun showMultiMediaPreview() {
        binding.apply {
            handleSubmitButtonVisibility()
            multipleMediaAttachment.root.show()
            postMediaViewData?.attachments?.let {
                multipleMediaAttachment.multipleMediaView.setViewPager(
                    0,
                    this@LMFeedEditPostFragment,
                    it
                )
            }
        }
    }

    //renders data in the link view
    private fun showLinkView() {
        val data = ogTags ?: return
        val link = data.url ?: ""

        // sends link attached event with the link
        LMFeedAnalytics.sendLinkAttachedEvent(link, LMFeedScreenNames.EDIT_POST)

        binding.linkPreview.root.show()

        binding.linkPreview.postLinkView.apply {
            setLinkTitle(data.title)
            setLinkImage(data.image)
            setLinkUrl(data.url)
            setLinkRemoveClickListener {
                binding.etPostComposer.removeTextChangedListener(etPostTextChangeListener)
                clearPreviewLink()
            }
        }
    }

    //shows poll view and sets poll data
    private fun showPollView() {
        binding.pollView.apply {
            show()
            setPollTitle(poll?.title ?: "")
            setPollInfo(poll?.getPollSelectionText(requireContext()))
            setTimeLeft(poll?.getExpireOnDate(requireContext()) ?: "")
            setPollOptions(
                0,
                poll?.options ?: emptyList(),
                LMFeedCreateEditPostViewStyleUtil.getUpdatedOptionViewStyle(),
                null
            )
        }
    }

    // clears link preview
    private fun clearPreviewLink() {
        ogTags = null
        postMediaViewData = null
        binding.linkPreview.apply {
            root.hide()
        }
    }

    // handles the visible of submit post button
    private fun handleSubmitButtonVisibility() {
        binding.apply {
            if (LMFeedStyleTransformer.editPostFragmentViewStyle.postHeadingComposerStyle != null) {
                headerViewEditPost.setSubmitButtonEnabled(
                    !etPostHeadingComposer.text?.trim().isNullOrEmpty()
                )
            } else {
                headerViewEditPost.setSubmitButtonEnabled(
                    !etPostComposer.text?.trim()
                        .isNullOrEmpty() || (postMediaViewData != null)
                )
            }
        }
    }

    override fun onPostDocumentMediaClicked(
        position: Int,
        parentPosition: Int,
        attachmentViewData: LMFeedAttachmentViewData
    ) {
        //open the pdf using Android's document view
        val documentUrl = attachmentViewData.attachmentMeta.url ?: ""
        val pdfUri = Uri.parse(documentUrl)
        LMFeedAndroidUtils.startDocumentViewer(requireContext(), pdfUri)
    }

    override fun onPostMultipleMediaPageChangeCallback(position: Int, parentPosition: Int) {
        super.onPostMultipleMediaPageChangeCallback(position, parentPosition)

        val viewPager = binding.multipleMediaAttachment.multipleMediaView.viewpagerMultipleMedia

        // processes the current video whenever view pager's page is changed
        val itemMultipleMediaVideoBinding =
            ((viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(position) as? LMFeedDataBoundViewHolder<*>)
                ?.binding as? LmFeedItemMultipleMediaVideoBinding

        if (itemMultipleMediaVideoBinding == null) {
            // in case the item is not a video
            postVideoPreviewAutoPlayHelper.removePlayer()
        } else {
            // processes the current video item
            postMediaViewData?.attachments?.let { attachments ->
                val meta = attachments[position].attachmentMeta
                postVideoPreviewAutoPlayHelper.playVideoInView(
                    itemMultipleMediaVideoBinding.postVideoView,
                    url = meta.url
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }
}