/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla

import mozilla.dangertypes.Danger
import mozilla.dangertypes.markdown
import mozilla.dangertypes.warn
import kotlin.js.Promise

// NB: Our interface to the Danger DSL may not be fully implemented
// and is not fully tested: you have been warned!
fun main(args: Array<String>) {
    checkNewFileJVMLicenseHeaders()
}

private fun checkNewFileJVMLicenseHeaders() {
    val expectedLicenseLines = Licenses.JVM.lines()
    fun hasValidLicense(contents: String): Boolean =
            contents.lines().take(expectedLicenseLines.size) == expectedLicenseLines

    // We rely on the use of createdFiles: see comment below.
    val jvmFiles = Danger.git.createdFiles.filter(Files::isJVMCodeFile)
    val fileDiffsDeferred = jvmFiles.map { Danger.git.diffForFile(it) }.let { Promise.all(it.toTypedArray()) }

    fileDiffsDeferred.then { fileDiffs ->
        val filesWithoutLicenses = jvmFiles.zip(fileDiffs).filter { (_, diff) ->
            !hasValidLicense(diff.after)
        }.map { it.first }

        filesWithoutLicenses.forEach {
            // The line number needs to exist in the diff or this violation may not get posted.
            // Since the current code operates on created files, all lines should be in the diff.
            //
            // We warn, not fail, because some *.kt files do not use the MPL header, e.g. third party code.
            warn("File `$it` contains no license or license is improperly formatted.", it, 1)
        }

        if (filesWithoutLicenses.isNotEmpty()) {
            markdown("## Expected JVM license format\n```\n${Licenses.JVM}\n```")
        }
    }
}
