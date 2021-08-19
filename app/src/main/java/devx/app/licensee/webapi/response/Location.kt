package devx.app.seller.webapi.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val _id: String,
    val coordinates: List<Double>,
    val text: String,
    val type: String
) : Parcelable