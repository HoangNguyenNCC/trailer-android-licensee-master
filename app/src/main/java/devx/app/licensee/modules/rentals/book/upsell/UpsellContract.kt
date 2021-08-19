package devx.app.seller.modules.rentals.book.upsell

import devx.app.licensee.common.BaseView
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse

class UpsellContract {

    interface View: BaseView {

        fun handlerResponse(t: UpSellItemResponse)
    }

    interface Presenter {

        fun load(id:String)
    }
}