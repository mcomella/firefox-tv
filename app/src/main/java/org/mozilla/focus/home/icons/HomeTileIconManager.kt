/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home.icons

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import kotlinx.coroutines.experimental.Job
import java.util.UUID

/**
 * TODO: docs, add caching
 */
object HomeTileIconManager {
    private val screenshotStore = HomeTileScreenshotStore

    fun removeAsync(context: Context, uuid: UUID): Job {
        return screenshotStore.removeAsync(context, uuid)
    }

    @WorkerThread // file access
    suspend fun read(context: Context, uuid: UUID): Bitmap? {
        return screenshotStore.read(context, uuid)
    }
}
