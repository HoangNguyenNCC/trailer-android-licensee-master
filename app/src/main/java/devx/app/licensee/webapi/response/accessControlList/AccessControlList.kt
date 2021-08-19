package devx.app.licensee.webapi.response.accessControlList

data class AccessControlList(
    val ADD: List<String>,
    val DELETE: List<String>,
    val UPDATE: List<String>,
    val VIEW: List<String>
)