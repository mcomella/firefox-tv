/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mozilla.tv.firefox.session.SessionRepo

/**
 * todo:
 */
class FxaLoginUseCase(
    private val fxaRepo: FxaRepo,
    private val sessionRepo: SessionRepo
) {

    fun beginSignIn() {
        val authUriDeferred = fxaRepo.beginSignIn()
        GlobalScope.launch(Dispatchers.Main) {
            val authUri = authUriDeferred.await()
            if (authUri == null) {
                Log.d("lol", "Null AuthURI received")
                return@launch
            }
            sessionRepo.loadURL(Uri.parse(authUri)) // todo: loadURL when Activity is backgrounded is okay?
        }
    }

    init {
        init() // SuppressLint requires a method. todo: clairfy.
    }

    // call this on init
    @SuppressLint("CheckResult") // Should be active for the duration of the app
    fun init() {
        sessionRepo.state
            .map { it.currentUrl }
            .distinctUntilChanged()
            .subscribe {
                // todo: more declarative.
                if (it.startsWith(FxaRepo.REDIRECT_URI)) {
                    val uri = Uri.parse(it)
                    val code = uri.getQueryParameter("code")
                    val state = uri.getQueryParameter("state")
                    if (code != null && state != null) {
                        GlobalScope.launch(Dispatchers.Main) {
                            // todo: why await? if necessary, choose thread (bg?)
                            fxaRepo.accountManager.finishAuthenticationAsync(code, state).await()
                            Log.e("lol", "async auth! $code $state")
                        }
                    } else {
                        Log.e("lol", "async fail!")
                    }
                }
            }
    }
}