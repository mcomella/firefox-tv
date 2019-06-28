/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.pocket

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mozilla.components.service.pocket.PocketListenEndpoint
import mozilla.components.service.pocket.data.PocketListenArticleMetadata
import mozilla.components.service.pocket.net.PocketResponse
import mozilla.components.support.ktx.android.org.json.mapNotNull
import org.json.JSONArray
import org.json.JSONException
import org.mozilla.tv.firefox.channels.ChannelTile
import org.mozilla.tv.firefox.channels.ImageSetStrategy
import org.mozilla.tv.firefox.channels.TileSource
import org.mozilla.tv.firefox.ext.take

private const val LOGTAG = "PocketListenRepo"
private const val SOURCE_ARTICLES_PATH = "bundled/example_pocket_articles.json"

/**
 * Manages and exposes the state for listening to Pocket articles.
 */
class PocketListenRepo(
    context: Context,
    @Suppress("unused") private val isPocketEnabledByLocale: () -> Boolean, // TODO: use me!
    private val endpoint: PocketListenEndpoint
) {

    // TODO: use final article source & identify/implement update strategy.
    private val dummyArticles = context.assets.open(SOURCE_ARTICLES_PATH).use {
        it.bufferedReader().readText()
    }.let { JSONArray(it) }

    private val listenArticles: Observable<List<PocketListenArticle>> = Observable.just(dummyArticles)
        .observeOn(Schedulers.io())
        .map {
            // The server takes time to access and we want to reduce server load so we only use the first few.
            val articlesToTransform = dummyArticles.take(3)

            return@map articlesToTransform.mapNotNull(JSONArray::getJSONObject) {
                try {
                    val id = it.getLong("id")
                    val url = it.getString("url")
                    val title = it.getString("title")
                    val imageSrc = it.getString("image_src")

                    val response = endpoint.getListenArticleMetadata(id, url)
                    when (response) {
                        is PocketResponse.Success -> PocketListenArticle(id, url, title, imageSrc, response.data)
                        is PocketResponse.Failure -> {
                            Log.e(LOGTAG, "Pocket server returned failure")
                            null
                        }
                    }
                } catch (e: JSONException) {
                    Log.e(LOGTAG, "Invalid JSON in dummyArticles", e)
                    null
                }
            }
        }
        .observeOn(AndroidSchedulers.mainThread()) // Other repos exposed observables run on the main thread so we do too.

    val listenTiles: Observable<List<ChannelTile>> = listenArticles.map { articles -> articles.map { ChannelTile(
        id = it.id.toString(),
        url = it.listenMetadata.audioUrl,
        title = it.title,
        subtitle = null,
        setImage = ImageSetStrategy.ByPath(it.imageSrc),
        tileSource = TileSource.POCKET
    ) } }
}

/**
 * A data container for a Pocket article and its Listen metadata.
 */
private data class PocketListenArticle(
    val id: Long,
    val url: String,
    val title: String,
    val imageSrc: String,
    val listenMetadata: PocketListenArticleMetadata
)
