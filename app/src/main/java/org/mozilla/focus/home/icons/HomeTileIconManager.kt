/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home.icons

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import org.mozilla.focus.R
import org.mozilla.focus.ext.withRoundedCorners
import java.util.UUID

private const val MAX_CACHE_COUNT = 60 // Each screenshot is ~0.5 MiB so this is ~30 MiB.

/**
 * TODO: docs, add caching
 * - Are animations for first run and new tiles acceptable?
 * - Low memory.
 */
object HomeTileIconManager {

    private val screenshotStore = HomeTileScreenshotStore
    private val cache = LruCache<UUID, Deferred<Bitmap>>(MAX_CACHE_COUNT)

    fun removeAsync(context: Context, uuid: UUID): Job {
        Log.d("lol", "test")
        cache.remove(uuid)
        return screenshotStore.removeAsync(context, uuid)
    }

    fun readMaybeAsync(context: Context, uuid: UUID, url: String): Deferred<Bitmap> = synchronized(cache) {
        val cachedVal = cache.get(uuid)
        if (cachedVal != null) return cachedVal

        val deferredResult = async {
            val res = context.resources
            val screenshot = HomeTileScreenshotStore.read(context, uuid)
            if (screenshot != null) {
                screenshot.withRoundedCorners(res.getDimension(R.dimen.home_tile_corner_radius))
            } else {
                val cornerRadius = res.getDimension(R.dimen.home_tile_placeholder_corner_radius)
                HomeTilePlaceholderGenerator.generate(context, url).withRoundedCorners(cornerRadius)
            }
        }

        cache.put(uuid, deferredResult)
        deferredResult
    }
}
