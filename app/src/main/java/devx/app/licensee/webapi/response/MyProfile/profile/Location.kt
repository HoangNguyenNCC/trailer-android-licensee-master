package devx.app.licensee.webapi.response.MyProfile.profile

data class Location(
    var _id: String? = null,
    var coordinates: List<Double>? = null,
    var type: String? = null
)