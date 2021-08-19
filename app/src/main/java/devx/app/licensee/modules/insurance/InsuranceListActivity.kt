package devx.app.licensee.modules.insurance

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
import devx.app.licensee.webapi.response.InsuranceResponse
import devx.app.licensee.webapi.response.requelist.RentelItems
import kotlinx.android.synthetic.main.activity_insurance_list.*
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import org.json.JSONArray
import org.json.JSONObject

class InsuranceListActivity : BaseActivity() {

    var requestList = arrayListOf<InsuranceResponse>()
    var itemId=""
    var flag=""
    var photo=""
    var trailerName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_list)
       // CommonTopbarView().setup(this)

        itemId= intent.getStringExtra("itemId").toString()
        flag= intent.getStringExtra("flag").toString()
        photo= intent.getStringExtra("photo").toString()
        trailerName= intent.getStringExtra("trailerName").toString()

        commonBack.setOnClickListener{
            finish()
        }
        if(flag=="insurance"){
            textView2.text="All Insurance"
        }
        else{
            textView2.text="All Servicing"
        }
        if(Utils.isNetworkAvailable(context)){

            getRequestedDetails()
        }
        else{
            showToast("No Internet Connectivity!")
        }

    }
    var url:String=""
    private fun getRequestedDetails() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)

        if(flag=="insurance"){

            url= ConstantApi.LIVE_BASE_URL+"insurance"
        }
        else{

            url= ConstantApi.LIVE_BASE_URL+"servicing"
        }

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization", HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url+"?itemId=$itemId"+"&count=15&skip=0")
            .type(
                JSONObject::class.java
            ).weakHandler(
                this,
                "getRequestedData"
            )
        mAQuery.progress(mProgress).ajax(cb)


    }
    fun getRequestedData(
        url: String?, `object`: JSONObject?,
        status: AjaxStatus
    ) {
        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals( ConstantApi.LIVE_BASE_URL+"insurance?itemId=$itemId"+"&count=15&skip=0", ignoreCase = true)) {
                        if (`object` != null) {
                            if (`object`.getBoolean("success")) {

                                //showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                                val mainObjArr:JSONArray=`object`.getJSONArray("insuranceList")
                                if(mainObjArr.length()>0) {

                                    recyclerViewInsuranceServicing.visible()
                                    txtNoDataAdded.gone()

                                    requestList.clear()
                                    for (i in 0 until mainObjArr.length()) {
                                        var jsonObject: JSONObject = mainObjArr.getJSONObject(i)
                                        var insuranceResponse: InsuranceResponse =
                                            InsuranceResponse()
                                        insuranceResponse.itemId = jsonObject.getString("itemId")
                                        insuranceResponse.itemType =
                                            jsonObject.getString("itemType")
                                        insuranceResponse._id = jsonObject.getString("_id")
                                        if (!jsonObject.isNull("document")){
                                            insuranceResponse.document =
                                                jsonObject.getJSONObject("document").getString("data")
                                        }
                                        insuranceResponse.photo=photo
                                        insuranceResponse.trailerName=trailerName

                                        insuranceResponse.issueDate=jsonObject.getString("issueDate")
                                        insuranceResponse.expiryDate=jsonObject.getString("expiryDate")
                                        insuranceResponse.insuranceRef=jsonObject.getString("insuranceRef")
                                        requestList.add(insuranceResponse)
                                    }

                                    recyclerViewInsuranceServicing.adapter=InsuranceServicingAdapter(requestList,context,"insurance")
                                }
                                else{

                                    recyclerViewInsuranceServicing.gone()
                                    txtNoDataAdded.visible()
                                }



                            } else {
                                showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                                recyclerViewInsuranceServicing.gone()
                                txtNoDataAdded.visible()
                            }
                        }
                    }
                    else if (url.equals( ConstantApi.LIVE_BASE_URL+"servicing?itemId=$itemId"+"&count=15&skip=0", ignoreCase = true)) {
                        if (`object` != null) {
                            if (`object`.getBoolean("success")) {

                                //showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                                val mainObjArr:JSONArray=`object`.getJSONArray("servicingList")
                                if(mainObjArr.length()>0){

                                    recyclerViewInsuranceServicing.visible()
                                    txtNoDataAdded.gone()
                                requestList.clear()
                                for(i in 0 until mainObjArr.length()){
                                    var jsonObject: JSONObject =mainObjArr.getJSONObject(i)
                                    var insuranceResponse : InsuranceResponse = InsuranceResponse()
                                    insuranceResponse.itemId=jsonObject.getString("itemId")
                                    insuranceResponse.itemType=jsonObject.getString("itemType")
                                    insuranceResponse._id=jsonObject.getString("_id")
                                    if (!jsonObject.isNull("document")){
                                        insuranceResponse.document =
                                            jsonObject.getJSONObject("document").getString("data")
                                    }
                                    insuranceResponse.photo=photo
                                    insuranceResponse.trailerName=trailerName
                                    insuranceResponse.name=jsonObject.getString("name")
                                    insuranceResponse.serviceDate=jsonObject.getString("serviceDate")
                                    insuranceResponse.nextDueDate=jsonObject.getString("nextDueDate")
                                    insuranceResponse.servicingRef=jsonObject.getString("servicingRef")
                                    requestList.add(insuranceResponse)
                                }

                                    recyclerViewInsuranceServicing.adapter=InsuranceServicingAdapter(requestList,context,"servicing")

                                }
                                else{

                                    recyclerViewInsuranceServicing.gone()
                                    txtNoDataAdded.visible()
                                }


                            } else {
                                recyclerViewInsuranceServicing.gone()
                                txtNoDataAdded.visible()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast(HelperMethods.safeText(resources.getString(R.string.err_try_again), ""))
            }
        }
    }
}
