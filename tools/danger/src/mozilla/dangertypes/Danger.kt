package mozilla.dangertypes

// Types for the JavaScript API provided by Danger: https://danger.systems/js/reference.html

external fun schedule() // incomplete
external fun peril() // incomplete

@JsName("danger")
external object Danger {
    /**
     * Details specific to the git changes within the code changes. Currently, this is just the
     * raw file paths that have been added, removed or modified.
     */
    val git: GitDSL

    /**
     * The BitBucket Server metadata. This covers things like PR info, comments and reviews
     * on the PR, related issues, commits, comments and activities. Null on GitHub.
     */
    val bitbucket_server: BitBucketServerDSL?

    /**
     * The GitHub metadata. This covers things like PR info, comments and reviews on the PR,
     * label metadata, commits with GitHub user identities and some useful utility functions for
     * displaying links to files.
     */
    val github: GitHubDSL?

    /**
     * Functions which are globally useful in most Dangerfiles. Right now, these functions
     * are around making sentences of arrays, or for making hrefs easily.
     */
    val utils: DangerUtilsDSL
}

external class BitBucketServerDSL // incomplete

external class DangerUtilsDSL {
    fun href(href: String?, text: String?): String?
}
