package devx.app.licensee.webapi.response.home

import devx.app.seller.webapi.response.trailerslist.Trailers

data class HomeResponse(
    val message: String,
    val success: Boolean,
    val trailersList: List<Trailers>
)
