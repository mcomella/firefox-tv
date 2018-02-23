/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home

import org.mozilla.focus.web.dom.LinkTag
import java.util.Locale

/**
 * A whitelist of <link> rel attr that represent icons we know how to handle.
 *
 * This list is inspired by desktop:
 * https://searchfox.org/mozilla-central/rev/47cb352984bac15c476dcd75f8360f902673cb98/browser/modules/ContentLinkHandler.jsm#324
 */
private val REL_ICON_WHITELIST = sortedSetOf(
        // TODO: ignores /favicon.ico approach, which apple.com takes.
        // TODO: "shortcut icon" also seems popular but unused by desktop.
        "icon", // Regular favicon.
        "apple-touch-icon", // High-res icons for touch screens.
        "apple-touch-icon-precomposed", // ^ without iOS-like effects added to it.
        "fluid-icon" // Icon used by Fluid, which makes web apps into macOS desktop apps.
)

/** Manages home tile icons. */
object HomeTileIconManager {

    // TODO: How to pass around instance?

    fun onReceivedLinkTags(url: String, linkTags: List<LinkTag>) {
        val filteredTags = filterLinkTags(linkTags)
        // TODO: Cache filtered tags
        // TODO: Prefetch icons
    }

    private fun filterLinkTags(linkTags: List<LinkTag>) = linkTags.filter {
        REL_ICON_WHITELIST.contains(it.rel.toLowerCase(Locale.US)) &&

                // I don't know how to handle "mask" attrs and desktop doesn't handle them either:
                // https://searchfox.org/mozilla-central/rev/47cb352984bac15c476dcd75f8360f902673cb98/browser/modules/ContentLinkHandler.jsm#330
                it.mask.isNullOrBlank()
    }
}