# Keeping all models

-keep class com.likeminds.feed.android.core.activityfeed.model.** { *; }
-keep class com.likeminds.feed.android.core.delete.model.** { *; }
-keep class com.likeminds.feed.android.core.likes.model.** { *; }
-keep class com.likeminds.feed.android.core.overflowmenu.model.** { *; }
-keep class com.likeminds.feed.android.core.post.model.** { *; }
-keep class com.likeminds.feed.android.core.post.detail.model.** { *; }
-keep class com.likeminds.feed.android.core.post.edit.model.** { *; }
-keep class com.likeminds.feed.android.core.pushnotification.model.** { *; }
-keep class com.likeminds.feed.android.core.report.model.** { *; }
-keep class com.likeminds.feed.android.core.topics.model.** { *; }
-keep class com.likeminds.feed.android.core.topics.model.** { *; }
-keep class com.likeminds.feed.android.core.topicselection.model.** { *; }
-keep class com.likeminds.feed.android.core.ui.theme.model.** { *; }
-keep class com.likeminds.feed.android.core.universalfeed.model.** { *; }
-keep class com.likeminds.feed.android.core.utils.base.model.** { *; }
-keep class com.likeminds.feed.android.core.utils.pluralize.model.** { *; }

# Enums are not obfuscated correctly in combination with Gson
-keepclassmembers enum * { *; }

# Kotlin
-keep class kotlin.coroutines.Continuation