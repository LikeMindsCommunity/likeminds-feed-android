package com.likeminds.feed.android.core.poll.create.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedFragmentCreatePollBinding
import com.likeminds.feed.android.core.databinding.LmFeedItemCreatePollOptionBinding
import com.likeminds.feed.android.core.poll.create.adapter.LMFeedCreatePollOptionAdapterListener
import com.likeminds.feed.android.core.poll.create.adapter.LMFeedPollAdvancedOptionsAdapter
import com.likeminds.feed.android.core.poll.create.model.LMFeedCreatePollOptionViewData
import com.likeminds.feed.android.core.poll.create.view.LMFeedCreatePollActivity.Companion.LM_FEED_CREATE_POLL_RESULT
import com.likeminds.feed.android.core.poll.create.viewmodel.LMFeedCreatePollViewModel
import com.likeminds.feed.android.core.poll.result.model.LMFeedPollViewData
import com.likeminds.feed.android.core.ui.base.styles.setStyle
import com.likeminds.feed.android.core.ui.base.views.*
import com.likeminds.feed.android.core.ui.widgets.headerview.view.LMFeedHeaderView
import com.likeminds.feed.android.core.ui.widgets.post.postheaderview.view.LMFeedPostHeaderView
import com.likeminds.feed.android.core.utils.*
import com.likeminds.feed.android.core.utils.LMFeedValueUtils.isValidIndex
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.hide
import com.likeminds.feed.android.core.utils.LMFeedViewUtils.show
import com.likeminds.feed.android.core.utils.coroutine.observeInLifecycle
import com.likeminds.feed.android.core.utils.user.LMFeedUserViewData
import com.likeminds.likemindsfeed.post.model.PollMultiSelectState
import com.likeminds.likemindsfeed.post.model.PollType
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

open class LMFeedCreatePollFragment : Fragment(), LMFeedCreatePollOptionAdapterListener {

    private lateinit var binding: LmFeedFragmentCreatePollBinding
    private var poll: LMFeedPollViewData? = null

    private val viewModel: LMFeedCreatePollViewModel by viewModels()
    private var isAdvancedOptionsVisible = false

    companion object {
        const val TAG = "LMFeedCreatePollFragment"
        const val LM_FEED_CREATE_POLL_FRAGMENT_EXTRAS = "LM_FEED_CREATE_POLL_FRAGMENT_EXTRAS"
        const val DATE_PICKER_TAG = "DATE_PICKER"
        const val TIME_PICKER_TAG = "TIME_PICKER"
        const val MAX_OPTIONS = 10
        const val DATE_STRING_FORMAT = "dd-MM-yy HH:mm"

        @JvmStatic
        fun getInstance(poll: LMFeedPollViewData?): LMFeedCreatePollFragment {
            val fragment = LMFeedCreatePollFragment()
            val args = Bundle()
            args.putParcelable(LM_FEED_CREATE_POLL_FRAGMENT_EXTRAS, poll)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiveExtras()
    }

    private fun receiveExtras() {
        if (arguments == null || arguments?.containsKey(LM_FEED_CREATE_POLL_FRAGMENT_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        } else {
            poll = LMFeedExtrasUtil.getParcelable(
                arguments,
                LM_FEED_CREATE_POLL_FRAGMENT_EXTRAS,
                LMFeedPollViewData::class.java
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LmFeedFragmentCreatePollBinding.inflate(inflater, container, false)

        binding.apply {
            customizeCreatePollHeader(headerViewCreatePoll)
            customizeAuthorView(authorView)
            customizePollQuestion(tvPollQuestionTitle, etPollQuestion)
            customizePollExpiryTime(tvPollExpiryTitle, tvPollExpiryTime)
            customizePollOptions(tvPollOptionsTitle, tvAddOption)
            customizeAdvancedOptionTitle(tvAdvancedOptions)
            customizeAdvanceOptionSwitchOptions(
                switchAnonymousPoll,
                switchLiveResults,
                switchAddNewOptions
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initUI()
        fetchInitialData()
        observeData()
    }

    //customize the header view of the fragment
    protected open fun customizeCreatePollHeader(headerView: LMFeedHeaderView) {
        headerView.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.headerViewStyle)

            setTitleText(getString(R.string.lm_feed_new_poll))
            setSubmitText(getString(R.string.lm_feed_done))
            setSubmitButtonEnabled(false)
        }
    }

    //customize the author view of the fragment
    protected open fun customizeAuthorView(authorView: LMFeedPostHeaderView) {
        authorView.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.authorViewStyle)
        }
    }

    //customize the poll question title and input box
    protected open fun customizePollQuestion(
        tvPollQuestionTitle: LMFeedTextView,
        etPollQuestion: LMFeedEditText
    ) {
        //customize the poll question title
        tvPollQuestionTitle.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollQuestionTitleViewStyle)
        }

        //customize the poll question edit text
        etPollQuestion.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollQuestionViewStyle)
        }
    }

    //customize the poll options title and add option text view
    protected open fun customizePollOptions(
        tvPollOptionsTitle: LMFeedTextView,
        tvAddOption: LMFeedTextView
    ) {
        tvPollOptionsTitle.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollOptionsTitleViewStyle)
        }

        tvAddOption.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollAddOptionViewStyle)
        }
    }

    //customize the poll expiry time title and text view
    protected open fun customizePollExpiryTime(
        tvPollExpiryTitle: LMFeedTextView,
        tvPollExpiryTime: LMFeedTextView
    ) {
        tvPollExpiryTitle.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollExpiryTimeTitleViewStyle)
        }

        tvPollExpiryTime.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollExpiryTimeViewStyle)
        }
    }

    //customize the advanced options title
    protected open fun customizeAdvancedOptionTitle(tvAdvancedOptions: LMFeedTextView) {
        tvAdvancedOptions.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollAdvanceOptionViewStyle)
        }
    }

    //customize the advance option switch options
    protected open fun customizeAdvanceOptionSwitchOptions(
        switchAnonymousPoll: LMFeedSwitch,
        switchLiveResults: LMFeedSwitch,
        switchAddNewOptions: LMFeedSwitch
    ) {
        switchAddNewOptions.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollAdvanceOptionSwitchViewStyle)
        }

        switchAnonymousPoll.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollAdvanceOptionSwitchViewStyle)
        }

        switchLiveResults.apply {
            setStyle(LMFeedStyleTransformer.createPollFragmentViewStyle.pollAdvanceOptionSwitchViewStyle)
        }
    }


    //initializes the UI
    private fun initUI() {
        initPollOptionListView()
        initPollQuestion()
        initPollExpiryTime()
        initPollAdvanceOptions()
        initPollMultiSelectStateSpinner()
        initPollMultiSelectNumberSpinner()
        initPollQuestionListeners()
        poll = null
    }

    //set the poll question text if exist
    private fun initPollQuestion() {
        poll?.let { poll ->
            binding.etPollQuestion.setText(poll.title)
            validatePoll()
        }
    }

    //initializes the poll option list view
    private fun initPollOptionListView() {
        binding.rvPollOptions.apply {
            //set the adapter
            setAdapter(this@LMFeedCreatePollFragment)

            //set the poll options
            val pollOptions = poll?.options?.map {
                LMFeedCreatePollOptionViewData.Builder()
                    .text(it.text)
                    .build()
            } ?: viewModel.createInitialPollOptionList()
            replaceOptions(pollOptions)

            //update the cache size
            updatePollItemCacheSize()
        }
    }

    //initializes the poll expiry time and set the date if exist
    private fun initPollExpiryTime() {
        poll?.let { poll ->
            //get simple date format
            val simpleDateFormat = SimpleDateFormat(
                DATE_STRING_FORMAT,
                Locale.getDefault()
            )
            //get the expiry time
            val expiryTime = poll.expiryTime

            //format the date
            val dateString = simpleDateFormat.format(Date(expiryTime))

            //set the text
            binding.tvPollExpiryTime.text = dateString
            viewModel.setPollExpiryTime(expiryTime)
            validatePoll()
        }
    }

    //initializes the poll advance options if exist
    private fun initPollAdvanceOptions() {
        poll?.let { poll ->
            if (poll.isAnonymous || poll.allowAddOption || poll.isDeferredPoll() || poll.isMultiChoicePoll()) {
                binding.apply {
                    expandAdvancedSettings()

                    switchAnonymousPoll.isChecked = poll.isAnonymous
                    switchLiveResults.isChecked = poll.isDeferredPoll()
                    switchAddNewOptions.isChecked = poll.allowAddOption
                    validatePoll()
                }
            }
        }
    }

    //initializes the poll multi select state spinner
    private fun initPollMultiSelectStateSpinner() {
        val stateList = viewModel.getMultipleOptionStateList()

        val spinnerAdapter = LMFeedPollAdvancedOptionsAdapter(
            requireContext(),
            stateList
        )

        var indexToSelect = 0
        poll?.let { poll ->
            val state = poll.multipleSelectState
            val stateValue = viewModel.getStringFromPollMultiSelectState(state)

            indexToSelect = stateList.indexOfFirst {
                it.equals(stateValue, true)
            }
        }

        binding.spinnerMultipleSelectState.apply {
            setAdapter(spinnerAdapter)
            setSelection(indexToSelect)
        }
    }

    //initializes the poll multi select poll option spinner
    private fun initPollMultiSelectNumberSpinner() {
        val spinnerAdapter = LMFeedPollAdvancedOptionsAdapter(
            requireContext(),
            viewModel.getMultipleOptionNoList(resources).subList(0, binding.rvPollOptions.itemCount)
        )

        //if (getSelectedPollMultiSelectNumber() > binding.rvPollOptions.itemCount) {
        //                0
        //            } else {
        //                getSelectedPollMultiSelectNumber() - 1
        //            }

        var indexToSelect = when {
            getSelectedPollMultiSelectNumber() == 0 -> 0
            getSelectedPollMultiSelectNumber() > binding.rvPollOptions.itemCount -> 0
            else -> getSelectedPollMultiSelectNumber() - 1
        }

        poll?.let { poll ->
            val number = poll.multipleSelectNumber
            indexToSelect = number - 1
        }

        binding.spinnerMultipleSelectNumber.apply {
            setAdapter(spinnerAdapter)
            setSelection(indexToSelect)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPollQuestionListeners() {
        binding.etPollQuestion.apply {

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
                validatePoll()
            }
        }
    }

    //attaches the listeners
    private fun initListeners() {
        binding.apply {
            headerViewCreatePoll.setNavigationIconClickListener {
                onNavigationIconClicked()
            }

            tvAddOption.setOnClickListener {
                onAddPollOptionClicked()
            }

            tvPollExpiryTime.setOnClickListener {
                onPollExpiryTimeClicked()
            }

            tvAdvancedOptions.setOnClickListener {
                onAdvancedSettingsClicked()
            }

            headerViewCreatePoll.setSubmitButtonClickListener {
                onPollSubmitClicked()
            }
        }
    }

    //fetches the initial data
    private fun fetchInitialData() {
        viewModel.getLoggedInUser()
    }

    //observes the response data
    private fun observeData() {
        viewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            initAuthorView(user)
        }

        viewModel.poll.observe(viewLifecycleOwner) { poll ->
            if (poll != null) {
                val intent = Intent().apply {
                    putExtra(LM_FEED_CREATE_POLL_RESULT, poll)
                }
                requireActivity().setResult(Activity.RESULT_OK, intent)
                requireActivity().finish()
            }
        }

        viewModel.errorEvent.onEach { response ->
            when (response) {
                is LMFeedCreatePollViewModel.ErrorEvent.GetLoggedInUserError -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.message
                    )
                }

                is LMFeedCreatePollViewModel.ErrorEvent.CreatePollError -> {
                    LMFeedViewUtils.showErrorMessageToast(
                        requireContext(),
                        response.message
                    )
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    //initializes the author view
    private fun initAuthorView(user: LMFeedUserViewData) {
        binding.apply {
            nestedScrollCreatePoll.show()
            authorView.apply {
                setAuthorImage(user)
                setAuthorName(user.name)
            }
        }
    }

    //customize click of the navigation icon
    protected open fun onNavigationIconClicked() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    //customize click of the add option text view
    protected open fun onAddPollOptionClicked() {
        binding.rvPollOptions.apply {
            addOption(itemCount, viewModel.getEmptyPollOption())
            updatePollItemCacheSize()
        }
        initPollMultiSelectNumberSpinner()
        validatePollOptionCount()
        validatePoll()
    }

    //customize the click of poll expiry time
    protected open fun onPollExpiryTimeClicked() {
        // calendar instance
        val calendar = Calendar.getInstance()

        //date formatter
        val simpleDateFormat = SimpleDateFormat(
            DATE_STRING_FORMAT,
            Locale.getDefault()
        )


        //date picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.lm_feed_select_poll_expiry_time))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build()
            )
            .build()

        //time picker
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.lm_feed_select_poll_expiry_time))
            .build()

        //show the date picker
        datePicker.show(requireActivity().supportFragmentManager, DATE_PICKER_TAG)

        //date picker listener
        datePicker.addOnPositiveButtonClickListener { epoch ->
            calendar.timeInMillis = epoch
            timePicker.show(requireActivity().supportFragmentManager, TIME_PICKER_TAG)
        }

        //time picker listener
        timePicker.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)

            val dateString = simpleDateFormat.format(calendar.time)

            if (calendar.time.after(Date())) {
                binding.tvPollExpiryTime.text = dateString
                viewModel.setPollExpiryTime(calendar.timeInMillis)
                validatePoll()
            } else {
                LMFeedViewUtils.showErrorMessageToast(
                    requireContext(),
                    getString(R.string.lm_feed_error_invalid_time)
                )
            }
        }
    }

    //customize the click of advanced options
    protected open fun onAdvancedSettingsClicked() {
        if (isAdvancedOptionsVisible) {
            collapseAdvancedSettings()
        } else {
            expandAdvancedSettings()
        }
    }

    //collapse the advanced options
    private fun collapseAdvancedSettings() {
        binding.apply {
            isAdvancedOptionsVisible = false
            tvAdvancedOptions.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.lm_feed_ic_arrow_edge_down,
                0
            )
            LMFeedAnimationUtils.collapseView(clAdvancedOptions)

        }
    }

    //expand the advanced options
    private fun expandAdvancedSettings() {
        binding.apply {
            isAdvancedOptionsVisible = true
            tvAdvancedOptions.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.lm_feed_ic_arrow_edge_up,
                0
            )
            LMFeedAnimationUtils.expandView(clAdvancedOptions)
        }
    }

    //customize the click of submit button
    protected open fun onPollSubmitClicked() {
        val pollQuestion = binding.etPollQuestion.text?.trim().toString()
        val pollType = getPollType()
        val isPollAnonymous = isAnonymousPoll()
        val isPollAllowAddOption = isAddOptionAllowed()
        val pollMultiSelectState = getSelectedPollMultiSelectState()
        val pollMultiSelectNumber = getSelectedPollMultiSelectNumber()

        LMFeedViewUtils.hideKeyboard(binding.root)
        viewModel.createPoll(
            pollQuestion,
            pollMultiSelectState,
            pollType,
            pollMultiSelectNumber,
            isPollAnonymous,
            isPollAllowAddOption,
            isAdvancedOptionsVisible
        )
    }

    //get poll type
    private fun getPollType(): PollType {
        return if (binding.switchLiveResults.isChecked) {
            PollType.DEFERRED
        } else {
            PollType.INSTANT
        }
    }

    //get is poll anonymous or not
    private fun isAnonymousPoll(): Boolean {
        return binding.switchAnonymousPoll.isChecked
    }

    //get is add option allowed or not
    private fun isAddOptionAllowed(): Boolean {
        return binding.switchAddNewOptions.isChecked
    }

    //get selected poll multi select state
    private fun getSelectedPollMultiSelectState(): PollMultiSelectState {
        val selectedValue = binding.spinnerMultipleSelectState.selectedItem.toString()
        return viewModel.getPollMultiSelectStateValue(selectedValue)
    }

    //get selected poll multi select number
    private fun getSelectedPollMultiSelectNumber(): Int {
        return binding.spinnerMultipleSelectNumber.selectedItemPosition + 1
    }


    override fun onPollOptionRemoved(createPollOptionViewData: LMFeedCreatePollOptionViewData) {
        val index = binding.rvPollOptions.getAllOptions().indexOf(createPollOptionViewData)
        if (index.isValidIndex()) {
            binding.rvPollOptions.removeOption(index)
            viewModel.removeBindingFromMap(index)
            validatePollOptionCount()
            validatePoll()
            initPollMultiSelectNumberSpinner()
        }
    }

    override fun onPollOptionBinded(position: Int, binding: LmFeedItemCreatePollOptionBinding) {
        viewModel.addBindingToMap(position, binding)
        validatePollOptionCount()
    }

    override fun onPollOptionFilled() {
        validatePoll()
    }

    //validates the poll option count and hide/show add option text view
    private fun validatePollOptionCount() {
        val optionCount = binding.rvPollOptions.itemCount
        if (optionCount == MAX_OPTIONS) {
            binding.tvAddOption.hide()
        } else {
            binding.tvAddOption.show()
        }
    }

    //validates the poll and enables/disables submit button
    private fun validatePoll() {
        val pollOptions = viewModel.getPollOptionsFromBindingMap()

        //get poll question
        val pollQuestion = binding.etPollQuestion.text.toString()

        //get poll expiry time
        val pollExpiryTime = viewModel.pollExpiryTime


        //validate the poll and enable/disable submit button
        if (pollQuestion.isNotEmpty() && pollOptions.size >= 2 && pollExpiryTime != null) {
            binding.headerViewCreatePoll.setSubmitButtonEnabled(true)
        } else {
            binding.headerViewCreatePoll.setSubmitButtonEnabled(false)
        }
    }
}