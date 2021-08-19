package devx.app.seller.webapi.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverLicense(
    val expiryDate: String,
    val licenseNumber: String,
    val picture: String,
    val state: String
) : Parcelable