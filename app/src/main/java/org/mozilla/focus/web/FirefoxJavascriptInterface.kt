/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.web

import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONException
import org.mozilla.focus.ext.flatMapObj
import org.mozilla.focus.home.HomeTileIconManager
import org.mozilla.focus.web.dom.LinkTag

/** The receiver for Javascript we execute in a WebView.*/
class FirefoxJavascriptInterface {

    /**
     * Callback for the link tags from the page's DOM: extract_page_favicons.js.
     *
     * @param url URL of the page we extracted the favicons from.
     * @param linkTagsJSON A JSON array of link objects with the following keys: href, rel, type,
     * mask, sizes.
     */
    @JavascriptInterface
    fun onExtractPageFavicons(url: String, linkTagsJSON: String) {
        val linkTags = linkTagsJSONToLinkTag(linkTagsJSON) ?: return
        HomeTileIconManager.onReceivedLinkTags(url, linkTags)
    }
}

private fun linkTagsJSONToLinkTag(jsonStr: String): List<LinkTag>? {
    return try {
        JSONArray(jsonStr).flatMapObj { LinkTag.fromJSON(it) }
    } catch (e: JSONException) {
        null
    }
}
