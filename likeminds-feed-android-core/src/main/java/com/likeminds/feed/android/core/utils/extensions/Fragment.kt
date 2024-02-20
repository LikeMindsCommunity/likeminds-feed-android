package com.likeminds.feed.android.core.utils.extensions

import androidx.fragment.app.Fragment

internal inline fun <reified T> Fragment.findListener(): T? {
    return (parentFragment as? T) ?: (activity as? T)
}