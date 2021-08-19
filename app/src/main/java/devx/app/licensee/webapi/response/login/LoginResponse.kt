package devx.app.licensee.webapi.response.login

import devx.app.licensee.webapi.response.MyProfile.profile.LicenseeObj
import devx.app.seller.webapi.response.EmployeeObj

data class LoginResponse(
    val employeeObj: EmployeeObj,
    val licenseeObj:LicenseeObj,
    val token: String,
    val message: String,
    val success: Boolean
)