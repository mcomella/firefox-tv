package mozilla.dangertypes

// Types for the JavaScript API provided by Danger: https://danger.systems/js/reference.html

/**
 * Fails a build, outputting a specific reason for failing into a HTML table.
 */
external fun fail(markdownStr: String, fileName: String? = definedExternally, lineNumber: Int? = definedExternally)

/**
 * Adds raw markdown into the Danger comment, under the table
 */
external fun markdown(markdownStr: String, fileName: String? = definedExternally, lineNumber: Int? = definedExternally)

/**
 * Adds a message to the Danger table, the only difference between this and warn is the emoji which shows in the table.
 */
external fun message(markdownStr: String, fileName: String? = definedExternally, lineNumber: Int? = definedExternally)

/**
 * Highlights low-priority issues, but does not fail the build. Message is shown inside a HTML table.
 */
external fun warn(markdownStr: String, fileName: String? = definedExternally, lineNumber: Int? = definedExternally)
