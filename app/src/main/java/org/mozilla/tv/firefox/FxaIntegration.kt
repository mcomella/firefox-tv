/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox

import android.content.Context
import mozilla.appservices.fxaclient.Config
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceType
import mozilla.components.service.fxa.manager.DeviceTuple
import mozilla.components.service.fxa.manager.FxaAccountManager

/**
 * TODO
 */
class FxaIntegration(
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

    companion object {
        // todo: sample app client ID: use our credentials.
        const val CLIENT_ID = "a2270f727f45f648"
        val REDIRECT_URI = "https://accounts.firefox.com/oauth/success/$CLIENT_ID" // todo: which base url?
    }
}
