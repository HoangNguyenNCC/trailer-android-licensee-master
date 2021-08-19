package devx.app.licensee.webapi.response.accessControlList

data class AccessControlListResponse(
    val accessControlList: AccessControlList,
    val message: String,
    val success: Boolean
)