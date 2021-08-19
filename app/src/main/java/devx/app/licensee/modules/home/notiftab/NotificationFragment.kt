package devx.app.seller.modules.home.notiftab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.PlaceHolderAdapter
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.home.notiftab.NotificationTrailerListAdapter
import devx.app.licensee.modules.home.requesttab.RequestInvoiceDetailsActivity
import devx.app.licensee.modules.home.requesttab.TrailerRequestAdapter
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.licensee.webapi.response.requelist.RentalRequest
import devx.app.seller.webapi.response.notification.NotifListResponse
import devx.app.seller.webapi.response.notification.Notifications
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.txtNoDataAdded
import kotlinx.android.synthetic.main.fragment_request_trailer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : BaseFragment() {

    private lateinit var notificationTrailerListAdapter: NotificationTrailerListAdapter
    var notificationList = listOf<Notifications>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationTrailerListAdapter= NotificationTrailerListAdapter(
            this@NotificationFragment, notificationList)
        notificationRecyclerView.adapter = notificationTrailerListAdapter

        if(HelperMethods.isOwner()) {
            if(Utils.isNetworkAvailable(context)){
                getMyAccountDetails()
            }
            else{
                showToast("No Internet Connectivity!")
            }
        }
        else {
            if (HelperMethods.checkACL("REMINDERS", "VIEW")) {
                if(Utils.isNetworkAvailable(context)){
                    getMyAccountDetails()
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            } else {
                Toast.makeText(activity,"You do not have access to view reminders.", Toast.LENGTH_SHORT).show()
            }
        }
        notificationSwipeToRefresh.setOnRefreshListener {
            notificationSwipeToRefresh.isRefreshing = true
            if(HelperMethods.isOwner()) {
                if(Utils.isNetworkAvailable(context)){
                    getMyAccountDetails()
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            }
            else {
                if (HelperMethods.checkACL("REMINDERS", "ADD")) {
                    if(Utils.isNetworkAvailable(context)){
                    getMyAccountDetails()
                }
                else{
                    showToast("No Internet Connectivity!")
                }
                } else {
                    Toast.makeText(activity,"You do not have access to view reminders.", Toast.LENGTH_SHORT).show()
                    if (notificationSwipeToRefresh.isRefreshing)
                        notificationSwipeToRefresh.isRefreshing = false
                }
            }

        }

    }

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun getMyAccountDetails() {
        showProgressDialog()
        if (notificationSwipeToRefresh.isRefreshing)
            notificationSwipeToRefresh.isRefreshing = false
        val getMyNotificationsCall: Call<NotifListResponse> =webService.getMyNotifications(ConstantApi.LIVE_BASE_URL + "reminders",HelperMethods.getUserId())

        getMyNotificationsCall.enqueue(object : Callback<NotifListResponse> {
            override fun onResponse(
                call: Call<NotifListResponse>,
                response: Response<NotifListResponse>
            ) {

                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.remindersList != null && response.body()!!.remindersList.isNotEmpty()) {
                            if(response.body()!!.remindersList.isNotEmpty()) {
                                notificationRecyclerView.show()
                                txtNoDataAdded.gone()
                                notificationList=response.body()!!.remindersList
                                notificationTrailerListAdapter?.notifyDataSetChanged(notificationList)
                            }
                            else{
                                notificationRecyclerView.gone()
                                txtNoDataAdded.show()
                            }
                        }  else{
                            notificationRecyclerView.gone()
                            txtNoDataAdded.show()
                        }
                    } else {
                        showToast("Something went wrong ...")
                        //return
                    }
                }
                else if(response.code()==500){
                    showToast("Something went wrong ...")
                        notificationRecyclerView.gone()
                        txtNoDataAdded.show()

                }else {
                    ///handling bad request 400 response
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    HelperMethods.closeEverythingOpenSplash(activity)
                }
            }

            override fun onFailure(
                call: Call<NotifListResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }

    fun showInvoiceDetails(rentalRequest: Notifications) {
        val intent= Intent(activity, RequestInvoiceDetailsActivity::class.java )
        intent.putExtra("id",rentalRequest.invoiceId)
        startActivity(intent)
    }
}
