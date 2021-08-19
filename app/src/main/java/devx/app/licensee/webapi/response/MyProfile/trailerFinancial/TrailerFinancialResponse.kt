package devx.app.licensee.webapi.response.MyProfile.trailerFinancial

data class TrailerFinancialResponse(
    val message: String,
    val success: Boolean,
    val finances: List<FinancialsObj>
)