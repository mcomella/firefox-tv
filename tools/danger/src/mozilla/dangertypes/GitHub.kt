package mozilla.dangertypes

// Types for the JavaScript API provided by Danger: https://danger.systems/js/reference.html

external class GitHubDSL {
    val api: GitHub
    val commits: Array<GitHubCommit>
    val pr: GitHubPRDSL

    @JsName("requested_reviewers")
    val requestedReviewers: Array<GitHubUser>

    val reviews: Array<GitHubReview>
    val thisPR: GitHubAPIPR
    val utils: GitHubUtilsDSL
}

external class GitHub // incomplete: full API https://octokit.github.io/rest.js/

external class GitHubCommit {
    val author: GitHubUser
    val commit: GitCommit
    val committer: GitHubUser
    val parents: Array<Any> // incomplete
    val sha: String
    val url: String
}

external class GitHubUser {
    @JsName("avatar_url")
    val avatarUrl: String
    val id: Int
    val login: String
    val type: String // User, Organization, or Bot
}

external class GitHubPRDSL { // incomplete: https://danger.systems/js/reference.html#GitHubPRDSL
    val additions: Int
    val assignee: GitHubUser
    val assignees: Array<GitHubUser>
    val base: Any // incomplete
    val body: String

    @JsName("changed_files")
    val changedFiles: Int

    @JsName("closed_at")
    val closedAt: String?

    val comments: Int
    val commits: Int

    @JsName("created_at")
    val createdAt: String

    val deletions: Int
    val head: Any // incomplete
    val locked: Boolean
    val merged: Boolean

    @JsName("merged_at")
    val mergedAt: String?

    val number: Int

    @JsName("review_comments")
    val reviewComments: Int

    val state: String // closed, open, locked, or merged
    val title: String

    @JsName("updated_at")
    val updatedAt: String

    val user: GitHubUser
}

external class GitHubReview {
    val body: String // nullable?

    @JsName("commit_id")
    val commitId: String // nullable?

    val id: Int // nullable?
    val state: String // APPROVED, REQUEST_CHANGES, COMMENT, or PENDING
    val user: GitHubUser
}

external class GitHubAPIPR {
    val number: Int
    val owner: String
    val repo: String
}

external class GitHubUtilsDSL // incomplete: https://danger.systems/js/reference.html#GitHubUtilsDSL
