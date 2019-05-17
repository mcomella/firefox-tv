/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.pocket

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.NONE
import androidx.annotation.VisibleForTesting.PRIVATE
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// todo: improve explanation.
/**
 * Provides calculations for when Pocket needs to fetch new content.
 *
 * For all users, we want to fetch when users are most likely to be inactive (i.e. when they're sleeping, like
 * Windows updates). Ideally, we could tell WorkManager to fetch at a random time between these hours: the random
 * time prevents every device from accessing the server at the same time, potentially causing on overload. However,
 * when scheduling periodic jobs, WorkManager makes no guarantees that it won't run at the very beginning of the
 * interval (i.e. all devices access the server at the same time). To solve this problem, inside the "user is likely
 * sleeping" fetch interval, each user is assigned a smaller window in which to fetch which starts at a random time
 * within the fetch interval. For example:
 *
 * | 0    1am  2    3    4    5    6    7    8... |
 * |           | Fetch interval    |              |
 * |              | User1 |                       |
 * |                  | User2 |                   |
 */
class PocketFetchCalculator {

    /**
     * TODO: EXPLAIN
     */
    fun getPeriodicWorkInitialDelayMillis(
        @VisibleForTesting(otherwise = NONE) now: Calendar = Calendar.getInstance(),
        @VisibleForTesting(otherwise = NONE) fetchInterval: TimeInterval = FETCH_INTERVAL,
        @VisibleForTesting(otherwise = NONE) fetchWindowDurationSeconds: Int = FETCH_WINDOW_DURATION_SECONDS,
        @VisibleForTesting(otherwise = NONE) randInt: (Int) -> Int = { Random.nextInt(it) }
    ): Long {
        // We're calculating when this device's fetch window should start inside the fetch interval for all users.
        // We calculate the max fetch window start time to ensure the fetch window end time does not exceed the
        // all-device fetch interval.
        val maxFetchWindowStartOffsetSeconds = fetchInterval.durationSeconds - fetchWindowDurationSeconds

        val fetchWindowStartOffsetSeconds = randInt(maxFetchWindowStartOffsetSeconds)
        val fetchWindowStartTime = (fetchInterval.startTime.clone() as Calendar).apply {
            add(Calendar.SECOND, fetchWindowStartOffsetSeconds) // will roll over minute & hour fields.
        }

        return fetchWindowStartTime.timeInMillis - now.timeInMillis
    }

    companion object {
        @VisibleForTesting(otherwise = PRIVATE) val FETCH_INTERVAL = TimeInterval(startTime = Calendar.getInstance().apply {
            set(1, 1, 1, 3, 0, 0)
        }, endTime = Calendar.getInstance().apply {
            set(1, 1, 1, 5, 0, 0)
        })

        @VisibleForTesting(otherwise = PRIVATE) val FETCH_WINDOW_DURATION_SECONDS = TimeUnit.HOURS.toSeconds(1).toInt()
    }
}

/**
 * A data container to represent an interval by holding the interval's start and end times.
 *
 * @param startTime the start time of the interval; the date fields are overwritten; DO NOT MUTATE
 * @param endTime the end time of the interval; the date fields are overwritten; DO NOT MUTATE
 */
@VisibleForTesting(otherwise = PRIVATE)
class TimeInterval(
    val startTime: Calendar,
    val endTime: Calendar
) {

    init {
        // We want to compare times without the date so we make the date fields the same.
        startTime.set(1, 1, 1)
        endTime.set(1, 1, 1)

        require(startTime.before(endTime))
    }

    val durationSeconds get() = TimeUnit.MILLISECONDS.toSeconds(endTime.timeInMillis - startTime.timeInMillis).toInt()
}
