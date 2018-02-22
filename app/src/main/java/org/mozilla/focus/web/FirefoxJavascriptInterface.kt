/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.web

import android.util.Log
import android.webkit.JavascriptInterface

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
        Log.d("lol", linkTagsJSON)
        // todo: save linkTagsJSON.
        // todo: prefetch icons.
    }
}
