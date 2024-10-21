package com.likeminds.feed.android.core.search.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feed.android.core.databinding.LmFeedSearchActivityBinding
import com.likeminds.feed.android.core.post.detail.view.LMFeedPostDetailActivity

open class LMFeedSearchActivity : AppCompatActivity() {

    lateinit var binding: LmFeedSearchActivityBinding

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, LMFeedSearchActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, LMFeedSearchActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedSearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inflates search feed fragment
        inflateSearchFeedFragment()

    }

    protected open fun inflateSearchFeedFragment() {
        //gets feed search fragment instance
        val feedSearchFragment =
            LMFeedSearchFragment.getInstance()

        //commits fragment replace transaction
        supportFragmentManager.beginTransaction()
            .replace(
                binding.containerSearch.id,
                feedSearchFragment,
                LMFeedPostDetailActivity.TAG
            )
            .commit()
    }

}
