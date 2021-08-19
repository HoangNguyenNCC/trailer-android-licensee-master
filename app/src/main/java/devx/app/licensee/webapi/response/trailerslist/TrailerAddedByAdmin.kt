package devx.app.licensee.webapi.response.trailerslist

import android.os.Parcelable
import devx.app.seller.webapi.response.trailerslist.Trailers
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrailerAddedByAdmin(
    val message: String,
    val success: Boolean,
    val trailersList: List<Trailers>
) : Parcelable