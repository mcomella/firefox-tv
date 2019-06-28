/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.ext

import org.json.JSONArray
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JSONKtTest {

    @Test
    fun `WHEN take is called on a JSONArray greater than length three THEN it is truncated to length three`() {
        val args = listOf(4, 5, 6, 7, 8)

        val input = JSONArray().apply {
            args.forEach { put(it) }
        }

        val expected = JSONArray().apply {
            args.take(3).forEach { put(it) }
        }

        assertEquals(expected, input.take(3))
    }
}
