package com.yellowpineapple.wakup.sdk.controllers

import android.content.Context
import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.yellowpineapple.wakup.sdk.R

import com.yellowpineapple.wakup.sdk.models.Offer
import com.yellowpineapple.wakup.sdk.views.OfferListView
import com.yellowpineapple.wakup.sdk.views.OfferSmallView
import com.yellowpineapple.wakup.sdk.views.RelatedOffersHeader

/***
 * ADAPTER
 */

class MultipleOffersAdapter(private val headerView: View?, val context: Context) :
        androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(), OfferSmallView.Listener {

    var offerCategories: LinkedHashMap<OfferCategory, List<Offer>> = LinkedHashMap();
    private var currentLocation: Location? = null
    var listener: Listener? = null

    private val isHeaderPresent: Boolean
        get() = headerView != null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        when (viewType) {
            TYPE_HEADER -> {
                setFullSpanView(headerView)
                return HeaderViewHolder(headerView);
            }
            TYPE_TITLE ->  {
                val view = RelatedOffersHeader(context)
                setFullSpanView(view)
                return HeaderViewHolder(view);
            }
            else -> {
                val offerView = OfferListView(context)
                offerView.setListener(this)
                return OfferViewHolder(offerView)
            }
        }
    }

    fun setFullSpanView(view: View?) {
        view?.viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val lp = view.layoutParams
                if (lp is androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) {
                    lp.isFullSpan = true
                    view.layoutParams = lp
                }
                view.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

    }

    override fun getItemViewType(position: Int): Int {
        var currentIndex = position
        if (isHeaderPresent) {
            if (position == 0) return TYPE_HEADER else --currentIndex
        }
        for ((category, offers) in offerCategories) {
            if (category.title != null) {
                if (currentIndex == 0) return TYPE_TITLE else --currentIndex
            }
            if (currentIndex < offers.size) {
                return TYPE_ITEM
            } else {
                currentIndex -= offers.size
            }
        }
        return TYPE_TITLE
    }

    /**
     * Obtains the position index ignoring any previous header or title
     */
    fun positionWithoutHeaders(originalPosition: Int) : Int {
        var position = originalPosition
        if (isHeaderPresent) {
            if (position == 0) return position else position--
        }
        for ((category, offers) in offerCategories) {
            if (category.title != null) {
                if (position == 0) return position else position--
            }
            if (position < offers.size) {
                return TYPE_ITEM
            } else {
                position -= offers.size
            }
        }

        return position
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            TYPE_HEADER -> {
                // Set full span
                val layoutParams = holder.itemView.layoutParams as? androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams
                layoutParams?.isFullSpan = true
            }
            TYPE_TITLE -> {
                val relatedOffersHeader = holder.itemView as RelatedOffersHeader
                relatedOffersHeader.setTitle(getTitle(position))
                // Set full span
                val layoutParams = holder.itemView.layoutParams as? androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams
                layoutParams?.isFullSpan = true
            }
            else -> {
                val offerViewHolder = holder as OfferViewHolder
                offerViewHolder.offerView.setOffer(getOffer(position), currentLocation)
            }
        }
    }

    private fun getTitle(position: Int): String? {
        var currentIndex = position
        if (isHeaderPresent) --currentIndex
        for ((category, offers) in offerCategories) {
            if (category.title != null) {
                if (currentIndex == 0) return category.title else currentIndex--
            }
            currentIndex -= offers.size
        }
        return context.getString(R.string.wk_related_offers)
    }

    private fun getOffer(position: Int): Offer? {
        var currentIndex = position
        if (isHeaderPresent) --currentIndex
        for ((category, offers) in offerCategories) {
            currentIndex -= if (category.title != null) 1 else 0
            if (currentIndex < offers.size) {
                return offers.get(currentIndex)
            } else {
                currentIndex -= offers.size
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        var count = 0
        for ((category, offers) in offerCategories) {
            count += if (category.title != null && offers.isNotEmpty()) 1 else 0
            count += offers.size
        }

        if (isHeaderPresent) count++

        return count
    }

    private class OfferViewHolder internal constructor(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        internal var offerView: OfferListView = v as OfferListView

    }

    private class HeaderViewHolder internal constructor(v: View?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v!!)

    override fun onClick(offer: Offer) {
        listener?.onOfferClick(offer)
    }

    override fun onLongClick(offer: Offer) {
        listener?.onOfferLongClick(offer)
    }

    interface Listener {
        fun onOfferClick(offer: Offer)
        fun onOfferLongClick(offer: Offer)
    }

    fun setCurrentLocation(currentLocation: Location) {
        this.currentLocation = currentLocation
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_TITLE = 1
        const val TYPE_ITEM = 2
    }

}