package devx.app.licensee.webapi.response.addUpSell

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddUpSellItemResponse(
    val message: String,
    val success: Boolean,
    val upsellItemsList: List<UpsellItems>,
    val trailersList: List<UpsellItems>
): Parcelable