package devx.app.licensee.modules.signup

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import devx.app.seller.webapi.response.CommonResponse
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.RelativeLayout
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.panWithCallback
import devx.app.licensee.R
import devx.app.licensee.common.AnimUtils
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.response.geocoding.Callback
import devx.app.licensee.webapi.response.geocoding.Constants.AddressTypes
import devx.app.licensee.webapi.response.geocoding.Coordinate
import devx.app.licensee.webapi.response.geocoding.Response
import devx.app.licensee.webapi.response.geocoding.ReverseGeocoding
import devx.app.seller.modules.signup.SignUpActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_location_bottom_sheet.*
import java.io.IOException

class LocationBottomSheetFragment(flag:String) : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mapFragment: SupportMapFragment? = null
    private var lastMarker: Marker? = null
    private var shouldLoadLatLong = true
    private var forceAnimate = true
    private var shouldForceSearch = false
    private var forceCameraZoom = true
    var recentering = false
    private var loadingDialog: Dialog? = null
    private var locationListener: AddLocationListener? = null

    private var mLandMark = ""
    private var mCity = ""
    private var mState = ""
    private var mCountry = "Australia"
    private var mLocality = ""
    private var mStreetName = ""
    var postalCode: String = ""
    private var placeSelected = ""
    private var appCompatActivity: Activity? = null
    private var mapView: View? = null
    val tempStr=flag


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }
        return inflater.inflate(R.layout.fragment_location_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = ProgressDialog(context, R.style.MyTheme)
        loadingDialog?.setCancelable(false)

        if (tempStr=="signup") {

            appCompatActivity = (activity as SignUpActivity)
            if(SignUpActivity.isSelectUserAddress){
                houseNumberET.hint="House Number"
            }
            else{
                houseNumberET.hint="Company Plot Number"
            }
        }
        else{
            appCompatActivity = (activity as ProfileEditActivity)
            if(ProfileEditActivity.isSelectUserAddress){
                houseNumberET.hint="House Number"
            }
            else{
                houseNumberET.hint="Company Plot Number"
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context!!)
        if (activity != null) {
            mapFragment =
                activity!!.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapView = mapFragment?.view!!
            mapFragment?.getMapAsync(this)
        }

        closeDialog.setOnClickListener {
            dismiss()
        }

        addAddressLayout.setOnClickListener {

            if (postalCode == "") {
                HelperMethods.showToastbar(
                    context,
                    "Pincode is empty.. Try with different location.."
                )
                return@setOnClickListener
            }


            val addressObject = JsonObject()
            val jsonArray = JsonArray()



            if (houseNumberET.text.toString().isNotEmpty()) {
                addressObject.addProperty(
                    "addressTitle",
                    houseNumberET.text.toString() + ", " + addressTitle.text.toString()
                )
                addressObject.addProperty(
                    "text",
                    houseNumberET.text.toString() + ", " +addressTitle.text.toString() + ", " + addressText.text.toString()
                )
            } else {
                addressObject.addProperty("addressTitle", addressTitle.text.toString())
                addressObject.addProperty(
                    "text",
                    addressTitle.text.toString() + ", " + addressText.text.toString()
                )
            }

            addressObject.addProperty("pincode", postalCode)
            addressObject.addProperty("country", mCountry)
            addressObject.addProperty("state", mState)
            addressObject.addProperty("city", mCity)

            jsonArray.add(lastMarker?.position?.latitude)
            jsonArray.add(lastMarker?.position?.longitude)
            addressObject.add("coordinates", jsonArray)
            if (tempStr=="signup") {
                (activity as SignUpActivity).setUserAddress(addressObject)
                dismiss()
                return@setOnClickListener
            }
            else{
                (activity as ProfileEditActivity).setUserAddress(addressObject)
                dismiss()
                return@setOnClickListener
            }
            /*val aa = JsonObject()
            aa.addProperty("_id", HelperMethods.getUserId())
            aa.addProperty("address", addressObject.toString())
            addUserAddress(aa)*/
        }

        txtLocation.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    HelperMethods.openPlaceSearch(appCompatActivity)
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }

    }



    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        locationListener = activity as AddLocationListener
    }

    private fun reCenterMarkerOnMap() {
        AnimUtils.bounceAnimation(markerRing, 2000)
        setMarker(true)
    }

    private fun setMarker(megaZoomAnimate: Boolean) {
        if (lastMarker == null)
            return

        removeMarker()
        lastMarker = Marker()
        lastMarker?.position = (mMap.cameraPosition?.target)

        if (lastMarker!!.position != null) {
            val location =
                LatLng(lastMarker!!.position!!.latitude, lastMarker!!.position!!.longitude)

            if (megaZoomAnimate) mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    14f
                )
            )
        }
    }

    private fun removeMarker() {
        if (lastMarker != null)
            lastMarker?.remove()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnCameraIdleListener {
            if (recentering) return@setOnCameraIdleListener

            shouldLoadLatLong = true
            Handler().postDelayed({
                if (shouldLoadLatLong) {
                    shouldLoadLatLong = false
                    if (forceCameraZoom) {
                        forceCameraZoom = false
                    }
                    val latLng = mMap.cameraPosition.target
                    latLongSearch(latLng.latitude, latLng.longitude, true, null)
                }
            }, 700)
        }

        askLocationPermission()

        val myLocationButton =
            (mapView?.findViewById<View>("1".toInt())?.parent as View).findViewById<View>("2".toInt())
        val layoutParams = myLocationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.setMargins(0, 30, 20, 0)
    }

    private fun askLocationPermission() {
        if (PermissionUtils.hasPermissions(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            createLocationRequest()
            checkForLocationEnabled()
        } else {
            PermissionUtils.requestPermissions(
                activity,
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
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun checkForLocationEnabled() {
        showProgressDialog()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        builder.addLocationRequest(locationRequest!!)

        val settingsClient = LocationServices.getSettingsClient(context!!)
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                fetchUserCurrentLocation()
            }.addOnFailureListener { e: Exception? ->
                if (e is ApiException) {
                    dismissProgressDialog()
                    if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED && e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(
                                appCompatActivity,
                                Constants.LOCATION_RESOLUTION_REQUEST_CODE
                            )
                        } catch (ex: IntentSender.SendIntentException) {
                            dismissProgressDialog()
                            HelperMethods.showToastbar(activity, "Error while getting location")
                            ex.printStackTrace()
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentParams.REQUEST_SELECT_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (place != null)
                    onPlaceSelected(place)
                else
                    HelperMethods.showToastbar(
                        context,
                        "Error in getting place details.. Please try different place.."
                    )
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                showProgressDialog()
                fetchUserCurrentLocation()
                HelperMethods.showToastbar(
                    context,
                    "Error in getting place details.. Please try different place.."
                )
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.LOCATION_RESOLUTION_REQUEST_CODE) {
            createLocationRequest()
            fetchUserCurrentLocation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun putMarkerOnMap(lat: Double, long: Double, isAnimateCamera: Boolean) {
        if (lastMarker != null)
            lastMarker?.remove()

        lastMarker = Marker()
        lastMarker?.position = mMap.cameraPosition.target

        if (isAnimateCamera && isValidLatLng() && forceAnimate)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 16f))
    }

    private fun isValidLatLng(): Boolean {
        try {
            if (lastMarker?.position?.latitude!! < -90 || lastMarker?.position?.latitude!! > 90)
                return false;
            else if (lastMarker?.position?.longitude!! < -180 || lastMarker?.position?.longitude!! > 180)
                return false;
        } catch (e: java.lang.Exception) {
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.MULTIPLE_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                if (!PermissionUtils.hasPermissions(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    PermissionUtils.requestPermissions(
                        activity,
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
                fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
                val latitude = locationResult.lastLocation.latitude
                val longitude = locationResult.lastLocation.longitude

                if (latitude > 0 && longitude > 0) {
                    HelperMethods.setLatitude(activity, latitude.toString())
                    HelperMethods.setLongitude(activity, longitude.toString())
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(latitude, longitude),
                            15f
                        )
                    )
                    shouldForceSearch = true
                    reCenterMarkerOnMap()
                    forceAnimate = true
                    latLongSearch(latitude, longitude, true, null)
                }
            }
        }

        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun showProgressDialog() {
        try {
            if (loadingDialog != null)
                if (!loadingDialog!!.isShowing) loadingDialog!!.show()
        } catch (e: java.lang.Exception) {
        }
    }

    fun dismissProgressDialog() {
        try {
            if (loadingDialog != null && loadingDialog?.isShowing!!)
                loadingDialog?.dismiss()
        } catch (e: java.lang.Exception) {
            try {
                loadingDialog?.dismiss()
            } catch (ee: java.lang.Exception) {
            }
        }
    }

    private fun onPlaceSelected(it: Place) {
        if (it.latLng != null) {
            placeSelected = it.name + " , " + it.address
            AnimUtils.bounceAnimation(markerRing, 2000)
            forceAnimate = true
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latLng!!.latitude,
                        it.latLng!!.longitude
                    ), 16f
                )
            )
            putMarkerOnMap(it.latLng!!.latitude, it.latLng!!.longitude, true)
        }
        forceAnimate = false
        latLongSearch(1.0, 1.0, false, it)
    }

    private fun latLongSearch(lat: Double, long: Double, isLatLongSearch: Boolean, place: Place?) {
        showProgressDialog()
        val coordinate = Coordinate(lat, long)
        val reverseGeoCoding: ReverseGeocoding?
        if (!isLatLongSearch) {
            reverseGeoCoding = ReverseGeocoding(place!!.id, Constants.GOOGLE_MAP_KAY)
        } else {
            reverseGeoCoding = ReverseGeocoding(coordinate, Constants.GOOGLE_MAP_KAY)
            putMarkerOnMap(lat, long, false)
        }

        reverseGeoCoding.fetch(object : Callback {
            override fun onResponse(response: Response?) {
                dismissProgressDialog()
                if (addressTitle == null) return
                addressTitle.text = "Select Location"

                if (response == null) return
                if (response.results == null) return
                if (response.results.isEmpty()) return
                val result = response.results[0]
                var tempCity = ""

                mState = ""
                mCity = ""
                mCountry = ""
                mLandMark = ""
                mStreetName = ""
                mLocality = ""
                postalCode = ""

                for (ac in result.addressComponents.indices) {
                    for (element in result.addressComponents[ac].addressTypes) {
                        val longName: String = result.addressComponents[ac].longName
                        when (element) {
                            AddressTypes.COUNTRY -> mCountry = longName
                            AddressTypes.POSTAL_CODE -> postalCode = longName
                            AddressTypes.ADMINISTRATIVE_AREA_LEVEL_1 -> mState = longName
                            AddressTypes.LOCALITY -> mCity = longName
                            AddressTypes.ROUTE -> mStreetName =
                                if (HelperMethods.safeText(mStreetName)
                                        .isNotEmpty()
                                ) mStreetName else if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mStreetName else longName
                            AddressTypes.NEIGHBORHOOD -> mStreetName =
                                if (HelperMethods.safeText(mStreetName)
                                        .isNotEmpty()
                                ) mStreetName else if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mStreetName else longName
                            AddressTypes.SUBPREMISE -> mStreetName =
                                if (HelperMethods.safeText(mStreetName)
                                        .isNotEmpty()
                                ) mStreetName else if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mStreetName else longName
                            AddressTypes.SUBLOCALITY -> mLocality =
                                if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mLocality else longName
                            AddressTypes.ADMINISTRATIVE_AREA_LEVEL_3 -> tempCity =
                                if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) tempCity else longName
                            AddressTypes.ADMINISTRATIVE_AREA_LEVEL_4 -> mLocality =
                                if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mLocality else longName
                            AddressTypes.ADMINISTRATIVE_AREA_LEVEL_5 -> mLocality =
                                if (HelperMethods.safeText(longName)
                                        .isEmpty()
                                ) mLocality else longName
                        }
                    }
                }

                mCity = if ((if (mCity == null) "" else mCity).isEmpty()) tempCity else mCity
                mCountry =
                    if ((if (mCountry == null) "" else mCountry).isEmpty()) "Australia" else mCountry
                mState = (if (mState == null) "" else mState)
                mStreetName = if (HelperMethods.safeText(
                        if ((if (HelperMethods.safeText(mStreetName)
                                    .isEmpty()
                            ) mLocality else mStreetName).contains("Unnamed Road", true)
                        ) mCity else mStreetName
                    ).isEmpty()
                ) mCity else mStreetName

                if (!isLatLongSearch) {
                    if (HelperMethods.safeText(place!!.name).isNotEmpty())
                        mStreetName = place.name!! + ", " + mStreetName
                }

                addressTitle.text = "" + mStreetName + ", " + mLocality
                addressText.text = "" + mCity + " , " + mState + "-" + postalCode + ", " + mCountry
                landmarkET.setText("" + mStreetName + ", " + mLocality+", "+ mCity + " , " + mState)
            }

            override fun onFailed(response: Response?, exception: IOException?) {
                dismissProgressDialog()
                if (response != null && response.errorMessage != null) {
                    Log.d("error::", response.errorMessage)
                }
            }
        })
    }

    override fun onDestroy() {
        if (mapFragment != null)
            fragmentManager?.beginTransaction()?.remove(mapFragment!!)?.commit()
        super.onDestroy()
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
}
