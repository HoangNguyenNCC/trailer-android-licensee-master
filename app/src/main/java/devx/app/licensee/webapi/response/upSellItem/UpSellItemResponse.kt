package devx.app.seller.webapi.response.upSellItem

import devx.app.licensee.modules.addBooking.TrailerItem
import devx.app.licensee.modules.addBooking.UpsellItem

data class UpSellItemResponse(
    val message: String,
    val success: Boolean,
    val upsellItemsList: ArrayList<UpsellItem>,
    val trailersList:ArrayList<TrailerItem>
)