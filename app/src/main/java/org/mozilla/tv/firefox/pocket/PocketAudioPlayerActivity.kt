/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.pocket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.mozilla.tv.firefox.R

/**
 * A player that allows the user to listen to Pocket content.
 *
 * This component is reusable within other Activities as the [PocketAudioPlayerFragment].
 *
 * TODO: what are args?
 */
class PocketAudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocket_audio_player)
        attachFragment(intent, isRestoring = savedInstanceState != null)
    }

    private fun attachFragment(intent: Intent, isRestoring: Boolean) {
        if (isRestoring) {
            return // Android reattaches our fragment for us: there's nothing for us to do.
        }

        val audioFile = intent.extras!!.getParcelable<PocketAudioPlayerFile>(PocketAudioPlayerFragment.KEY_AUDIO_FILE)!!
        val fragment = PocketAudioPlayerFragment.newInstance(audioFile)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment, PocketAudioPlayerFragment.FRAGMENT_TAG)
            .commit()
    }
}
