package devx.app.licensee.modules.home.requesttab

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ChangeRentalStatusRequest
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.services.SignalSenderService
import devx.app.licensee.common.utils.*
import devx.app.licensee.modules.trailerTracking.TrackingLicenseBottomSheetFragment
import devx.app.licensee.modules.trailerTracking.TrailerTrackingActivity
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.requelist.RentelItems
import devx.app.licensee.webapi.response.requelist.TrailerRequestListReponse
import devx.app.seller.webapi.response.CommonResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_request_approve.*
import kotlinx.android.synthetic.main.activity_request_approve.textView
import kotlinx.android.synthetic.main.activity_request_invoice_details.txtCustomerAddress
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

private const val LOCATION_RESOLUTION_REQUEST_CODE = 100
private const val LOCATION_REQUEST_INTERVAL_TIME = 45000L


class RequestApproveActivity : BaseActivity(), OnMapReadyCallback {

    var requestList = arrayListOf<RentelItems>()


    private lateinit var mMap: GoogleMap
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    //private var rentalId = "5e58914c9d61b40017de39f8"
    private var isContinuousLocationUpdate = false
    var rentalId: String = ""
    var revisionId: String = ""
    var approvalStatus: String = ""


    var url: String = ""
    var webService: Api = ServiceGenerator.getApi()

    var destLat: Double = 0.0
    var destLong: Double = 0.0
    var srcLat: Double = 0.0
    var srcLong: Double = 0.0
    var revisedStartDate = ""
    var revisedEndDate = ""
    var originalStartDate = ""
    var originalEndDate = ""

    private var bottomSheetFragment: StartReturnBottomSheetFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_approve)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //askLocationPermission()

        commonBack.setOnClickListener {
            finish()
        }
        btnApprove.setOnClickListener {
            if (btnApprove.text == "Start Tracking") {
                val intent1 = Intent(context, TrailerTrackingActivity::class.java)
                intent1.putExtra("id", intent.getStringExtra("id"))
                startActivity(intent1)
            }
            else if(btnApprove.text == "End Booking"){

                val call = webService.setStatus(ChangeRentalStatusRequest(SignalSenderService.rentalId, "return-started"))
                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.isSuccessful) {
                            Log.d("here", "response success")
                            showToast("Trailer rental ended")
                            onBackPressed()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Log.d("here", "response failed" + t.message)
                    }
                })

            }
            else {
                askToApproveOrReject(rentalId, revisionId, "approved")

            }
        }
        btnDecline.setOnClickListener {
            askToApproveOrReject(rentalId, revisionId, "rejected")
        }
        btnShowoiginalDate.setOnClickListener {
            if (btnShowoiginalDate.text == "Show Original Dates") {
                txtRentalStartDate.text = originalStartDate
                txtRentalEndDate.text = originalEndDate
                btnShowoiginalDate.text = "Show Revised Dates"
            } else {
                txtRentalStartDate.text = revisedStartDate
                txtRentalEndDate.text = revisedEndDate
                btnShowoiginalDate.text = "Show Original Dates"
            }
        }

    }

    private fun askToApproveOrReject(
            rentalId: String,
            revisionId: String,
            flag: String
    ) {

        val builder = android.app.AlertDialog.Builder(context)
        //set title for alert dialog
        if (flag == "approved") {
            builder.setTitle("Approve Request")
        } else {
            builder.setTitle("Reject Request")
        }
        //set message for alert dialog
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            if (flag == "approved") {
                changeRequestStatusAPI(rentalId, revisionId, "approved")
            } else {
                changeRequestStatusAPI(rentalId, revisionId, "rejected")
            }
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: android.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        val positiveColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        val negativeColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        alertDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        alertDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }


    override fun onResume() {
        super.onResume()
        if (Utils.isNetworkAvailable(context)) {
            getRequestedDetails()
        } else {
            showToast("No Internet Connectivity!")
        }
    }

    private fun getRequestedDetails() {
        showProgressDialog("Please wait..")
        val requestMap = HashMap<String, String>()
        requestMap["id"] = intent.getStringExtra("id").toString()

        val getAllRequestCall: Call<TrailerRequestListReponse> =
            webService.getAllRequest(ConstantApi.LIVE_BASE_URL + "rental/details?", requestMap)

        getAllRequestCall.enqueue(object : Callback<TrailerRequestListReponse> {
            override fun onResponse(
                    call: Call<TrailerRequestListReponse>,
                    response: Response<TrailerRequestListReponse>
            ) {
                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.isSuccess) {
                        if (response.body()!!.rentalObj != null) {
                            rentalId = response.body()!!.rentalObj._id

                            originalStartDate = HelperMethods.changeDateFormat(
                                    HelperMethods.newFormtaDateTime(
                                            "yyyy-MM-dd HH:mm",
                                            "yyyy-MM-dd HH:mm",
                                            response.body()!!.rentalObj.revisions[0].start
                                    )
                            )
                            originalEndDate = HelperMethods.changeDateFormat(
                                    HelperMethods.newFormtaDateTime(
                                            "yyyy-MM-dd HH:mm",
                                            "yyyy-MM-dd HH:mm",
                                            response.body()!!.rentalObj.revisions[0].end
                                    )
                            )

                            var lenght = response.body()!!.rentalObj.revisions.size
                            if (lenght > 1) {
                                //var tempjson=response.body()!!.rentalObj.revisions[lenght-1].start.getJSONObject(lenght-1)
                                revisionId =
                                        response.body()!!.rentalObj.revisions[lenght - 1].revisionId
                                revisedStartDate = HelperMethods.changeDateFormat(
                                        HelperMethods.newFormtaDateTime(
                                                "yyyy-MM-dd HH:mm",
                                                "yyyy-MM-dd HH:mm",
                                                response.body()!!.rentalObj.revisions[lenght - 1].start
                                        )
                                )
                                revisedEndDate = HelperMethods.changeDateFormat(
                                        HelperMethods.newFormtaDateTime(
                                                "yyyy-MM-dd HH:mm",
                                                "yyyy-MM-dd HH:mm",
                                                response.body()!!.rentalObj.revisions[lenght - 1].end
                                        )
                                )

                                txtRentalStartDate.text = revisedStartDate
                                txtRentalEndDate.text = revisedEndDate
                                btnShowoiginalDate.show()
                                textView.text =
                                        "" + response.body()!!.rentalObj.revisions[lenght - 1].revisionType.substring(0,1).toUpperCase()+ response.body()!!.rentalObj.revisions[lenght - 1].revisionType.substring(1) + " Request"
                            } else {
                                revisionId =
                                        response.body()!!.rentalObj.revisions[0].revisionId
                                txtRentalStartDate.text = originalStartDate
                                txtRentalEndDate.text = originalEndDate
                                btnShowoiginalDate.gone()
                            }
                            //approvalStatus=mainjsonObject.getInt("isApproved").toString()
                            txtCustomerAddress.text =
                                    response.body()!!.rentalObj.bookedByUser.address.text

                            if (response.body()!!.rentalObj.revisions[lenght-1].isApproved == 1) {
                                if (response.body()!!.rentalObj.rentalStatus == "approved" || response.body()!!.rentalObj.rentalStatus == "booked") {
                                    if (SharedPrefsUtils.getStringPreferenceWithDefaultValue(context, "Dropoff", "false") == "true") {
                                        btnApprove.text = "Start Tracking"
                                        btnDecline.gone()
                                        btnApprove.isClickable = true
                                    } else {
                                        btnApprove.text = "Request Approved"
                                        btnDecline.gone()
                                        btnApprove.setClickable(false)
                                    }
                                } else if (response.body()!!.rentalObj.rentalStatus == "delivered") {
                                    btnApprove.text = "End Booking"
                                    btnDecline.gone()
                                    btnApprove.isClickable = true
                                }
                            } else if (response.body()!!.rentalObj.isApproved == 2) {
                                btnDecline.text = "Request Rejected"
                                btnApprove.gone()
                                btnDecline.isClickable = false
                            } else {
                                btnApprove.isClickable = true
                                btnDecline.isClickable = true
                                btnApprove.text = "Approve Request"
                                btnDecline.text = "Reject Request"
                                btnDecline.show()
                                btnApprove.show()
                            }

                            destLat = response.body()!!.rentalObj.dropOffLocation.coordinates[0]
                            destLong = response.body()!!.rentalObj.dropOffLocation.coordinates[1]

                            //val directionURl = getUrlForDirection(LatLng(18.5204, 73.8567), LatLng(19.8762, 75.3433))
                            srcLat = response.body()!!.rentalObj.bookedByUser.address.coordinates[0]
                            srcLong =
                                    response.body()!!.rentalObj.bookedByUser.address.coordinates[1]
                            var origin = LatLng(srcLat, srcLong)
                            var dest = LatLng(destLat, destLong)
                            val directionURl = getUrlForDirection(origin, dest)
                            val downloadTask = downloadTask()
                            downloadTask.execute(directionURl)
                        } else {
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
                    HelperMethods.closeEverythingOpenSplash(this@RequestApproveActivity)
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

    fun changeRequestStatusAPI(rentalId: String, revisionId: String, approvalStatus: String) {
        showProgressDialog("Please wait..")
        val jsonObject = JsonObject()
        jsonObject.addProperty("rentalId", rentalId)
        jsonObject.addProperty("revisionId", revisionId)
        jsonObject.addProperty("approvalStatus", approvalStatus)
        jsonObject.addProperty("NOPAYMENT", true)

        CallApi().onTrailerOptionSelected(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    dismissProgressDialog()
                    if (response != null && response.success) {
                        showToast("Trailer request $approvalStatus Successfully")
                        //HelperMethods.closeEverythingOpenHome(context)
                        onBackPressed()
                    } else
                        showToast(HelperMethods.safeText(response.message, ""))
                }

                override fun onError(e: Throwable) {
                    dismissProgressDialog()
                    showToast(e.message)
                }
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun getUrlForDirection(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyCr81cOWzYioAUJPsf0xqsoiPEFjJr2b8U"
    }

    private inner class downloadTask :
        AsyncTask<Any?, String?, String?>() {
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val parserTask = ParserTask(RequestApproveActivity())
            parserTask.execute(result)
        }

        override fun doInBackground(vararg params: Any?): String? {
            var data = ""
            try {
                data = HelperMethods.downloadUrl(params[0].toString())
            } catch (e: java.lang.Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }
    }

    lateinit var sydney: LatLng

    private inner class ParserTask(private var activity: RequestApproveActivity?) :
        AsyncTask<String, Integer, List<List<HashMap<String, String>>>>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData.get(0))
                val parser = DirectionJSONParser()
                routes = parser.parse(jObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            super.onPostExecute(result)
            var points = ArrayList<LatLng>()
            var lineOptions: PolylineOptions? = null
            val markerOptions = MarkerOptions()


            for (i in 0 until result!!.size) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path: List<HashMap<String, String>> = result!![i]
                for (j in 0 until path.size) {
                    val point: HashMap<String, String> = path[j]
                    val lat: Double? = point.get("lat")?.toDouble()
                    val lng: Double? = point.get("lng")?.toDouble()
                    val position = LatLng(lat!!, lng!!)
                    points.add(position)
                }

                sydney = LatLng(path[0].get("lat")!!.toDouble(), path[0].get("lng")!!.toDouble())
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }
            if (lineOptions != null) {
                mMap.addMarker(
                        MarkerOptions()
                                .title("You")
                                .position(sydney)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.address_location_icon_red))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f))
                mMap.addPolyline(lineOptions)
            } else {
                val location = LatLng(srcLat, srcLong)
                mMap.addMarker(
                        MarkerOptions()
                                .title("You")
                                .position(location)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.address_location_icon_red))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
            }


        }

    }

    private fun askLocationPermission() {
        if (PermissionUtils.hasPermissions(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
        ) {
            createLocationRequest()
            checkForLocationEnabled()
        } else {
            PermissionUtils.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PermissionUtils.MULTIPLE_PERMISSION
            )
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest?.interval = LOCATION_REQUEST_INTERVAL_TIME
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun checkForLocationEnabled() {
        showProgressDialog()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        builder.addLocationRequest(locationRequest!!)
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                // fetchUserCurrentLocation()
            }.addOnFailureListener { e: Exception? ->
                if (e is ApiException) {
                    dismissProgressDialog()
                    if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED && e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(this, LOCATION_RESOLUTION_REQUEST_CODE)
                        } catch (ex: IntentSender.SendIntentException) {
                            dismissProgressDialog()
                            showToast("Error while getting location")
                            ex.printStackTrace()
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_RESOLUTION_REQUEST_CODE) {
            createLocationRequest()
            //fetchUserCurrentLocation()
        } else {
            showToast("Accept Location Request to Start Delivery")
            Handler().postDelayed({
                checkForLocationEnabled()
            }, 500)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.MULTIPLE_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                if (!PermissionUtils.hasPermissions(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                ) {
                    PermissionUtils.requestPermissions(
                            this,
                            arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            PermissionUtils.MULTIPLE_PERMISSION
                    )
                } else {
                    createLocationRequest()
                    checkForLocationEnabled()
                }
            }
        }
    }

    fun dismissBottomSheet() {
        if (bottomSheetFragment != null) {
            bottomSheetFragment!!.dismiss()
            bottomSheetFragment = null
        }
    }

    var partLicensePic: MultipartBody.Part? = null
    fun addLicenseToserver() {

        if (TrackingLicenseBottomSheetFragment.photoSelectedBitmap != null) {
            val file = Utils.convertBitmapToFile("temp.png",
                    TrackingLicenseBottomSheetFragment.photoSelectedBitmap!!, context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                    file?.asRequestBody("image/jpg".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            partLicensePic = requestFile?.let {
                MultipartBody.Part.createFormData("driverLicenseScan", file.name,
                        it
                )
            }
        }
    }
}
