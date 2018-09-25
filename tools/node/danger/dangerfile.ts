/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// See README.md for more information.

import { danger, fail, markdown } from 'danger';
import { LICENSE_MPL_JVM, LICENSE_MPL_JVM_LINES } from './helpers/licenses';
import { isJVMCodeFile } from './helpers/paths';

function runChecks() {
    checkJVMLicenseHeaders();
}

function checkJVMLicenseHeaders() {
    async function doesJVMFileContainLicense(jvmFile: string) {
        const diff = await danger.git.diffForFile(jvmFile);
        const diffLines = diff.after.split('\n');

        const isValidLicense = LICENSE_MPL_JVM_LINES.filter((mplLine, i) => {
            return mplLine !== diffLines[i]; // returns undefined if out of bounds.
        }).length === 0;

        return {
            filePath: jvmFile,
            isValidLicense: isValidLicense
        }
    }

    Promise.all(danger.git.created_files.filter(isJVMCodeFile).map(doesJVMFileContainLicense)).then(results => {
        const resultsWithoutLicenses = results.filter(result => !result.isValidLicense);
        resultsWithoutLicenses.forEach(result => {
            fail('Added file contains no license or license is improperly formatted.', result.filePath, 0);
        });

        if (resultsWithoutLicenses.length > 0) {
            markdown(`## JVM Licenses\nExpected *.kt/*.java license format on line 1:\n\`\`\`kotlin\n${LICENSE_MPL_JVM}\`\`\``);
        }
    });
}

runChecks();
