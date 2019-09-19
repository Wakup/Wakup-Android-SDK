package com.yellowpineapple.wakup.sdk.controllers

import android.content.Context
import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.yellowpineapple.wakup.sdk.models.Offer
import com.yellowpineapple.wakup.sdk.views.OfferListView
import com.yellowpineapple.wakup.sdk.views.OfferSmallView
import com.yellowpineapple.wakup.sdk.views.RelatedOffersHeader

/***
 * ADAPTER
 */

class MultipleOffersAdapter(private val headerView: View?, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), OfferSmallView.Listener {

    var offerCategories: LinkedHashMap<OfferCategory, List<Offer>> = LinkedHashMap();
    private var currentLocation: Location? = null
    var listener: Listener? = null

    private val isHeaderPresent: Boolean
        get() = headerView != null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            TYPE_HEADER -> return HeaderViewHolder(headerView)
            TYPE_TITLE -> return HeaderViewHolder(RelatedOffersHeader(context))
            else -> {
                val offerView = OfferListView(context)
                offerView.setListener(this)
                return OfferViewHolder(offerView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderPresent && position == 0) TYPE_HEADER else TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            TYPE_HEADER -> return
            TYPE_TITLE -> {
                val relatedOffersHeader = holder.itemView as RelatedOffersHeader
                relatedOffersHeader.setTitle(getTitle(position))
            }
            else -> {
                val offerViewHolder = holder as OfferViewHolder
                offerViewHolder.offerView.setOffer(getOffer(position), currentLocation)
            }
        }
    }

    private fun getTitle(position: Int): String? {
        return "Ofertas relacionadas"
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
            count += if (category.title != null) 1 else 0
            count += offers.size
        }

        if (isHeaderPresent) count++

        return count
    }

    private class OfferViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {

        internal var offerView: OfferListView = v as OfferListView

    }

    private class HeaderViewHolder internal constructor(v: View?) : RecyclerView.ViewHolder(v)

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
        private val TYPE_HEADER = 0
        private val TYPE_TITLE = 1
        private val TYPE_ITEM = 2
    }

}