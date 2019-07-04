/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Deferred
import mozilla.appservices.fxaclient.Config
import mozilla.components.concept.sync.AccountObserver
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceType
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.concept.sync.Profile
import mozilla.components.service.fxa.manager.DeviceTuple
import mozilla.components.service.fxa.manager.FxaAccountManager

//     init {
//         // User taps sign in button
//         val authenticationURi = accountManager.beginAuthenticationAsync().await()
//         webView.loadUrl(authenticationUri)
//
//         // user does stuff in WebView
//
//         // on success: WebView loads our REDIRECT_URI
//         // intercept load request if startsWith REDIRECT_URI to extract state & code from GET params (in URL)
//         accountManager.endAuthenticationAsync(state, code).await()
//
//         // presumably, update FxaRepo State (onAuthenticated? leverage accountManager observers)
//     }

/**
 * TODO
 */
class FxaRepo(
    context: Context
) {
    fun beginSignIn(): Deferred<String?> {
        return accountManager.beginAuthenticationAsync()
    }

    val accountManager = FxaAccountManager(
        context,
        Config.release(CLIENT_ID, REDIRECT_URI),
        applicationScopes = arrayOf("profile", "https://identity.mozilla.com/apps/oldsync"), // todo?
        deviceTuple = DeviceTuple(
            name = "A-C Sync Sample - ${System.currentTimeMillis()}", // todo
            type = DeviceType.MOBILE, // todo
            capabilities = listOf(DeviceCapability.SEND_TAB) // todo
        )
    )

    private val accountObserver = FirefoxAccountObserver() // todo: do we need a reference?

    init {
        accountManager.register(accountObserver)
        // TODO: register for device events

        @Suppress("DeferredResultUnused") // TODO disable button until init completes?
        accountManager.initAsync() // todo: await necessary? done in sample
    }

    // todo: encapsulate state in observable?
    private inner class FirefoxAccountObserver : AccountObserver {
        override fun onAuthenticated(account: OAuthAccount) {
            Log.d("lol", "onAuthenticated")
        }

        override fun onAuthenticationProblems() {
            Log.d("lol", "onAuthenticationProblems")
        }

        override fun onError(error: Exception) {
            Log.d("lol", "onError")
        }

        override fun onLoggedOut() {
            Log.d("lol", "onLoggedOut")
        }

        override fun onProfileUpdated(profile: Profile) {
            Log.d("lol", "onProfileUpdated")
        }
    }

    companion object {
        // todo: sample app client ID: use our credentials.
        const val CLIENT_ID = "a2270f727f45f648"
        val REDIRECT_URI = "https://accounts.firefox.com/oauth/success/$CLIENT_ID" // todo: which base url?
    }
}
