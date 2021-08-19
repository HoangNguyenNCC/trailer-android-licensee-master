package devx.app.licensee.webapi.response.MyProfile.trailerFinancial

import devx.app.seller.webapi.response.trailerslist.Photos

data class TotalByType(
    val rentedItem: String,
    val rentedItemName: String,
    val rentedItemPhoto: Photos,
    val rentedItemId: String,
    val total: String
)