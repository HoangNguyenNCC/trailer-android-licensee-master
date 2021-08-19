package devx.app.seller.webapi.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Name(
    val firstName: String,
    val lastName: String
) : Parcelable