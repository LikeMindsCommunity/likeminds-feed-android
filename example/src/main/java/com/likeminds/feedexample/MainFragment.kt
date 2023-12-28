package com.likeminds.feedexample

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.likeminds.feed.android.ui.base.styles.LMFeedTextStyle
import com.likeminds.feed.android.ui.base.styles.setStyle
import com.likeminds.feedexample.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    companion object {
        /**
         * creates an instance of fragment
         **/
        @JvmStatic
        fun getInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        //set Customizable Textview
        setCustomizableTextView()
        return binding.root
    }

    protected open fun setCustomizableTextView() {
        val viewStyle = LMFeedTextStyle.Builder()
            .textSize(30)
            .textColor(com.likeminds.feed.android.ui.R.color.purple_200)
            .build()
        binding.tvCustomisable.setStyle(viewStyle)
    }
}