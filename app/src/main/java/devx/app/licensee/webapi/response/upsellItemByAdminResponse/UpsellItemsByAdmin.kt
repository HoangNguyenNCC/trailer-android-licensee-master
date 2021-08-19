package devx.app.seller.webapi.response.upSellItem

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpsellItemsByAdmin(
    val _id: String,
    val name: String,
    val photo: List<String>?,
    val description: String,
    val avilability:Boolean,
    val type: String
) : Parcelable