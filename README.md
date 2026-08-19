# LikeMinds Feed SDK for Android

Drop-in social feed for Android apps, in Kotlin. Posts, comments, likes, polls and topics, in social,
Q&A or video form.

[![Maven Central](https://img.shields.io/maven-central/v/community.likeminds/likeminds-feed-core.svg)](https://central.sonatype.com/artifact/community.likeminds/likeminds-feed-core)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

**Docs:** https://docs.likeminds.io/

## What you get

Universal and personalised feeds · posts with text, images, video, documents and PDFs, link previews,
polls and custom widgets · comments with nested replies · likes with liker lists · topics and
topic-filtered feeds · @-mentions · save, pin, hide, repost, share with deep links · search ·
activity and notification feed · report and a **pending-post moderation queue** · member rights
editing · background upload with progress and retry · push notifications.

16 documented screens across 24 fragment classes.

## Install

```groovy
implementation 'community.likeminds:likeminds-feed-core:1.10.0'
```

Then in `Application.onCreate()`:

```kotlin
LMFeedCore.setup(...)
LMFeedCore.showFeed(...)
```

Inflate whichever feed shape you need: `LMFeedSocialFeedFragment`, `LMFeedQnAFeedFragment` or
`LMFeedVideoFeedFragment`.

The data layer is pulled in transitively:

```groovy
implementation 'community.likeminds:likemindsfeed:1.16.0'
```

Source at [likeminds-feed-android-data](https://github.com/LikeMindsCommunity/likeminds-feed-android-data).

## Theming

Every widget ships a paired `*ViewStyle` class routed through `LMFeedStyleTransformer`, with global
appearance set via `LMFeedAppearance` - roughly 45 documented widgets. A complete restyle needs no
subclassing. See the
[minimal theme example](https://github.com/LikeMindsCommunity/likeminds-feed-android-social-feed-theme).

## Samples

`social-feed`, `qna-feed` and `video-feed`. The last also demonstrates a custom post renderer.

## Requirements

minSdk 21 · Java 17 · Kotlin 1.8.22

## Built on

ExoPlayer · Glide · AWS S3 · Firebase · plus the standalone
[custom gallery](https://github.com/LikeMindsCommunity/likeminds-android-custom-gallery) and
[user tagging](https://github.com/LikeMindsCommunity/likeminds-android-user-tagging) libraries

## Contributing

See the org-wide [contributing guide](https://github.com/LikeMindsCommunity/.github/blob/master/.github/CONTRIBUTING.md).
Security issues go to **hi@likeminds.community**, not the issue tracker.

## License

Apache 2.0. See [LICENSE](LICENSE).
