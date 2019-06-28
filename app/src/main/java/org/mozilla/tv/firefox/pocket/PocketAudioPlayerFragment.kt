/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.pocket

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_audio_player.view.*
import org.mozilla.tv.firefox.R

/**
 * A player that allows the user to listen to Pocket content.
 *
 * The core functionality is in a fragment, rather than directly in [PocketAudioPlayerActivity],
 * so it's easy to move later.
 *
 * TODO: what are args?
 */
class PocketAudioPlayerFragment : Fragment() {

    private val mediaPlayer = MediaPlayer().apply {
    }

    private lateinit var audioFile: PocketAudioPlayerFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioFile = arguments!!.getParcelable(KEY_AUDIO_FILE)!!
        with(mediaPlayer) {
            setDataSource(audioFile.audioPath)
            prepare() // todo: async?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_audio_player, container, false)

        rootView.playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer.start()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }

    companion object {
        fun newInstance(audioFile: PocketAudioPlayerFile): PocketAudioPlayerFragment = PocketAudioPlayerFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_AUDIO_FILE, audioFile)
            }
        }

        const val FRAGMENT_TAG = "PocketAudioPlayer"
        const val KEY_AUDIO_FILE = "audioFile"
    }
}

data class PocketAudioPlayerFile(
    val title: String,
    val imageSrc: String,
    val audioPath: String
) : Parcelable {

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(imageSrc)
        parcel.writeString(audioPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PocketAudioPlayerFile> {
        override fun createFromParcel(parcel: Parcel): PocketAudioPlayerFile {
            return PocketAudioPlayerFile(parcel)
        }

        override fun newArray(size: Int): Array<PocketAudioPlayerFile?> {
            return arrayOfNulls(size)
        }
    }
}
