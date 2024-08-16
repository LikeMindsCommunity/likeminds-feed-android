package com.likeminds.feed.android.core.utils.video

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource.EventListener
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

            val cache = File(context.externalCacheDir, "lm_feed_video_cache")
            if (!cache.exists()) {
                cache.mkdirs()
            }

            sDownloadCache =
                SimpleCache(cache, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)

            return sDownloadCache!!
        }

        fun getCacheDataSourceFactory(context: Context, url: String = ""): CacheDataSource.Factory {
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
                    .setEventListener(object : EventListener {
                        override fun onCachedBytesRead(
                            cacheSizeBytes: Long,
                            cachedBytesRead: Long
                        ) {
//                            Log.d(
//                                "PUI",
//                                "onCachedBytesRead: cacheSizeBytes: ${cacheSizeBytes / (1024 * 1024)} cachedBytesRead: ${cachedBytesRead / (1024 * 1024)} url: $url"
//                            )
                        }

                        override fun onCacheIgnored(reason: Int) {
//                            Log.d(
//                                "PUI",
//                                "onCacheIgnored: $reason"
//                            )
                        }

                    })
//                    .setCacheReadDataSourceFactory(
//                        FileDataSource.Factory()
//                            .setListener(object : TransferListener {
//                            override fun onTransferInitializing(
//                                source: DataSource,
//                                dataSpec: DataSpec,
//                                isNetwork: Boolean
//                            ) {
//                                Log.d(
//                                    "PUI",
//                                    "onTransferInitializing: $isNetwork"
//                                )
//                            }
//
//                            override fun onTransferStart(
//                                source: DataSource,
//                                dataSpec: DataSpec,
//                                isNetwork: Boolean
//                            ) {
//                                Log.d(
//                                    "PUI",
//                                    "onTransferStart: $isNetwork"
//                                )
//                            }
//
//                            override fun onBytesTransferred(
//                                source: DataSource,
//                                dataSpec: DataSpec,
//                                isNetwork: Boolean,
//                                bytesTransferred: Int
//                            ) {
//                                Log.d(
//                                    "PUI",
//                                    "onBytesTransferred: $isNetwork"
//                                )
//                            }
//
//                            override fun onTransferEnd(
//                                source: DataSource,
//                                dataSpec: DataSpec,
//                                isNetwork: Boolean
//                            ) {
//                                Log.d(
//                                    "PUI",
//                                    "onTransferEnd: $isNetwork"
//                                )
//                            }
//
//                        })
//                    )
//                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            return cacheDataSourceFactory!!
        }
    }
}