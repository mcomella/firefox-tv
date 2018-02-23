/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.web.dom

import org.json.JSONObject

/** A representation of a <link> DOM element. */
data class LinkTag(
        val href: String,
        val rel: String,
        val type: String?,
        val mask: String?,

        /** The sizes attr is e.g. "32x32" - this is just the first number.  */
        val size: Int?
) {

    companion object {
        fun fromJSON(obj: JSONObject): LinkTag? {
            val href = obj.optString("href")
            val rel = obj.optString("rel")
            if (href == null || rel == null) {
                return null
            }

            return LinkTag(
                    href = href,
                    rel = rel,
                    type = obj.optString("type"),
                    mask = obj.optString("mask"),
                    size = obj.optString("sizes")?.takeWhile { it.isDigit() }?.toIntOrNull()
            )
        }
    }
}
