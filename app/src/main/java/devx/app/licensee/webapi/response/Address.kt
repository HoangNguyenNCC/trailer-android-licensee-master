package devx.app.seller.webapi.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val _id: String,
    val location: Location,
    val pincode: String,
    val text: String
) : Parcelable