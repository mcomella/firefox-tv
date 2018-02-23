/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.ext

import org.json.JSONArray
import org.json.JSONObject

/** TODO: doc. */
inline fun <R> JSONArray.flatMapObj(transform: (JSONObject) -> R?): List<R> {
    val transformedList = mutableListOf<R>()
    for (i in 0 until length()) {
        val transformedVal = transform(getJSONObject(i)) ?: continue
        transformedList.add(transformedVal)
    }
    return transformedList
}

