package devx.app.licensee.webapi.response.MyProfile.profile

import java.io.Serializable


data class EmployeeObj(
    var _id: String? = null,
    var acceptedInvite: Boolean? = null,
    var createdAt: String? = null,
    var email: String? = null,
    var isEmailVerified: Boolean? = null,
    var isMobileVerified: Boolean? = null,
    var isOwner: Boolean? = null,
    var licenseeId: String? = null,
    var mobile: String? = null,
    var isDeleted:Boolean? = null,
    var name: String? = null,
    var updatedAt: String? = null,
    var title: String? = null,
    var type:String? = null,
    var acl: List<String>? = null,
    var additionalDocument: String? = null,
    var driverLicense:DriverLicense? = null,
    var photo:String? = null,
    var address: Address? = null,
    var dob:String? = null
):Serializable

data class DriverLicense(
    var verified: Boolean? = null,
    var state: String? = null,
    var card: String? = null,
    var expiry: String? = null,
    var scan: String? = null
)
