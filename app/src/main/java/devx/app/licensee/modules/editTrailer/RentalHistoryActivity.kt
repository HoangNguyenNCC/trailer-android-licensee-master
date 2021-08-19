package devx.app.licensee.modules.editTrailer

import android.os.Bundle
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.response.RentalHistory
import devx.app.licensee.webapi.response.requelist.RentelItems
import kotlinx.android.synthetic.main.activity_add_booking.*
import kotlinx.android.synthetic.main.activity_insurance_list.*
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import org.json.JSONObject

class RentalHistoryActivity : BaseActivity() {

    var requestList = arrayListOf<RentalHistory>()
    var itemId=""
    var flag=""
    var tempJsonObject=TrailerDetailsActivity.trailerDetailsReponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_list)

        commonBack.setOnClickListener{
            finish()
        }
        textView2.text="Rental History"
        textView.text="YOUR CLIENTS"


        for(i in 0 until tempJsonObject.getJSONArray("rentalsList").length()){
            var jsonObject: JSONObject =tempJsonObject.getJSONArray("rentalsList").getJSONObject(i)
            var rentelItems : RentalHistory = RentalHistory()
            rentelItems.start=jsonObject.getString("start")
            rentelItems.end=jsonObject.getString("end")
            rentelItems.businessName=jsonObject.getJSONObject("bookedByUser").getString("name")
            rentelItems.photo=jsonObject.getJSONObject("bookedByUser").getJSONObject("photo").getString("data")
            rentelItems.invoiceId=jsonObject.getString("invoiceId")
            requestList.add(rentelItems)
        }

        recyclerViewInsuranceServicing.adapter=RentalHistoryAdapter(requestList,context,"insurance")

    }
}
