package com.likeminds.feed.android.core.utils.video

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.likeminds.feed.android.core.R
import java.io.File

class LMFeedVideoCache {
    companion object {
        private var sDownloadCache: SimpleCache? = null
        private var cacheDataSourceFactory: CacheDataSource.Factory? = null

        private fun getCache(context: Context): SimpleCache {
            if (sDownloadCache != null) {
                return sDownloadCache!!
            }

            val exoPlayerCacheSize = 750 * 1024 * 1024.toLong()// Set the size of cache for video
            val leastRecentlyUsedCacheEvictor =
                LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = StandaloneDatabaseProvider(context)

            val cache = File(context.cacheDir, "lm_feed_video_cache")
            if (!cache.exists()) {
                cache.mkdirs()
            }

            sDownloadCache =
                SimpleCache(cache, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)

            return sDownloadCache!!
        }

        fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
            if (cacheDataSourceFactory != null) {
                return cacheDataSourceFactory!!
            }

            getCache(context)

            cacheDataSourceFactory =
                CacheDataSource.Factory()
                    .setCache(sDownloadCache!!)
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory()
                            .setUserAgent(
                                Util.getUserAgent(
                                    context, context.getString(
                                        R.string.lm_feed_app_name
                                    )
                                )
                            )
                    )
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            return cacheDataSourceFactory!!
        }
    }
}