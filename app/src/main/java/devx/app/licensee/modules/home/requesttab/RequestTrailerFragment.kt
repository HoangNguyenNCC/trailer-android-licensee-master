package devx.app.seller.modules.home.hometab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.home.requesttab.RequestInvoiceDetailsActivity
import devx.app.licensee.modules.home.requesttab.TrailerRequestAdapter
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.licensee.webapi.response.requelist.RentalRequest
import devx.app.licensee.webapi.response.requelist.RentelItems
import devx.app.licensee.webapi.response.requelist.TrailerRequestListReponse
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_request_trailer.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestTrailerFragment : BaseFragment() {

    private lateinit var trailerRequestAdapter: TrailerRequestAdapter
    var requestList:List<RentalRequest> = ArrayList<RentalRequest>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_trailer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trailerRequestAdapter= TrailerRequestAdapter(context!!,
            this@RequestTrailerFragment, requestList)
        trailerRequestRecyclerView.adapter = trailerRequestAdapter

        requestSwipeToRefresh.setOnRefreshListener {
            requestSwipeToRefresh.isRefreshing = true

            if(HelperMethods.isOwner()) {
                if(Utils.isNetworkAvailable(context)){
                    getRequestedList()
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            }
            else {
                if (HelperMethods.checkACL("REMINDERS", "VIEW")) {
                    if(Utils.isNetworkAvailable(context)){
                        getRequestedList()
                    }
                    else{
                        showToast("No Internet Connectivity!")
                    }
                } else {
                    Toast.makeText(activity,"You do not have access to view request.", Toast.LENGTH_SHORT).show()
                    if (requestSwipeToRefresh.isRefreshing)
                        requestSwipeToRefresh.isRefreshing = false
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if(HelperMethods.isOwner()) {
            if(Utils.isNetworkAvailable(context)){
                getRequestedList()
            }
            else{
                showToast("No Internet Connectivity!")
            }
        }
        else {
            if (HelperMethods.checkACL("REMINDERS", "VIEW")) {
                if(Utils.isNetworkAvailable(context)){
                    getRequestedList()
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            } else {
                Toast.makeText(activity,"You do not have access to view request.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var url:String=""
    var webService: Api = ServiceGenerator.getApi()
    private fun getRequestedList() {
        if (requestSwipeToRefresh.isRefreshing)
            requestSwipeToRefresh.isRefreshing = false
        showProgressDialog("")
        val requestMap = HashMap<String, String>()
        requestMap["count"] = "10"
        requestMap["skip"] = "0"

        val getAllRequestCall: Call<TrailerRequestListReponse> =webService.getAllRequest(ConstantApi.LIVE_BASE_URL + "rental/requests?",requestMap)

        getAllRequestCall.enqueue(object : Callback<TrailerRequestListReponse> {
            override fun onResponse(
                call: Call<TrailerRequestListReponse>,
                response: Response<TrailerRequestListReponse>
            ) {
                if (response.isSuccessful()) {
                    dismissProgressDialog()
                    if (response.body()!!.isSuccess) {
                        if (response.body()!!.requestList!= null && response.body()!!.requestList.isNotEmpty()) {
                            if(response.body()!!.requestList.size>0) {
                                trailerRequestRecyclerView.show()

                                txtNoDataAdded.gone()
                                requestList=response.body()!!.requestList
                                trailerRequestAdapter?.notifyDataSetChanged(requestList)
                            }
                            else{
                                trailerRequestRecyclerView.gone()
                                txtNoDataAdded.show()
                            }
                        }
                        else{
                            trailerRequestRecyclerView.gone()
                            txtNoDataAdded.show()
                        }
                    } else {
                        showToast("Something went wrong ...")
                    }

                } else {
                    dismissProgressDialog()

                    ///handling bad request 400 response
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    HelperMethods.closeEverythingOpenSplash(activity)
                }
            }

            override fun onFailure(
                call: Call<TrailerRequestListReponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
    }

    fun showInvoiceDetails(rentalRequest: RentalRequest) {
        val intent=Intent(activity,RequestInvoiceDetailsActivity::class.java )
        intent.putExtra("id",rentalRequest.rentalId)
        startActivity(intent)
    }

}
