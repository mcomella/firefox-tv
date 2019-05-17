/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.pocket

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class PocketFetchCalculatorTest {

    private lateinit var calculator: PocketFetchCalculator

    @Before
    fun setUp() {
        calculator = PocketFetchCalculator()
    }

    @Test
    fun `GIVEN the fetch window and fetch interval constants THEN ensure the fetch window plus some delta fits within the fetch interval`() {
        with(PocketFetchCalculator) {
            assertTrue(FETCH_WINDOW_DURATION_SECONDS < FETCH_INTERVAL.durationSeconds) // first: a sanity check.

            // If the fetch window is too large compared to the fetch interval, all jobs may start around the same time
            // because the fetch window has nowhere to move within the fetch interval. As such, we verify there's an
            // arbitrary amount of wiggle room (the delta) for the fetch window to move around.
            val deltaSeconds = TimeUnit.HOURS.toSeconds(1)
            assertTrue(FETCH_WINDOW_DURATION_SECONDS + deltaSeconds <= FETCH_INTERVAL.durationSeconds)
        }
    }

    @Test
    fun getPeriodicInterval() {
        // todo: write while pairing
    }
}
