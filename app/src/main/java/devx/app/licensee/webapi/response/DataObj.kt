package devx.app.seller.webapi.response

import devx.app.licensee.webapi.response.MyProfile.profile.Address


data class EmployeeObj(
    val _id: String,
    val acceptedInvite: Boolean,
    var acl: MutableMap<String,ArrayList<String>>,
    val createdAt: String,
    val email: String,
    val isEmailVerified: Boolean,
    val isMobileVerified: Boolean,
    val isOwner: Boolean,
    val licenseeId: String,
    val mobile: String,
    val name: String,
    val updatedAt: String,
    val address: Address
)
