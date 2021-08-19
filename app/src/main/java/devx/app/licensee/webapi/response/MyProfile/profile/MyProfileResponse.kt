package devx.app.licensee.webapi.response.MyProfile.profile

data class MyProfileResponse(
    val employeeObj: EmployeeObj,
    val licenseeObj: LicenseeObj,
    val message: String,
    val success: Boolean,
    val errorsList:ArrayList<String>
)