package com.likeminds.feed.android.core.ui.widgets.poll.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.likeminds.feed.android.core.R
import com.likeminds.feed.android.core.databinding.LmFeedDialogFragmentAnonymousPollBinding
import com.likeminds.feed.android.core.ui.widgets.alertdialog.view.LMFeedAlertDialogView
import com.likeminds.feed.android.core.utils.LMFeedNavigationFragments
import com.likeminds.feed.android.core.utils.LMFeedStyleTransformer

open class LMFeedAnonymousPollDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "LMFeedAnonymousPollDialogFragment"

        @JvmStatic
        fun showDialog(supportFragmentManager: FragmentManager) {
            LMFeedNavigationFragments.getInstance().getAnonymousPollDialogFragment().show(supportFragmentManager, TAG)
        }
    }

    private lateinit var binding: LmFeedDialogFragmentAnonymousPollBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LmFeedDialogFragmentAnonymousPollBinding.inflate(layoutInflater)

        customizeAnonymousPollDialog(binding.alertDialogAnonymousPoll)
        return binding.root
    }

    //customize anonymous poll dialog
    protected open fun customizeAnonymousPollDialog(alertDialogAnonymousPoll: LMFeedAlertDialogView) {
        alertDialogAnonymousPoll.setStyle(
            LMFeedStyleTransformer.alertDialogAnonymousPoll.anonymousPollDialogStyle
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        binding.alertDialogAnonymousPoll.apply {
            setAlertTitle(getString(R.string.lm_feed_anonymous_poll))
            setAlertSubtitle(getString(R.string.lm_feed_this_being_an_lm_feed_anonymous_poll_the_names_of_the_voters_cannot_be_disclosed))
            setAlertPositiveButtonText(getString(R.string.lm_feed_okay))
            setPositiveButtonEnabled(true)
            setPositiveButtonClickListener {
                onAnonymousPollPositiveButtonClick()
            }
        }
    }

    //processes the anonymous poll dialog positive button click
    protected open fun onAnonymousPollPositiveButtonClick() {
        dismiss()
    }
}