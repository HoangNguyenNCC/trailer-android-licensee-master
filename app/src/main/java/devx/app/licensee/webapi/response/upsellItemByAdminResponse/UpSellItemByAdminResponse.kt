package devx.app.seller.webapi.response.upSellItem

data class UpSellItemByAdminResponse(
    val message: String,
    val success: Boolean,
    val upsellItemsList: List<UpsellItemsByAdmin>
)