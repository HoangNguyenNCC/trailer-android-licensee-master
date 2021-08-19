package devx.app.licensee.webapi.response.MyProfile.profile

data class Address(
    var _id: String? = null,
    var location: Location? = null,
    var pincode: String? = null,
    var text: String? = null,
    var city:String? = null,
    var state:String? = null,
    var country:String? = null
)