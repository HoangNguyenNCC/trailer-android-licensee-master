package devx.app.seller.webapi.response.MyProfile

import kotlinx.android.parcel.RawValue

data class MyProfileResponse(
    val message: String,
    val success: Int,
    val userObj: UserObj,
    val rentalsList: @RawValue List<MyTrailerData>
)