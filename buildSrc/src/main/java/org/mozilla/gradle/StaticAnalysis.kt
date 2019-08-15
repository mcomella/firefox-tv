/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.gradle

import org.gradle.api.GradleException
import java.io.File

/**
 * A collection of functions to run static analysis on the build or its outputs.
 */
object StaticAnalysis {

    // TODO: this should be a task that automatically inserts itself into the Android assemble tasks.
    // TODO: this should go in the shared Android plugin.
    @JvmStatic
    fun validateAPKSize(maxMB: Float, apkPath: String) {
        val apk = File(apkPath)
        if (!apk.exists()) {
            throw GradleException("validateAPKSize function: expected APK file to exist: $apkPath")
        }

        val apkMB = apk.length() / (1024 * 1024) // convert bytes to MiB.
        println("APK size: $apkMB MiB")

        if (apkMB > maxMB) {
            throw GradleException("APK size - $apkMB - exceeds expected maximum $maxMB MiB. " +
                "Reduce APK size or update expected APK size in app/build.gradle.")
        }
    }
}
