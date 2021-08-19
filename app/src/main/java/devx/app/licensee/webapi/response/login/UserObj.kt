package devx.app.licensee.webapi.response.login

import android.os.Parcelable
import devx.app.seller.webapi.response.Address
import devx.app.seller.webapi.response.DriverLicense
import devx.app.seller.webapi.response.MyProfile.CreditCard
import devx.app.seller.webapi.response.Name
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserObj(
    val __v: Int,
    val _id: String,
    val address: Address,
    val photo: String,
    val createdAt: String,
    val dob: String,
    val driverLicense: DriverLicense,
    val creditCard: CreditCard,
    val email: String,
    val mobile: String,
    val name: Name,
    val updatedAt: String,
    val isVerified: Boolean
) : Parcelable