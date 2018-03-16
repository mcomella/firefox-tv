/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.exto

import android.os.StrictMode

/** Runs the given function under `this` thread policy and reverts it after the function completes. */
inline fun <R> StrictMode.ThreadPolicy.use(functionBlock: () -> R): R {
    val returnValue = functionBlock()
    StrictMode.setThreadPolicy(this)
    return returnValue
}

