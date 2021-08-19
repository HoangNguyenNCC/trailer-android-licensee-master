package devx.app.licensee.modules.addtrailer

import devx.app.licensee.common.BaseView
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp

class AddTrailerContract {

    interface View: BaseView {

        fun handlerResponse(t:TrailerDetailsResp)
    }

    interface Presenter {

        fun loadTrailerDetails(id:String)
    }
}