package devx.app.seller.webapi.response.trailerslist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PickUp(
    val charges: Int,
    val duration: Int
) : Parcelable