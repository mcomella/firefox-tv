/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.utils

import android.content.Context
import mozilla.components.concept.fetch.Client
import mozilla.components.lib.fetch.okhttp.OkHttpClient
import java.io.IOException

/**
 * A holder for our shared HTTP [Client]. This class is intended to allow developers
 * to swap HTTP client implementations without changing the calling code.
 */
class Http(
    applicationContext: Context
) {

    /** @see [client] */
    @Deprecated("Use client. New code should not access a specific implementation.")
    val rawClient = okhttp3.OkHttpClient.Builder().build()

    @Suppress("DEPRECATION") // rawClient is only deprecated because it's public.
    val client = OkHttpClient(rawClient, applicationContext.applicationContext)

    fun onLowMemory() {
        try {
            @Suppress("DEPRECATION") // rawClient is only deprecated because it's public.
            rawClient.cache()?.evictAll()
        } catch (_: IOException) { /* We don't care. */ }
    }
}
