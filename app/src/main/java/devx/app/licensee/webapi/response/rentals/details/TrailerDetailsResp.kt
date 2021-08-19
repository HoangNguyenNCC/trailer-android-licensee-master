package devx.app.seller.webapi.response.rentals.details

import android.os.Parcelable
import devx.app.seller.webapi.response.trailerslist.Trailers
import devx.app.seller.webapi.response.upSellItem.UpsellItems
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrailerDetailsResp(
    val message: String,
    val success: Boolean,
    val trailerObj: Trailers,
    val upsellItemsList: List<UpsellItems>
) : Parcelable
