package devx.app.seller.webapi.response.MyProfile

data class DriverLicense(
    val _id: String,
    val expiryDate: String,
    val licenseNumber: String,
    val picture: String,
    val state: String
)