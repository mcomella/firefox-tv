/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.gradle.process.internal.ExecException // internal but it works and its what we need.

// Autocomplete will be slow unless you increase your max heap size:
//   https://developer.android.com/studio/intro/studio-config#adjusting_heap_size
//
// The IDE isn't reading the Kotlin DSL correctly:
// - get/set* isn't transformed into *; we use the old form to preserve autocompletion
// - doFirst/Last is undefined; we use it anyway because it's minimally disruptive

plugins {
    id("kotlin2js")
}

kotlin.sourceSets["main"].kotlin.setSrcDirs(listOf("src"))

getTasks().getByName<Kotlin2JsCompile>("compileKotlin2Js").kotlinOptions {
    main = "call"
    moduleKind = "commonjs"
}

getTasks().create<Exec>("npmInstall") {
    setDescription("Installs npm dependencies; requires nodejs to be installed.")

    commandLine("sh", "-c", "npm install")
    setIgnoreExitValue(true) // we'll handle the error message ourselves.

    doLast {
        if (getExecResult().getExitValue() != 0) {
            throw ExecException("`npm install` exited with non-zero exit code: is nodejs installed and in your PATH?")
        }
    }
}

/**
 * A task that executes a Danger command.
 *
 * To use, set the args passed to the `danger` command and
 * optionally set a description.
 */
open class DangerTask : Exec() {
    init {
        this.setGroup("Verification")

        // We use explicit this to protect us from someone from overriding these methods.
        this.dependsOn("npmInstall", "compileKotlin2Js")

        // danger only operates on <working-dir>/dangerfile.js so we
        // change the working dir to the compiled output dangerfile.
        this.workingDir("build/classes/kotlin/main")
        this.executable("../../../../node_modules/.bin/danger")
    }
}

getTasks().create<DangerTask>("runPR") {
    setDescription("Runs your local Dangerfile against an existing GitHub PR. Will " +
            "not post on the PR: `danger pr <pr-url>`. Expects -Ppr=<pr-url> property.")

    doFirst {
        // Execute property fetching as part of the task
        // so validation doesn't fail at configure time.
        val usageText = "Usage: ./gradlew danger:runPR -Ppr=<pr-url>"
        val prArg = getProject().findProperty("pr") as? String
                ?: throw ExecException("Missing required pull request argument.\n  $usageText")
        args("pr", prArg)
    }
}

getTasks().create<DangerTask>("runContinuousIntegration") {
    setDescription("Runs Danger on CI: `danger ci`")
    args("ci")
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-js"))
    testCompile(kotlin("test-js"))
}
