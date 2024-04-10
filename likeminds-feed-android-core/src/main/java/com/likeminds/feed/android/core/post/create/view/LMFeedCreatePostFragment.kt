package com.likeminds.feed.android.core.post.create.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
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
import com.likeminds.customgallery.CustomGallery
import com.likeminds.customgallery.media.model.*
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentCreatePostBinding
import com.likeminds.feed.android.core.databinding.LmFeedItemMultipleMediaVideoBinding
import com.likeminds.feed.android.core.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostActivity.Companion.LM_FEED_CREATE_POST_EXTRAS
import com.likeminds.feed.android.core.post.create.view.LMFeedCreatePostActivity.Companion.POST_ATTACHMENTS_LIMIT
import com.likeminds.feed.android.core.post.create.viewmodel.LMFeedCreatePostViewModel
import com.likeminds.feed.android.core.post.model.LMFeedLinkOGTagsViewData
import com.likeminds.feed.android.core.topics.model.LMFeedTopicViewData
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionExtras
import com.likeminds.feed.android.core.topicselection.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feed.android.core.topicselection.view.LMFeedTopicSelectionActivity
import com.likeminds.feed.android.core.ui.base.styles.LMFeedIconStyle
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.LMFeedChipGroup
import com.likeminds.feed.android.core.ui.base.views.LMFeedEditText
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.view.LMFeedPostHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.style.LMFeedPostImageMediaViewStyle
import com.likeminds.feed.android.core.ui.widgets.post.postmedia.view.*
import com.likeminds.feed.android.core.universalfeed.adapter.LMFeedUniversalFeedAdapterListener
import com.likeminds.feed.android.core.universalfeed.model.LMFeedMediaViewData
import com.likeminds.feed.android.core.universalfeed.model.LMFeedPostViewData
import com.likeminds.feed.android.core.universalfeed.util.LMFeedPostBinderUtils
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.getUrlIfExist
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.base.LMFeedDataBoundViewHolder
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.feed.android.core.utils.video.LMFeedPostVideoPreviewAutoPlayHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

open class LMFeedCreatePostFragment : Fragment(), LMFeedUniversalFeedAdapterListener {
    private lateinit var binding: LmFeedFragmentCreatePostBinding
    private lateinit var lmFeedCreatePostExtras: LMFeedCreatePostExtras
    private lateinit var etPostTextChangeListener: TextWatcher

    private val createPostViewModel: LMFeedCreatePostViewModel by viewModels()

    private var isDocumentExpanded = false

    private val postVideoPreviewAutoPlayHelper by lazy {
        LMFeedPostVideoPreviewAutoPlayHelper.getInstance()
    }

    private var selectedMediaUris: java.util.ArrayList<SingleUriData> = arrayListOf()
    private val selectedTopic by lazy {
        ArrayList<LMFeedTopicViewData>()
    }
    private var ogTags: LMFeedLinkOGTagsViewData? = null

    companion object {
        const val TAG = "LMFeedCreatePostFragment"
        const val LM_FEED_CREATE_POST_FRAGMENT_EXTRAS = "LM_FEED_CREATE_POST_FRAGMENT_EXTRAS"
        const val TYPE_OF_ATTACHMENT_CLICKED = "image, video"

        fun getInstance(createPostExtras: LMFeedCreatePostExtras): LMFeedCreatePostFragment {
            val createPostFragment = LMFeedCreatePostFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_CREATE_POST_FRAGMENT_EXTRAS, createPostExtras)
            createPostFragment.arguments = bundle
            return createPostFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //receive extras
        receiveExtras()
    }

    private fun receiveExtras() {
        if (arguments == null || arguments?.containsKey(LM_FEED_CREATE_POST_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        lmFeedCreatePostExtras = LMFeedExtrasUtil.getParcelable(
            arguments,
            LM_FEED_CREATE_POST_FRAGMENT_EXTRAS,
            LMFeedCreatePostExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedFragmentCreatePostBinding.inflate(layoutInflater)

        //add customizations
        binding.apply {
            customizeCreatePostHeaderView(headerViewCreatePost)
            customizeAuthorView(authorView)
            customizeTopicsGroup(topicsGroup)
            customizePostComposer(etPostComposer)
            customizePostImageAttachment(postSingleImage)
            customizePostVideoAttachment(postSingleVideo)
            customizePostLinkViewAttachment(postLinkView)
            customizePostDocumentsAttachment(postDocumentsView)
            customizePostMultipleMedia(multipleMediaView)
        }

        return binding.root
    }

    protected open fun customizeCreatePostHeaderView(headerViewCreatePost: LMFeedHeaderView) {
        headerViewCreatePost.apply {
            setStyle(LMFeedStyleTransformer.createPostFragmentViewStyle.headerViewStyle)

            setTitleText("Create %s")
            setSubmitText("Create")
            setSubmitButtonEnabled(false)
        }
    }

    protected open fun customizeAuthorView(authorView: LMFeedPostHeaderView) {
        authorView.apply {
            setStyle(LMFeedStyleTransformer.createPostFragmentViewStyle.authorViewStyle)
        }
    }

    protected open fun customizeTopicsGroup(topicsGroup: LMFeedChipGroup) {
        topicsGroup.apply {
            LMFeedPostBinderUtils.customizePostTopicsGroup(this)
        }
    }

    protected open fun customizePostComposer(etPostComposer: LMFeedEditText) {
        etPostComposer.setStyle(LMFeedStyleTransformer.createPostFragmentViewStyle.postComposerStyle)
    }

    protected open fun customizePostImageAttachment(imageMediaView: LMFeedPostImageMediaView) {
        val updatedImageMediaStyles = getUpdatedImageMediaStyle() ?: return
        imageMediaView.setStyle(updatedImageMediaStyles)
    }

    private fun getUpdatedImageMediaStyle(): LMFeedPostImageMediaViewStyle? {
        val imageAttachmentViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postImageMediaStyle

        return imageAttachmentViewStyle?.toBuilder()
            ?.removeIconStyle(
                LMFeedIconStyle.Builder()
                    .inActiveSrc(R.drawable.lm_feed_ic_cross)
                    .build()
            )
            ?.build()
    }

    protected open fun customizePostVideoAttachment(videoMediaView: LMFeedPostVideoMediaView) {
        val videoAttachmentViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postVideoMediaStyle
        val updatedVideoAttachmentViewStyle =
            videoAttachmentViewStyle?.toBuilder()
                ?.removeIconStyle(
                    LMFeedIconStyle.Builder()
                        .inActiveSrc(R.drawable.lm_feed_ic_cross)
                        .build()
                )
                ?.build()

        updatedVideoAttachmentViewStyle?.let {
            videoMediaView.setStyle(it)
        }
    }

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

    protected open fun customizePostDocumentsAttachment(documentsMediaView: LMFeedPostDocumentsMediaView) {
        val documentsAttachmentViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postDocumentsMediaStyle
                ?: return
        documentsMediaView.setStyle(documentsAttachmentViewStyle)
    }

    protected open fun customizePostMultipleMedia(multipleMediaView: LMFeedPostMultipleMediaView) {
        val multipleMediaViewStyle =
            LMFeedStyleTransformer.postViewStyle.postMediaViewStyle.postMultipleMediaStyle
                ?: return

        multipleMediaView.setStyle(multipleMediaViewStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchInitialData()
        initAddAttachmentsView()
        initPostComposerTextListener()
        observeData()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        showPostMedia()
    }

    override fun onPause() {
        super.onPause()
        postVideoPreviewAutoPlayHelper.removePlayer()
    }

    private fun fetchInitialData() {
        createPostViewModel.getLoggedInUser()
        createPostViewModel.getAllTopics()
    }

    private fun initAddAttachmentsView() {
        binding.apply {
            layoutAttachFiles.setOnClickListener {
                // sends clicked on attachment event for file
                createPostViewModel.sendClickedOnAttachmentEvent("file")

                CustomGallery.start(
                    documentLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(PDF))
                        .allowMultipleSelect(true)
                        .build()
                )
            }

            layoutAddImage.setOnClickListener {
                // sends clicked on attachment event for photo
                createPostViewModel.sendClickedOnAttachmentEvent("photo")

                CustomGallery.start(
                    galleryLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(IMAGE))
                        .allowMultipleSelect(true)
                        .build()
                )
            }

            layoutAddVideo.setOnClickListener {
                // sends clicked on attachment event for video
                createPostViewModel.sendClickedOnAttachmentEvent("video")

                CustomGallery.start(
                    galleryLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(VIDEO))
                        .allowMultipleSelect(true)
                        .build()
                )
            }
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

            // text watcher with debounce to add delay in api calls for ogTags
            textChanges()
                .debounce(500)
                .distinctUntilChanged()
                .onEach {
                    val text = it?.toString()?.trim()
                    if (selectedMediaUris.isNotEmpty()) return@onEach
                    if (!text.isNullOrEmpty()) {
                        showPostMedia()
                    }
                }
                .launchIn(lifecycleScope)

            // text watcher to handlePostButton click-ability
            addTextChangedListener {
                val text = it?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                    clearPreviewLink()
                    if (selectedMediaUris.isEmpty()) {
                        binding.headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
                    } else {
                        binding.headerViewCreatePost.setSubmitButtonEnabled(isEnabled = false)
                    }
                } else {
                    binding.headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
                }
            }
        }
    }

    private fun observeData() {
        createPostViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            initAuthorFrame(user)
        }

        createPostViewModel.showTopicFilter.observe(viewLifecycleOwner) { toShow ->
            if (toShow) {
                handleTopicSelectionView(true)
                initTopicSelectionView()
            } else {
                handleTopicSelectionView(false)
            }
        }

        // observes addPostResponse, once post is created
        createPostViewModel.postAdded.observe(viewLifecycleOwner) { postAdded ->
            requireActivity().apply {
                if (postAdded) {
                    // post is already posted
                    setResult(Activity.RESULT_OK)
                } else {
                    // post is stored in db, now upload it from [FeedFragment]
                    setResult(LMFeedCreatePostActivity.RESULT_UPLOAD_POST)
                }
                finish()
            }
        }

        createPostViewModel.errorEventFlow.onEach { response ->
            when (response) {
                is LMFeedCreatePostViewModel.ErrorMessageEvent.GetLoggedInUser -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedCreatePostViewModel.ErrorMessageEvent.GetTopic -> {
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedCreatePostViewModel.ErrorMessageEvent.AddPost -> {
                    binding.headerViewCreatePost.setSubmitButtonEnabled(
                        isEnabled = true,
                        showProgress = false
                    )
                    LMFeedViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedCreatePostViewModel.ErrorMessageEvent.DecodeUrl -> {
                    val postText = binding.etPostComposer.text.toString()
                    val link = postText.getUrlIfExist()
                    if (link != ogTags?.url) {
                        clearPreviewLink()
                    }
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun initListeners() {
        binding.apply {
            headerViewCreatePost.setSubmitButtonClickListener {
                val text = etPostComposer.text
                //todo: member tagging
                val updatedText = text.toString()
//                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                LMFeedViewUtils.hideKeyboard(binding.root)
                if (selectedMediaUris.isNotEmpty()) {
                    headerViewCreatePost.setSubmitButtonEnabled(
                        isEnabled = true,
                        showProgress = true
                    )
                    createPostViewModel.addPost(
                        context = requireContext(),
                        postTextContent = updatedText,
                        fileUris = selectedMediaUris,
                        ogTags = ogTags,
                        selectedTopics = selectedTopic
                    )
                } else if (updatedText.isNotEmpty()) {
                    headerViewCreatePost.setSubmitButtonEnabled(
                        isEnabled = true,
                        showProgress = true
                    )
                    createPostViewModel.addPost(
                        context = requireContext(),
                        postTextContent = updatedText,
                        ogTags = ogTags,
                        selectedTopics = selectedTopic
                    )
                }
            }
        }
    }

    // clears link preview
    private fun clearPreviewLink() {
        ogTags = null
        binding.postLinkView.hide()
    }

    // handles the logic to show the type of post
    private fun showPostMedia() {
        checkIfAttachmentsLimitExceeded()

        when {
            selectedMediaUris.size >= 1 && MediaType.isPDF(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedDocuments()
            }

            selectedMediaUris.size == 1 && MediaType.isImage(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedImage()
            }

            selectedMediaUris.size == 1 && MediaType.isVideo(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedVideo()
            }

            selectedMediaUris.size >= 1 -> {
                ogTags = null
                showMultiMediaAttachments()
            }

            else -> {
                val text = binding.etPostComposer.text?.trim()
                if (selectedMediaUris.size == 0 && text != null) {
                    showLinkPreview(text.toString())
                } else {
                    clearPreviewLink()
                }
                binding.headerViewCreatePost.setSubmitButtonEnabled(isEnabled = !text.isNullOrEmpty())
                handleAddAttachmentLayouts(true)
            }
        }
    }

    // shows attached image in single image post type
    private fun showAttachedImage() {
        binding.apply {
            handleAddAttachmentLayouts(false)
            headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
            postSingleImage.show()
            postSingleVideo.hide()
            postLinkView.hide()
            postDocumentsView.hide()
            multipleMediaView.hide()
            btnAddMoreMedia.setOnClickListener {
                // sends clicked on attachment event for image and video
                createPostViewModel.sendClickedOnAttachmentEvent(TYPE_OF_ATTACHMENT_CLICKED)
                CustomGallery.start(
                    galleryLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(IMAGE, VIDEO))
                        .allowMultipleSelect(true)
                        .build()
                )
            }

            postSingleImage.setRemoveIconClickListener {
                selectedMediaUris.clear()
                postSingleImage.hide()
                handleAddAttachmentLayouts(true)
                val text = etPostComposer.text?.trim()
                headerViewCreatePost.setSubmitButtonEnabled(isEnabled = !text.isNullOrEmpty())
            }

            val imageStyle = getUpdatedImageMediaStyle() ?: return
            postSingleImage.setImage(selectedMediaUris.first().uri, imageStyle)
        }
    }

    // shows attached video in single video post type
    private fun showAttachedVideo() {
        binding.apply {
            handleAddAttachmentLayouts(false)
            headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
            postSingleVideo.show()
            postSingleImage.hide()
            postLinkView.hide()
            postDocumentsView.hide()
            multipleMediaView.hide()
            btnAddMoreMedia.setOnClickListener {
                // sends clicked on attachment event for image and video
                createPostViewModel.sendClickedOnAttachmentEvent(TYPE_OF_ATTACHMENT_CLICKED)
                CustomGallery.start(
                    galleryLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(IMAGE, VIDEO))
                        .allowMultipleSelect(true)
                        .build()
                )
            }

            postVideoPreviewAutoPlayHelper.playVideoInView(
                postSingleVideo,
                selectedMediaUris.first().uri
            )

            postSingleVideo.setRemoveIconClickListener {
                selectedMediaUris.clear()
                postSingleVideo.hide()
                handleAddAttachmentLayouts(true)
                val text = etPostComposer.text?.trim()
                headerViewCreatePost.setSubmitButtonEnabled(isEnabled = !text.isNullOrEmpty())
                postVideoPreviewAutoPlayHelper.removePlayer()
            }
        }
    }

    //shows view pager with multiple media
    private fun showMultiMediaAttachments() {
        binding.apply {
            handleAddAttachmentLayouts(false)
            headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
            postSingleImage.hide()
            postSingleVideo.hide()
            postLinkView.hide()
            postDocumentsView.hide()
            multipleMediaView.show()

            btnAddMoreMedia.visibility =
                if (selectedMediaUris.size >= POST_ATTACHMENTS_LIMIT) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            btnAddMoreMedia.setOnClickListener {
                // sends clicked on attachment event for image and video
                createPostViewModel.sendClickedOnAttachmentEvent(TYPE_OF_ATTACHMENT_CLICKED)

                CustomGallery.start(
                    galleryLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(IMAGE, VIDEO))
                        .allowMultipleSelect(true)
                        .build()
                )
            }

            val attachments = LMFeedViewDataConvertor.convertSingleDataUri(selectedMediaUris)

            multipleMediaView.setViewPager(
                0,
                this@LMFeedCreatePostFragment,
                attachments,
                true
            )
        }
    }

    // shows link preview for link post type
    private fun showLinkPreview(text: String?) {
        binding.postLinkView.apply {
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
                createPostViewModel.decodeUrl(link)
            } else {
                clearPreviewLink()
            }
        }
    }

    //hide/show topics related views
    private fun handleTopicSelectionView(showView: Boolean) {
        binding.apply {
            topicsGroup.isVisible = showView
            topicSeparator.isVisible = showView
        }
    }

    //init initial topic selection view with "Select Topic Chip"
    private fun initTopicSelectionView() {
        binding.topicsGroup.apply {
            removeAllChips()
            addChip(
                getString(R.string.lm_feed_select_topics),
                LMFeedStyleTransformer.createPostFragmentViewStyle.selectTopicsChipStyle
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
                    addTopicsToGroup(selectedTopics)
                }
            }
        }

    //add selected topics to group and add edit chip as well in the end
    private fun addTopicsToGroup(newSelectedTopics: List<LMFeedTopicViewData>) {
        this.selectedTopic.apply {
            clear()
            addAll(newSelectedTopics)
        }
        binding.topicsGroup.apply {
            //clear view
            removeAllChips()

            //add topics to group
            newSelectedTopics.forEach { topic ->
                addChip(topic.name, LMFeedStyleTransformer.postViewStyle.postTopicChipsStyle)
            }

            // add edit chip at end
            addChip(chipStyle = LMFeedStyleTransformer.createPostFragmentViewStyle.editChipStyle) {
                val extras = LMFeedTopicSelectionExtras.Builder()
                    .showAllTopicFilter(false)
                    .selectedTopics(selectedTopic)
                    .showEnabledTopicOnly(true)
                    .build()

                val intent = LMFeedTopicSelectionActivity.getIntent(context, extras)
                topicSelectionLauncher.launch(intent)
            }
        }
    }

    //set logged in user data to post header frame
    private fun initAuthorFrame(user: LMFeedUserViewData) {
        binding.nestedScrollCreatePost.show()
        binding.authorView.apply {
            setAuthorImage(user)
            setAuthorName(user.name)
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

    // launcher to handle document (PDF) intent
    private val documentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = LMFeedExtrasUtil.getParcelable(
                    result.data?.extras,
                    CustomGallery.ARG_CUSTOM_GALLERY_RESULT,
                    CustomGalleryResult::class.java
                )
                if (data != null) {
                    onDocumentPicked(data)
                }
            }
        }

    private fun onDocumentPicked(documentResult: CustomGalleryResult) {
        // sends media attached event with media type and count
        val mediaUris = documentResult.medias
        createPostViewModel.sendMediaAttachedEvent(mediaUris)
        selectedMediaUris.addAll(mediaUris)

        if (mediaUris.isNotEmpty()) {
            checkIfAttachmentsLimitExceeded()
            showAttachedDocuments()
        }
    }

    // shows toast and removes extra items if attachments limit is exceeded
    private fun checkIfAttachmentsLimitExceeded() {
        if (selectedMediaUris.size > 10) {
            LMFeedViewUtils.showErrorMessageToast(
                requireContext(), requireContext().resources.getQuantityString(
                    R.plurals.lm_feed_you_can_select_upto_x_items,
                    POST_ATTACHMENTS_LIMIT,
                    POST_ATTACHMENTS_LIMIT
                )
            )
            val size = selectedMediaUris.size
            selectedMediaUris.subList(POST_ATTACHMENTS_LIMIT, size).clear()
        }
    }

    // shows document recycler view with attached files
    private fun showAttachedDocuments() {
        binding.apply {
            handleAddAttachmentLayouts(false)
            headerViewCreatePost.setSubmitButtonEnabled(isEnabled = true)
            postSingleImage.hide()
            postSingleVideo.hide()
            postLinkView.hide()
            postDocumentsView.show()
            multipleMediaView.hide()

            btnAddMoreMedia.visibility =
                if (selectedMediaUris.size >= POST_ATTACHMENTS_LIMIT) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            btnAddMoreMedia.setOnClickListener {
                // sends clicked on attachment event for file
                createPostViewModel.sendClickedOnAttachmentEvent("file")

                CustomGallery.start(
                    documentLauncher,
                    requireContext(),
                    CustomGalleryConfig.Builder()
                        .mediaTypes(listOf(PDF))
                        .allowMultipleSelect(true)
                        .build()
                )
            }
            val documentMediaViewData = LMFeedMediaViewData.Builder()
                .isExpanded(isDocumentExpanded)
                .attachments(LMFeedViewDataConvertor.convertSingleDataUri(selectedMediaUris))
                .build()

            postDocumentsView.setAdapter(
                0,
                documentMediaViewData,
                this@LMFeedCreatePostFragment,
                true
            )
        }
    }

    // handles visibility of add attachment layouts
    private fun handleAddAttachmentLayouts(show: Boolean) {
        binding.groupAddAttachments.isVisible = show
        binding.btnAddMoreMedia.isVisible = !show
    }

    // launcher to handle gallery (IMAGE/VIDEO) intent
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = LMFeedExtrasUtil.getParcelable(
                    result.data?.extras,
                    CustomGallery.ARG_CUSTOM_GALLERY_RESULT,
                    CustomGalleryResult::class.java
                )

                if (data != null) {
                    onMediaPicked(data)
                }
            }
        }

    // adds the picked media to the selected media
    private fun onMediaPicked(mediaResult: CustomGalleryResult) {
        // sends media attached event with media type and count
        val mediaUris = mediaResult.medias
        createPostViewModel.sendMediaAttachedEvent(mediaUris)
        selectedMediaUris.addAll(mediaUris)
        showPostMedia()
    }

    override fun onPostMultipleMediaPageChangeCallback(position: Int, parentPosition: Int) {
        super.onPostMultipleMediaPageChangeCallback(position, parentPosition)

        val viewPager = binding.multipleMediaView.viewpagerMultipleMedia

        // processes the current video whenever view pager's page is changed
        val itemMultipleMediaVideoBinding =
            ((viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(position) as? LMFeedDataBoundViewHolder<*>)
                ?.binding as? LmFeedItemMultipleMediaVideoBinding

        if (itemMultipleMediaVideoBinding == null) {
            // in case the item is not a video
            postVideoPreviewAutoPlayHelper.removePlayer()
        } else {
            // processes the current video item
            postVideoPreviewAutoPlayHelper.playVideoInView(
                itemMultipleMediaVideoBinding.postVideoView,
                uri = selectedMediaUris[position].uri
            )
        }
    }

    override fun onMediaRemovedClicked(position: Int, mediaType: String) {
        super.onMediaRemovedClicked(position, mediaType)

        selectedMediaUris.removeAt(position)
        binding.apply {
            if (mediaType == PDF) {
                postDocumentsView.removeDocument(position)
                if (selectedMediaUris.size == 0) {
                    postDocumentsView.hide()
                }
            } else {
                multipleMediaView.removeMedia(position)
                postVideoPreviewAutoPlayHelper.removePlayer()
            }
        }
        showPostMedia()
    }

    override fun onPostMultipleDocumentsExpanded(position: Int, postViewData: LMFeedPostViewData) {
        super.onPostMultipleDocumentsExpanded(position, postViewData)

        isDocumentExpanded = true
    }
}