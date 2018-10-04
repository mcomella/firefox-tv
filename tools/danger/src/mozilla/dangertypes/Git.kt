package mozilla.dangertypes

import kotlin.js.Promise

// Types for the JavaScript API provided by Danger: https://danger.systems/js/reference.html

/**
 * The git specific metadata for a PR.
 */
external class GitDSL {
    val commits: Array<GitCommit>

    @JsName("created_files")
    val createdFiles: Array<String>

    @JsName("deleted_files")
    val deletedFiles: Array<String>

    @JsName("modified_files")
    val modifiedFiles: Array<String>

    @JsName("JSONDiffForFile")
    fun jsonDiffForFile(fileName: String): Promise<JSONDiff>

    @JsName("JSONPatchForFile")
    fun jsonPatchForFile(fileName: String): Promise<JSONPatch>

    fun diffForFile(fileName: String): Promise<TextDiff>
    fun structuredDiffForFile(fileName: String): Promise<StructuredFileDiff>
}

external class GitCommit {
    val author: GitCommitAuthor
    val committer: GitCommitAuthor
    val message: String
    val parents: Array<Any>? // incomplete
    val sha: String
    val tree: Any // incomplete
    val url: String
}

external class GitCommitAuthor {
    /** ISO6801 date string */
    val date: String
    val email: String
    val name: String
}

external class JSONPatch { // incomplete
    val after: Any
    val before: Any
    val diff: Array<JSONPatchOperation>
}

external class JSONPatchOperation {
    val op: String
    val path: String
    val value: String
}

typealias JSONDiff = Map<String, JSONDiffValue>

external class JSONDiffValue { // incomplete
    val added: Array<Any>
    val after: Any
    val before: Any
    val removed: Array<Any>
}

external class TextDiff {
    val added: String
    val after: String
    val before: String
    val diff: String
    val removed: String
}

external class StructuredFileDiff {
    val chunks: Array<Any> // incomplete
}
