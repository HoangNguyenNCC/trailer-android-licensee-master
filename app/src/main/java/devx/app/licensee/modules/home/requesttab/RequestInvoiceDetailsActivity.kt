package devx.app.licensee.modules.home.requesttab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.requelist.RentalRequest
import devx.app.licensee.webapi.response.requelist.RentelItems
import devx.app.licensee.webapi.response.requelist.TrailerRequestListReponse
import kotlinx.android.synthetic.main.activity_request_invoice_details.*
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import kotlinx.android.synthetic.main.fragment_request_trailer.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestInvoiceDetailsActivity : BaseActivity() {

	lateinit var requestList :List<RentelItems>
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_request_invoice_details)


		expListRentalItems.setFocusable(false)
		expListRentalItems.setExpanded(true)
		if(Utils.isNetworkAvailable(context)){
			getRequestedDetails()
		}
		else{
			showToast("No Internet Connectivity!")
		}
		commonBack.setOnClickListener{
			finish()
		}
		btnInvoiceDetailNext.setOnClickListener {
			val intent1= Intent(context,RequestApproveActivity::class.java )
			intent1.putExtra("id",intent.getStringExtra("id"))
			startActivity(intent1)
		}
	}
	var url:String=""
	var webService: Api = ServiceGenerator.getApi()
	private fun getRequestedDetails() {

		showProgressDialog("Please wait..")
		val requestMap = HashMap<String, String>()
		requestMap["id"] = intent.getStringExtra("id").toString()

		val getAllRequestCall: Call<TrailerRequestListReponse> =webService.getAllRequest(ConstantApi.LIVE_BASE_URL + "rental/details?",requestMap)

		getAllRequestCall.enqueue(object : Callback<TrailerRequestListReponse> {
			override fun onResponse(
				call: Call<TrailerRequestListReponse>,
				response: Response<TrailerRequestListReponse>
			) {
				dismissProgressDialog()
				if (response.isSuccessful()) {
					if (response.body()!!.isSuccess) {
						if (response.body()!!.rentalObj!= null ) {
							txtCustomerName.text = response.body()!!.rentalObj.bookedByUser.name
							txtCustomerAddress.text =
								response.body()!!.rentalObj.bookedByUser.address.text
							HelperMethods.downloadImage(
								response.body()!!.rentalObj.bookedByUser.photo.data,
								context,
								imgBookedByUser
							)

							var originalStartDate = HelperMethods.newFormtaDateTime(
								"yyyy-MM-dd HH:mm",
								"yyyy-MM-dd HH:mm",
								response.body()!!.rentalObj.rentalPeriod.start
							)
							var originalEndDate = HelperMethods.newFormtaDateTime(
								"yyyy-MM-dd HH:mm",
								"yyyy-MM-dd HH:mm",
								response.body()!!.rentalObj.rentalPeriod.end
							)
							txtRentalDate.text =
								"From " + originalStartDate + " to " + originalEndDate
							txtInvoiceNumber.text =
								"Invoice Number: " + response.body()!!.rentalObj.invoiceNumber

							if (response.body()!!.rentalObj.rentedItems.size > 0) {
								txtTrailerName.show()
								txtTrailerName.text =
									response.body()!!.rentalObj.rentedItems[0].itemName
								HelperMethods.downloadImage(
									response.body()!!.rentalObj.rentedItems[0].itemPhoto.data,
									context,
									itemPic
								)
							} else {
								txtTrailerName.gone()
								itemPic.gone()
							}
							if (response.body()!!.rentalObj.rentedItems.size > 0) {
								txtPricing.show()
								expListRentalItems.show()
								requestList = response.body()!!.rentalObj.rentedItems
								expListRentalItems.adapter =
									RentedItemsAdapter(requestList, context, "")
							} else {
								txtPricing.gone()
								expListRentalItems.gone()
							}
						}
						else{
							showToast("Something went wrong ...")
						}
					} else {
						showToast("Something went wrong ...")
					}

					dismissProgressDialog()
				} else {
					///handling bad request 400 response
					HelperMethods.setIsUserLogin(false)
					HelperMethods.setUserId("-1")
					showToast("Session expired! Please login again.")
					HelperMethods.closeEverythingOpenSplash(this@RequestInvoiceDetailsActivity)
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


}
