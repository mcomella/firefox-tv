/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_tile.view.*
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.mozilla.focus.R
import org.mozilla.focus.autocomplete.UrlAutoCompleteFilter
import org.mozilla.focus.ext.forceExhaustive
import org.mozilla.focus.ext.toJavaURI
import org.mozilla.focus.ext.toUri
import org.mozilla.focus.exto.use
import org.mozilla.focus.home.icons.HomeTileIconManager
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.telemetry.UrlTextInputLocation
import org.mozilla.focus.utils.FormattedDomain
import org.mozilla.focus.utils.OnUrlEnteredListener

private const val COL_COUNT = 5
private const val SETTINGS_ICON_IDLE_ALPHA = 0.4f
private const val SETTINGS_ICON_ACTIVE_ALPHA = 1.0f

/**
 * Duration of animation to show custom tile. If the duration is too short, the tile will just
 * pop-in. I speculate this happens because the amount of time it takes to downsample the bitmap
 * is longer than the animation duration.
 */
private const val CUSTOM_TILE_TO_SHOW_MILLIS = 200L
private val CUSTOM_TILE_ICON_INTERPOLATOR = DecelerateInterpolator()

/** The home fragment which displays the navigation tiles of the app. */
class HomeFragment : Fragment() {

    lateinit var urlBar: LinearLayout
    var onUrlEnteredListener = object : OnUrlEnteredListener {} // default impl does nothing.
    var onSettingsPressed: (() -> Unit)? = null
    val urlAutoCompleteFilter = UrlAutoCompleteFilter()

    /**
     * Used to cancel background->UI threads: we attach them as children to this job
     * and cancel this job at the end of the UI lifecycle, cancelling the children.
     */
    private lateinit var uiLifecycleCancelJob: Job

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        urlBar = rootView.findViewById(R.id.homeUrlBar)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // todo: saved instance state?
        uiLifecycleCancelJob = Job()

        initTiles()
        initUrlInputView()

        settingsButton.alpha = SETTINGS_ICON_IDLE_ALPHA
        settingsButton.setImageResource(R.drawable.ic_settings)
        settingsButton.setOnFocusChangeListener { v, hasFocus ->
            v.alpha = if (hasFocus) SETTINGS_ICON_ACTIVE_ALPHA else SETTINGS_ICON_IDLE_ALPHA
        }

        settingsButton.setOnClickListener { v ->
            onSettingsPressed?.invoke()
        }

        registerForContextMenu(view)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity.menuInflater.inflate(R.menu.menu_context_hometile, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove -> {
                val homeTileAdapter = tileContainer.adapter as HomeTileAdapter
                val tileToRemove = homeTileAdapter.getItemAtPosition(getFocusedTilePosition()) ?: return false
                // This assumes that since we're deleting from a Home Tile object that we created
                // that the Uri is valid, so we do not do error handling here.
                when (tileToRemove) {
                    is BundledHomeTile -> {
                        val tileUri = tileToRemove.url.toUri()
                        if (tileUri != null) {
                            BundledTilesManager.getInstance(context).unpinSite(context, tileUri)
                            homeTileAdapter.removeItemAtPosition(getFocusedTilePosition())
                        }
                    }
                    is CustomHomeTile -> {
                        CustomTilesManager.getInstance(context).unpinSite(context, tileToRemove.url)
                        homeTileAdapter.removeItemAtPosition(getFocusedTilePosition())
                    }
                }
                TelemetryWrapper.homeTileRemovedEvent(tileToRemove)
                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiLifecycleCancelJob.cancel(CancellationException("Parent lifecycle has ended"))
    }

    override fun onResume() {
        super.onResume()
        urlAutoCompleteFilter.load(context)
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        urlBar.requestFocus()
    }

    private fun initTiles() = with (tileContainer) {
        val homeTiles = mutableListOf<HomeTile>().apply {
            addAll(BundledTilesManager.getInstance(context).getBundledHomeTilesList())
            addAll(CustomTilesManager.getInstance(context).getCustomHomeTilesList())
        }

        adapter = HomeTileAdapter(uiLifecycleCancelJob, homeTiles, onUrlEnteredListener)
        layoutManager = GridLayoutManager(context, COL_COUNT)
        setHasFixedSize(true)
    }

    private fun initUrlInputView() = with (urlInputView) {
        setOnCommitListener {
            onUrlEnteredListener.onTextInputUrlEntered(text.toString(), urlInputView.lastAutocompleteResult, UrlTextInputLocation.HOME)
        }
        setOnFilterListener { searchText, view -> urlAutoCompleteFilter.onFilter(searchText, view) }
    }

    companion object {
        const val FRAGMENT_TAG = "home"

        @JvmStatic
        fun create() = HomeFragment()
    }

    fun getFocusedTilePosition(): Int {
        return (activity.currentFocus.parent as? RecyclerView)?.getChildAdapterPosition(activity.currentFocus) ?: RecyclerView.NO_POSITION
    }

    fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU &&
                event.action == KeyEvent.ACTION_UP &&
                getFocusedTilePosition() != RecyclerView.NO_POSITION) {
            activity.openContextMenu(view)
            return true
        }
        return false
    }
}

private class HomeTileAdapter(
        private val uiLifecycleCancelJob: Job,
        private val tiles: MutableList<HomeTile>,
        private val onUrlEnteredListener: OnUrlEnteredListener
) : RecyclerView.Adapter<TileViewHolder>() {

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) = with (holder) {
        val item = tiles[position]
        when (item) {
            is BundledHomeTile -> {
                onBindBundledHomeTile(holder, item)
                setLayoutMarginParams(iconView, R.dimen.bundled_home_tile_margin_value)
            }
            is CustomHomeTile -> {
                onBindCustomHomeTile(uiLifecycleCancelJob, holder, item)
                setLayoutMarginParams(iconView, R.dimen.custom_home_tile_margin_value)
            }
        }.forceExhaustive

        itemView.setOnClickListener {
            onUrlEnteredListener.onNonTextInputUrlEntered(item.url)
            TelemetryWrapper.homeTileClickEvent(item)
        }

        val tvWhiteColor = ContextCompat.getColor(holder.itemView.context, R.color.tv_white)
        itemView.setOnFocusChangeListener { v, hasFocus ->
            val backgroundResource: Int
            val textColor: Int
            if (hasFocus) {
                backgroundResource = R.drawable.home_tile_title_focused_background
                textColor = tvWhiteColor
                menuButton.visibility = View.VISIBLE
            } else {
                backgroundResource = 0
                textColor = Color.BLACK
                menuButton.visibility = View.GONE
            }
            titleView.setBackgroundResource(backgroundResource)
            titleView.setTextColor(textColor)
        }
    }

    private fun setLayoutMarginParams(iconView: View, tileMarginValue: Int) {
        val layoutMarginParams = iconView.layoutParams as ViewGroup.MarginLayoutParams
        val marginValue = iconView.resources.getDimensionPixelSize(tileMarginValue)
        layoutMarginParams.setMargins(marginValue, marginValue, marginValue, marginValue)
        iconView.layoutParams = layoutMarginParams
    }

    fun getItemAtPosition(position: Int): HomeTile? {
        if (position > -1 && position < itemCount) {
            return tiles[position]
        }
        return null
    }

    fun removeItemAtPosition(position: Int) {
        if (position > -1 && position < itemCount) {
            tiles.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = tiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_tile, parent, false)
    )
}

private fun onBindBundledHomeTile(holder: TileViewHolder, tile: BundledHomeTile) = with (holder) {
    val bitmap = BundledTilesManager.getInstance(itemView.context).loadImageFromPath(itemView.context, tile.imagePath)
    iconView.setImageBitmap(bitmap)

    titleView.text = tile.title
}

private fun onBindCustomHomeTile(uiLifecycleCancelJob: Job, holder: TileViewHolder, item: CustomHomeTile) = with (holder) {
    fun updateUI(title: String, icon: Bitmap) {
        titleView.text = title
        iconView.setImageBitmap(icon)
    }

    // Getting the title may block so we start the maybe async icon job first.
    val iconDeferred = HomeTileIconManager.readMaybeAsync(itemView.context, item.id, item.url)
    val tileTitle = getBlockingTileTitle(itemView.context, item)

    // We'd prefer to not have animations, so we update the UI synchronously when possible.
    if (iconDeferred.isCompleted) {
        updateUI(tileTitle, iconDeferred.getCompleted())
        return@with
    }

    launch(uiLifecycleCancelJob + UI, CoroutineStart.UNDISPATCHED) {
        val icon = iconDeferred.await()

        // NB: Don't suspend after this point (i.e. between view updates like setImage)
        // so we don't see intermediate view states.
        updateUI(tileTitle, icon)

        // Animate to avoid pop-in due to thread hand-offs.
        AnimatorSet().apply {
            interpolator = CUSTOM_TILE_ICON_INTERPOLATOR
            duration = CUSTOM_TILE_TO_SHOW_MILLIS

            val iconAnim = ObjectAnimator.ofInt(iconView, "imageAlpha", 0, 255)
            val titleAnim = ObjectAnimator.ofFloat(titleView, "alpha", 0f, 1f)

            playTogether(iconAnim, titleAnim)
        }.start()
    }
}

private fun getBlockingTileTitle(context: Context, item: CustomHomeTile): String {
    val validUri = item.url.toJavaURI()
    return if (validUri == null) {
        item.url
    } else {
        // NB: The initial FormattedDomain call will block the UI thread if its data is not done
        // reading. However:
        // - We preload this data on app start
        // - The OS delays the app launch, giving us more time to block the UI thread without consequences.
        //
        // Ultimately, we don't want animations which means loading the title synchronously. I
        // tried a more correct solution - returning deferred only if the call isn't completed
        // yet - but it's unreasonably complex so we just block the UI thread instead.
        StrictMode.allowThreadDiskReads().use {
            val subdomainDotDomain = FormattedDomain.format(context, validUri, false, 1)
            FormattedDomain.stripCommonPrefixes(subdomainDotDomain)
        }
    }
}

private class TileViewHolder(
        itemView: View
) : RecyclerView.ViewHolder(itemView) {
    val iconView = itemView.tile_icon
    val titleView = itemView.tile_title
    val menuButton = itemView.home_tile_menu_icon
}
