package devx.app.licensee.modules.trailerTracking

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.makeramen.roundedimageview.RoundedImageView
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ChangeRentalStatusRequest
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.RegularTextView
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.services.SignalSenderService
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.webapi.response.CommonResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_trailer_tracking.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject

private const val LOCATION_RESOLUTION_REQUEST_CODE = 100
private const val LOCATION_REQUEST_INTERVAL_TIME = 45000L

class TrailerTrackingActivity : BaseActivity(), OnMapReadyCallback,LocationListener {

    private lateinit var mMap: GoogleMap
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var pickup=false
    private var dropoff=false
    private var isContinuousLocationUpdate = false
    private var bottomSheetFragment: TrackingLicenseBottomSheetFragment? = null

    var gpsService: SignalSenderService? = null
    companion object{
        var rentalId =""
        var invoiceNumber =""
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val name: String = className.className
            Log.d("here", "service conn")
                Log.d("Here", "gpsservice set");
                gpsService = (binder as SignalSenderService.LocationServiceBinder).service
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "SignalSenderService") {
                gpsService = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer_tracking)
        CommonTopbarView().setup(this)
        //prepare service
        val intent1 = Intent(this, SignalSenderService::class.java)
        bindService(intent1, serviceConnection, Context.BIND_AUTO_CREATE)

        if (intent != null && intent.hasExtra("id"))
            rentalId = intent.getStringExtra("id")!!

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        askLocationPermission()
        startDelivery("start", if (pickup) "pickup" else "dropoff", rentalId)

        bottomButton.setOnClickListener {
            if (bottomButton.text.toString() == "Start Delivery") {
                //bottomButton.isEnabled = false

            }
            else if (bottomButton.text.toString() == "Stop Delivery") {
                // bottomButton.isEnabled = false
                if (bottomSheetFragment == null) bottomSheetFragment = TrackingLicenseBottomSheetFragment("")
                if (bottomSheetFragment?.isAdded!!) return@setOnClickListener
                bottomSheetFragment!!.isCancelable = true
                bottomSheetFragment!!.show(supportFragmentManager, "")
                // openTakeLicensePictureDialog()

            }
        }
        txtViewCustomerLicense.setOnClickListener{
            showCustomerScan()
        }
        if(Utils.isNetworkAvailable(context)){
            getRequestedDetails()
        }
        else{
            showToast("No Internet Connectivity!")
        }
    }

        /*Show employee action bottom sheet layout*/
        var showScanbottomSheetDialog: BottomSheetDialog? = null
        var showScanbottomSheetView: View? = null
        var imgShowScan: RoundedImageView?=null
        var btnCancelShowScan: RegularTextView?=null
        private fun showCustomerScan() {
            showScanbottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
            val behaviorField: BottomSheetBehavior<*> = showScanbottomSheetDialog!!.getBehavior()
            behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
            behaviorField.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float
                ) {
                }
            })
            showScanbottomSheetView = layoutInflater
                .inflate(
                        R.layout.sheet_show_scan, null)


            imgShowScan=showScanbottomSheetView!!.findViewById(R.id.imgShowScan)
            btnCancelShowScan=showScanbottomSheetView!!.findViewById(R.id.btnCancel)

            btnCancelShowScan!!.setOnClickListener {
                showScanbottomSheetDialog!!.cancel()
            }
            HelperMethods.downloadImage(licenseURL, context!!, imgShowScan)


            showScanbottomSheetDialog!!.setContentView(showScanbottomSheetView!!)

            showScanbottomSheetDialog!!.show()
        }

    private fun startDelivery(action: String, type: String, rentalId: String) {
        var tempJsonObject=JsonObject()
        tempJsonObject.addProperty("rentalId", rentalId)
        if(pickup) {
            tempJsonObject.addProperty(
                    "type", "pickup"
            )
        }
        else if (dropoff){
            tempJsonObject.addProperty(
                    "type", "dropoff"
            )
        }
        tempJsonObject.addProperty("action", "start")

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempJsonObject.toString())


        Log.d("here", "/track called")
        CallApi().startDeliveryTracking(requestBodyAllInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    dismissProgressDialog()
                    if (response == null) {
                        showToast("Something went wrong ...")
                        return
                    }

                    if (response.success) {
                        showToast("Delivery started successfully!")
                        isContinuousLocationUpdate = true
                        if (fusedLocationProviderClient != null && locationCallback != null) {
                            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
                        }
                        if (locationRequest != null) locationCallback = null
                        askLocationPermission()
                        bottomButton.text = "Stop Delivery"
                        Log.d("Here", "null??")
                        gpsService?.startTracking();
                    } else
                        showToast(HelperMethods.safeText(response.message, ""))
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                }
            })
    }


    var licenseURL=""
    private fun getRequestedDetails() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)
        val url: String = ConstantApi.LIVE_BASE_URL+"rental/details?"

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization", HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url + "id=$rentalId")
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
        Log.d("Here", "Called????")
        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals(ConstantApi.LIVE_BASE_URL + "rental/details?id=$rentalId", ignoreCase = true)) {
                        if (`object` != null) {
                            if (`object`.getBoolean("success")) {

                                val mainjsonObject: JSONObject =`object`.getJSONObject("rentalObj")

                                invoiceNumber=mainjsonObject.getString("invoiceNumber");
                                SignalSenderService.invoiceNumber=mainjsonObject.getString("invoiceNumber");
                                SignalSenderService.rentalId = rentalId
                                userName.text=mainjsonObject.getJSONObject("bookedByUser").getString("name")
                                userLocation.text=mainjsonObject.getJSONObject("bookedByUser").getJSONObject("address").getString("text")

                                licenseURL=mainjsonObject.getJSONObject("bookedByUser").getJSONObject("driverLicense").getJSONObject("scan").getString("data")
                                pickup=HelperMethods.compareDate(mainjsonObject.getJSONObject("rentalPeriod").getString("end"))
                                dropoff=HelperMethods.compareDate(mainjsonObject.getJSONObject("rentalPeriod").getString("start"))

                                HelperMethods.downloadImage(mainjsonObject.getJSONObject("bookedByUser").getJSONObject("photo").getString("data"), context, userProfileImageView)


                                var srcLat=mainjsonObject.getJSONObject("pickUpLocation").getJSONArray("coordinates").getDouble(0)
                                var srcLong=mainjsonObject.getJSONObject("pickUpLocation").getJSONArray("coordinates").getDouble(1)
                                var destLat=mainjsonObject.getJSONObject("dropOffLocation").getJSONArray("coordinates").getDouble(0)
                                var destLong=mainjsonObject.getJSONObject("dropOffLocation").getJSONArray("coordinates").getDouble(1)

                                 val url = getUrlForDirection(LatLng(srcLat, srcLong), LatLng(destLat, destLong))
                                //val url = getUrlForDirection(LatLng(18.5204, 73.8567), LatLng(19.8762, 75.3433))
                                getDirection(url).execute()

                                if(mainjsonObject.getBoolean("isTrackingPickUp")||mainjsonObject.getBoolean("isTrackingDropOff")){
                                    isContinuousLocationUpdate=true
                                    if (fusedLocationProviderClient != null && locationCallback != null) {
                                        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
                                    }
                                    if (locationRequest != null) locationCallback = null
                                    askLocationPermission()
                                    bottomButton.text="Stop Delivery"
                                }
                            } else {
                                showToast(HelperMethods.safeText(`object`.getString("message"), ""))
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

    fun dismissBottomSheet() {
        if (bottomSheetFragment != null) {
            bottomSheetFragment!!.dismiss()
            bottomSheetFragment = null
        }
    }

    var partLicensePic: MultipartBody.Part? = null
    fun addLicenseToserver() {

        if (TrackingLicenseBottomSheetFragment.photoSelectedBitmap!= null) {
            val file=Utils.convertBitmapToFile("temp.png",
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

        var tempJsonObject=JsonObject()
        tempJsonObject.addProperty("rentalId", rentalId)
        if(pickup) {
            tempJsonObject.addProperty(
                    "type", "pickup"
            )
        }
        else if (dropoff){
            tempJsonObject.addProperty(
                    "type", "dropoff"
            )
        }
        tempJsonObject.addProperty("action", "end")

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempJsonObject.toString())

        CallApi().stopDeliveryTracking(requestBodyAllInfo, partLicensePic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    dismissProgressDialog()
                    if (response == null) {
                        showToast("Something went wrong ...")
                        return
                    }
                    if (response.success) {
                        showToast("Delivery ended successfully!")
                        isContinuousLocationUpdate = false
                        gpsService?.stopTracking();
                        HelperMethods.closeEverythingOpenHome(context)
                    } else
                        showToast(HelperMethods.safeText(response.message, ""))
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                }
            })
        val api = ServiceGenerator.getApi()
        var tempJsonObject1=JsonObject()
        tempJsonObject1.addProperty("rentalId", rentalId)
        tempJsonObject1.addProperty("status", "delivered")
        val request : RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),tempJsonObject1.toString())
        val startReturnCall : retrofit2.Call<ResponseBody> = api.startReturn(request, partLicensePic)
        startReturnCall.enqueue(object : retrofit2.Callback<ResponseBody>{
            override fun onResponse(call: retrofit2.Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if(response.isSuccessful){
                    showToast("Trailer rental ended")
                }
            }
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                showDebugToast(t.message)
            }
        })


    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun askLocationPermission() {
        if (PermissionUtils.hasPermissions(
                        this@TrailerTrackingActivity,
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
                fetchUserCurrentLocation()
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
            fetchUserCurrentLocation()
        }
        else  if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(this@TrailerTrackingActivity)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                    //setImageData(pickedPhotoBitmap)
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection cancelled")
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
                                this@TrailerTrackingActivity,
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

    private fun fetchUserCurrentLocation() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                dismissProgressDialog()
                val latitude = locationResult.lastLocation.latitude
                val longitude = locationResult.lastLocation.longitude

                if (latitude > 0 && longitude > 0) {
                    HelperMethods.setLatitude(this@TrailerTrackingActivity, latitude.toString())
                    HelperMethods.setLongitude(this@TrailerTrackingActivity, longitude.toString())
                    mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
                    mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    LatLng(latitude, longitude),
                                    16.0f
                            )
                    )
                    //showToast("My location")
                    //val location3 = LatLng(19.215438, 72.833671)
                    // mMap.addMarker(MarkerOptions().position(location3).title("Near Location"))


                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )
        //Looper.myLooper()
    }


    private fun getUrlForDirection(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyCr81cOWzYioAUJPsf0xqsoiPEFjJr2b8U"
    }

    private inner class getDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("errorGoogleMap", e.toString())
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineOption = PolylineOptions()
            for (i in result.indices) {
                lineOption.addAll(result[i])
                lineOption.width(5f)
                lineOption.color(ResourcesUtil.getColor(R.color.green))
                lineOption.geodesic(true)
            }
            mMap.addPolyline(lineOption)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    private fun updateTrailerLocation(jsonObject: JsonObject) {
        CallApi().updateTrailerLocation(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {

                    }
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                }
            })
    }

    override fun onLocationChanged(location: android.location.Location?) {
        // Getting longitude of the current location
        val longitude: Double = location!!.longitude
        val latitude: Double = location!!.latitude

        // Creating a LatLng object for the current location
        val latLng = LatLng(latitude, longitude)

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
        //start = latLng
        showToast("Location Changed: $latLng")
        Log.d("Location Changed: ", latLng.toString())

        mMap.addMarker(
                MarkerOptions()
                        .title("You")
                        .position(LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_vehicle))
        )
        if (isContinuousLocationUpdate) {
            val locationJsonArray = JsonArray()
            locationJsonArray.add(latitude)
            locationJsonArray.add(longitude)

            //updateTrailerLocation(jsonObject)
        }

    }


}
