package devx.app.seller.webapi.response.MyProfile

import devx.app.seller.webapi.response.Location

data class Address(
    val _id: String,
    val location: Location,
    val pincode: String,
    val text: String
)