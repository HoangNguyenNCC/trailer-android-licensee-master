package devx.app.licensee.webapi.response.addUpSell

import android.os.Parcelable
import devx.app.seller.webapi.response.trailerslist.Photos
import devx.app.seller.webapi.response.trailerslist.TrailerAvailability
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpsellItems(
    val _id: String,
    val licensee: String,
    val name: String,
    val photos: List<Photos>?,
    val price: String,
    val trailerModel: String,
    val rating: Int,
    var flag:Boolean=false,
    val availability:Boolean,
    val trailerAvailability: TrailerAvailability
): Parcelable