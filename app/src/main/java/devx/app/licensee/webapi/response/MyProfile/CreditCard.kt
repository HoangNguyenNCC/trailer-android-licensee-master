package devx.app.seller.webapi.response.MyProfile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreditCard(
    val _id: String,
    val code: Int,
    val expiryDate: String,
    val name: String,
    val number: String
) : Parcelable