/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home

import org.mozilla.focus.web.dom.LinkTag

/** Manages home tile icons. */
object HomeTileIconManager {

    // TODO: How to pass around instance?

    fun onReceivedLinkTags(url: String, linkTags: List<LinkTag>) {
        // TODO: Filter tags on icons
        // TODO: Cache filtered tags
        // TODO: Prefetch icons
    }
}