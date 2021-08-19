package devx.app.licensee.webapi.response.MyProfile.profile

data class LicenseeLocation(
    val _id: String,
    val location: Location,
    val pincode: String,
    val text: String
)