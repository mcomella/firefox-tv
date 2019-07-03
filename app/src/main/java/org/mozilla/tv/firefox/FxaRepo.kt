/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox

import android.content.Context
import mozilla.appservices.fxaclient.Config
import mozilla.components.concept.sync.AccountObserver
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceType
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.concept.sync.Profile
import mozilla.components.service.fxa.manager.DeviceTuple
import mozilla.components.service.fxa.manager.FxaAccountManager

/**
 * TODO
 */
class FxaRepo(
    context: Context
) {
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

    private val accountObserver = FirefoxAccountObserver()

    init {
        accountManager.register(accountObserver)
        // TODO: register for device events
    }

    private inner class FirefoxAccountObserver : AccountObserver {
        override fun onAuthenticated(account: OAuthAccount) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onAuthenticationProblems() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onError(error: Exception) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLoggedOut() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProfileUpdated(profile: Profile) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    companion object {
        // todo: sample app client ID: use our credentials.
        const val CLIENT_ID = "a2270f727f45f648"
        val REDIRECT_URI = "https://accounts.firefox.com/oauth/success/$CLIENT_ID" // todo: which base url?
    }
}
