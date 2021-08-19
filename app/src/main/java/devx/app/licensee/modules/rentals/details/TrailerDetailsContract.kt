package devx.app.seller.modules.rentals.details

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import devx.app.licensee.common.BaseView
import org.json.JSONArray
import org.json.JSONObject

class TrailerDetailsContract {

    interface View: BaseView {

        fun handlerResponse(t: JSONObject, t1: JSONArray)
    }

    interface Presenter {

        fun loadTrailerDetails(id:String)
    }
}