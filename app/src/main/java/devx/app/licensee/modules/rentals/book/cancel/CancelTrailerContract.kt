package devx.app.seller.modules.rentals.book.cancel

import devx.app.licensee.common.BaseView
import devx.app.seller.webapi.response.CommonResponse
class CancelTrailerContract {

    interface View : BaseView {

        fun handlerResponse(t: CommonResponse)
    }

    interface Presenter {

        fun cancelTrailer(id: String, licenseeId: String)
    }
}