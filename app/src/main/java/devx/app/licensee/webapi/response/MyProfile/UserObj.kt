package devx.app.seller.webapi.response.MyProfile

import android.os.Parcelable
import devx.app.seller.webapi.response.MyProfile.CreditCard
import kotlinx.android.parcel.Parcelize


data class UserObj(
    val __v: Int,
    val _id: String,
    val address: String,
    val photo: String,
    val createdAt: String,
    val dob: String,
    val driverLicense: DriverLicense,
    val creditCard: CreditCard,
    val email: String,
    val mobile: String,
    val name: String,
    val updatedAt: String,
    val isVerified: Boolean
)