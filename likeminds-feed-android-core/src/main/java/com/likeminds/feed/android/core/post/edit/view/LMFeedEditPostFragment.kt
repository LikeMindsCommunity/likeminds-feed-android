package com.likeminds.feed.android.core.post.edit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feed.android.core.LMFeedCoreApplication.Companion.LOG_TAG
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentEditPostBinding
import com.likeminds.feed.android.core.databinding.LmFeedItemMultipleMediaVideoBinding
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostDisabledTopicsDialogExtras
import com.likeminds.feed.android.core.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feed.android.core.post.edit.view.LMFeedEditPostActivity.Companion.LM_FEED_EDIT_POST_EXTRAS
import com.likeminds.feed.android.core.post.edit.viewmodel.LMFeedEditPostViewModel
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.post.util.LMFeedPostEvent
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionExtras
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedEditText
import com.likeminds.feed.android.core.ui.base.views.LMFeedImageView
import com.likeminds.feed.android.core.ui.base.views.LMFeedProgressBar
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.view.LMFeedPostHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostDocumentsMediaView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostLinkMediaView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostMultipleMediaView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.LMFeedPostVideoMediaView
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedUserViewData
import com.likeminds.feed.android.core.universalfeed.util.LMFeedPostBinderUtils.customizePostTopicsGroup
import com.likeminds.feed.android.core.utils.LMFeedCommunityUtil
import com.likeminds.feed.android.core.utils.LMFeedExtrasUtil
import com.likeminds.feed.android.core.utils.LMFeedProgressBarHelper
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getUrlIfExist
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.pluralizeOrCapitalize
import com.likeminds.feed.android.core.utils.LMFeedViewUtils
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.analytics.LMFeedAnalytics
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_DOCUMENTS
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_LINK
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_MULTIPLE_MEDIA
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_SINGLE_IMAGE
import com.likeminds.feed.android.core.utils.base.model.ITEM_POST_SINGLE_VIDEO
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.emptyExtrasException
import com.likeminds.feed.android.core.utils.pluralize.model.LMFeedWordAction
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoAutoPlayHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

open class LMFeedEditPostFragment :
    Fragment(),
    LMFeedUniversalFeedAdapterListener {

    private lateinit var binding: LmFeedFragmentEditPostBinding

    private lateinit var editPostExtras: LMFeedEditPostExtras

    private var postMediaViewData: LMFeedMediaViewData? = null
    private var ogTags: LMFeedLinkOGTagsViewData? = null

    private var post: LMFeedPostViewData? = null

    private lateinit var etPostTextChangeListener: TextWatcher

    private val editPostViewModel: LMFeedEditPostViewModel by viewModels()

    private val postVideoAutoPlayHelper by lazy {
        LMFeedPostVideoAutoPlayHelper.getInstance()
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
            customizePostSingleImageView(singleImageAttachment.ivSingleImagePost)
            customizePostSingleVideoView(singleVideoAttachment.postVideoView)
            customizePostLinkPreview(linkPreview.postLinkView)
            customizePostDocumentsView(documentsAttachment.postDocumentsMediaView)
            customizePostMultipleMediaView(multipleMediaAttachment.multipleMediaView)
            customizeEditPostProgressbar(progressBar.progressView)
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
            setSubmitButtonEnabled(false)
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
        etPostComposer.setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.postComposerStyle)
    }

    //customizes single image view type post
    protected open fun customizePostSingleImageView(postSingleImageView: LMFeedImageView) {
        val postImageMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle
                ?: return

        postSingleImageView.setStyle(postImageMediaStyle)
    }

    //customizes single video view type post
    protected open fun customizePostSingleVideoView(postSingleVideoView: LMFeedPostVideoMediaView) {
        val postVideoMediaStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle
                ?: return

        postSingleVideoView.setStyle(postVideoMediaStyle)
    }

    //customizes link preview in the post
    protected open fun customizePostLinkPreview(postLinkView: LMFeedPostLinkMediaView) {
        //todo: change this to the create post link view style
        val postLinkViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postLinkViewStyle
                ?: return

        postLinkView.setStyle(postLinkViewStyle)
    }

    //customizes documents type post
    protected open fun customizePostDocumentsView(postDocumentsView: LMFeedPostDocumentsMediaView) {
        val postDocumentsViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postDocumentsMediaStyle
                ?: return

        postDocumentsView.setStyle(postDocumentsViewStyle)
    }

    //customizes multiple media type post
    protected open fun customizePostMultipleMediaView(postMultipleMediaView: LMFeedPostMultipleMediaView) {
        val postMultipleMediaViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postMultipleMediaStyle
                ?: return

        postMultipleMediaView.setStyle(postMultipleMediaViewStyle)
    }

    //customizes progress bar in the edit post
    protected open fun customizeEditPostProgressbar(progressBar: LMFeedProgressBar) {
        progressBar.setStyle(LMFeedStyleTransformer.editPostFragmentViewStyle.progressBarStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        fetchData()
        observeData()
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

    protected open fun onSavePostClicked() {
        //check all the necessary conditions before saving a post
        binding.apply {
            val text = etPostComposer.text
            //todo:
            val updatedText = text.toString()
//        val updatedText = memberTagging.replaceSelectedMembers(text).trim()
            val topics = selectedTopic.values

            if (selectedTopic.isNotEmpty()) {
                if (disabledTopics.isEmpty()) {
                    //call api as all topics are enabled
                    headerViewEditPost.setSubmitButtonEnabled(isEnabled = true, showProgress = true)
                    editPostViewModel.editPost(
                        editPostExtras.postId,
                        updatedText,
                        attachments = postMediaViewData?.attachments,
                        ogTags = ogTags,
                        selectedTopics = topics.toList()
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
                    attachments = postMediaViewData?.attachments,
                    ogTags = ogTags,
                    selectedTopics = topics.toList()
                )
            }
        }
    }

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
        //todo: add remaining things as well
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
            }
        }.observeInLifecycle(viewLifecycleOwner)

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

            LMFeedProgressBarHelper.hideProgress(progressBar)
            nestedScroll.show()

            // decodes the post text and sets to the edit text
            //todo:
//            MemberTaggingDecoder.decode(
//                etPostContent,
//                post.text,
//                LMFeedBranding.getTextLinkColor()
//            )

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
                    ogTags = postMediaViewData?.attachments?.first()?.attachmentMeta?.ogTags
                    showLinkView()
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

            if (postMediaViewData == null) {
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
                val text = it?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                    clearPreviewLink()
                    if (postMediaViewData != null) {
                        binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = true)
                    } else {
                        binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = false)
                    }
                } else {
                    binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = true)
                }
            }
        }
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
            addChip(chipStyle = LMFeedStyleTransformer.editPostFragmentViewStyle.editChipStyle) {
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
        binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = true)
        val attachmentUrl = postMediaViewData?.attachments?.first()?.attachmentMeta?.url ?: return

        binding.singleImageAttachment.apply {
            root.show()

            val postImageMediaStyle =
                LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle
                    ?: return

            ivSingleImagePost.setImage(attachmentUrl, postImageMediaStyle)
        }
    }

    //shows single video preview
    private fun showSingleVideoPreview() {
        val videoAttachment = postMediaViewData?.attachments?.first()
        binding.singleVideoAttachment.apply {
            root.show()
            val meta = videoAttachment?.attachmentMeta
            postVideoAutoPlayHelper?.playVideoInView(postVideoView, url = meta?.url)
        }
    }

    //shows documents preview
    private fun showDocumentsPreview() {
        binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = true)
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
        binding.headerViewEditPost.setSubmitButtonEnabled(isEnabled = true)
        binding.apply {
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
        LMFeedAnalytics.sendLinkAttachedEvent(link)

        binding.linkPreview.root.show()

        binding.linkPreview.postLinkView.apply {
            setLinkTitle(data.title)
            setLinkDescription(data.description)
            setLinkImage(data.image)
            setLinkUrl(data.url)
            setLinkRemoveClickListener {
                binding.etPostComposer.removeTextChangedListener(etPostTextChangeListener)
                clearPreviewLink()
            }
            //todo: check if this is required
//            LinkUtil.handleLinkPreviewConstraints(this, isImageValid)
        }
    }

    // clears link preview
    private fun clearPreviewLink() {
        ogTags = null
        binding.linkPreview.apply {
            root.hide()
        }
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
            postVideoAutoPlayHelper?.removePlayer()
        } else {
            // processes the current video item
            postMediaViewData?.attachments?.let { attachments ->
                val meta = attachments[position].attachmentMeta
                postVideoAutoPlayHelper?.playVideoInView(
                    itemMultipleMediaVideoBinding.postVideoView,
                    url = meta.url
                )
            }
        }
    }
}