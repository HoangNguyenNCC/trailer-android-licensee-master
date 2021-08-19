package devx.app.seller.webapi.response.trailerslist

data class TrailerListResp(
    val message: String,
    val success: Int,
    val trailersList: List<Trailers>
)

