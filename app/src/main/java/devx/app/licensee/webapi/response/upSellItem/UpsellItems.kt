package devx.app.seller.webapi.response.upSellItem

import android.os.Parcelable
import devx.app.seller.webapi.response.trailerslist.Photos
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpsellItems(
    var _id: String="",
    var licensee: String="",
    var name: String="",
    var photos: List<Photos>? =null,
    var price: String="",
    var photo: Photos? =null,
    var rating: Int=0,
    var quantity: Int=0,
    var type: String? = null,
    val isSelected:Boolean = false
) : Parcelable
