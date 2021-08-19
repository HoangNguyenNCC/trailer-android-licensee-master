package devx.app.seller.webapi.response.trailerslist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RentalCharges(
    val door2Door: List<Door2Door>,
    val pickUp: List<PickUp>

) : Parcelable

@Parcelize
class Door2Door (
    val charges: Int,
    val duration: Int
    ): Parcelable

