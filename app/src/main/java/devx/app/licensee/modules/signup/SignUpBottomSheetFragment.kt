package devx.app.licensee.modules.signup

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import com.makeramen.roundedimageview.RoundedImageView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.custumViews.RegularTextView
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity
import devx.app.licensee.modules.inviteEmployees.PermissionItem
import devx.app.seller.modules.signup.SignUpActivity
import kotlinx.android.synthetic.main.sheet_driver_license.*

class SignUpBottomSheetFragment(
    flag:String
) : BottomSheetDialogFragment() {


    private var selectedState = ""
    var photoSelected = ""
    var tempStr=flag

    companion object{
         var photoSelectedBitmap : Bitmap? =null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_driver_license, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        closeDLDialog.setOnClickListener {
             if(tempStr=="signup")
                (activity as SignUpActivity).dismissBottomSheet()
            else
                 (activity as ProfileEditActivity).dismissBottomSheet()
        }

        scanLayout.setOnClickListener {
            showBottomSheet()
        }

        addDriverLicenceLayout.setOnClickListener {
            if (cardNumberET.text.toString().isEmpty() || cardNumberET.text.toString().length < 2) {
                HelperMethods.showToastbar(context, "Please enter valid card number")
                return@setOnClickListener
            }

            if (DLMonthExpiry.text.isEmpty()) {
                DLMonthExpiry.requestFocus()
                HelperMethods.showToastbar(context, "Please enter expiry month")
                return@setOnClickListener
            }

            if (DLMonthExpiry.text.toString().toInt() > 12) {
                DLMonthExpiry.requestFocus()
                HelperMethods.showToastbar(context, "Please enter valid expiry month")
                return@setOnClickListener
            }

            if (DLYearExpiry.text.isEmpty()) {
                DLYearExpiry.requestFocus()
                HelperMethods.showToastbar(context, "Please enter expiry year")
                return@setOnClickListener
            }

            if (DLYearExpiry.text.toString().toInt() <= 2020) {
                DLYearExpiry.requestFocus()
                HelperMethods.showToastbar(context, "Please make sure driving license is not expired. Expecting year > 2020")
                return@setOnClickListener
            }

            if (selectedState.isEmpty() || selectedState == "" || selectedState == "SELECT STATE") {
                HelperMethods.showToastbar(context, "Please select state")
                return@setOnClickListener
            }

            if(tempStr=="signup") {
                if (photoSelected.isEmpty()) {
                    HelperMethods.showToastbar(context, "Please add photo of Driving licence")
                    return@setOnClickListener
                }
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("card", cardNumberET.text.toString())
            jsonObject.addProperty(
                "expiry",
                DLYearExpiry.text.toString()+ "-" + DLMonthExpiry.text.toString()
            )
            jsonObject.addProperty("state", selectedState)
            jsonObject.addProperty("employeeDriverLicenseScan", photoSelected)
            if(tempStr=="signup") {
                (activity as SignUpActivity).setLicenseAdded(jsonObject)
                (activity as SignUpActivity).dismissBottomSheet()
            }
            else {
                (activity as ProfileEditActivity).setLicenseAdded(jsonObject)
                (activity as ProfileEditActivity).dismissBottomSheet()
            }
        }

        setUpStateListSpinner()
        if(tempStr=="profile"){

            cardNumberET.text=ProfileEditActivity.driverLicenceInfo.get("card").asString.toEditable()
            DLMonthExpiry.text=ProfileEditActivity.driverLicenceInfo.get("expiry").asString.split("-")[1].toEditable()
            DLYearExpiry.text=ProfileEditActivity.driverLicenceInfo.get("expiry").asString.split("-")[0].toEditable()
            if(stateList.contains(ProfileEditActivity.driverLicenceInfo.get("state").asString)){
                stateSpinner.setSelection(stateList.indexOf(ProfileEditActivity.driverLicenceInfo.get("state").asString))
            }
            if(ProfileEditActivity.driverLicenceInfo.has("employeeDriverLicenseScan")){
                scanImageTv.text = "Photo added"
            }
        }

        if(tempStr=="signup" && SignUpActivity.licenseAdded){
            cardNumberET.text=SignUpActivity.driverLicenceInfo.get("card").asString.toEditable()
            DLMonthExpiry.text=SignUpActivity.driverLicenceInfo.get("expiry").asString.split("-")[1].toEditable()
            DLYearExpiry.text=SignUpActivity.driverLicenceInfo.get("expiry").asString.split("-")[0].toEditable()
            if(stateList.contains(SignUpActivity.driverLicenceInfo.get("state").asString)){
                stateSpinner.setSelection(stateList.indexOf(SignUpActivity.driverLicenceInfo.get("state").asString))
            }
            if(SignUpActivity.driverLicenceInfo.has("employeeDriverLicenseScan")){
                scanImageTv.text = "Photo added"
            }
        }
    }

    val stateList = arrayListOf<String>()
    private fun setUpStateListSpinner() {
        stateList.clear()
        stateList.add("Select State")
        stateList.add("New South Wales")
        stateList.add("Queensland")
        stateList.add("Tasmania")
        stateList.add("Western Australia")
        stateList.add("Victoria")
        stateList.add("Northern Territory")
        stateList.add("Australian Capital Territory")
        stateList.add("South Australia")

        val adapter =
            ArrayAdapter(context!!, R.layout.sgl_item_spinner, stateList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = adapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                selectedState = stateList[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                selectedState = "Select State"
            }
        }
    }

    fun setScanPhotoAdded() {
        scanImageTv.text = "Photo added"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            try {
                val pickedPhotoBitmap: Bitmap
                val storage = EZPhotoPickStorage(activity)
                pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                if (pickedPhotoBitmap != null) {
                    HelperMethods.showToastbar(activity, "Image selected")
                    photoSelected = HelperMethods.getBitmapInto64BaseEncoded(pickedPhotoBitmap)
                    photoSelectedBitmap=pickedPhotoBitmap
                } else {
                    HelperMethods.showToastbar(activity, "Image not selected")
                }
            } catch (ee: java.lang.Exception) {
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
    /*Show employee action bottom sheet layout*/
    var bottomSheetDialog: BottomSheetDialog? = null
    var bottomSheetView: View? = null
    var btnUpdateScan: RegularTextView?=null
    var btnViewScan: RegularTextView?=null
    var btnCancel: RegularTextView?=null
    fun showBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val behaviorField: BottomSheetBehavior<*> = bottomSheetDialog!!.getBehavior()
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
        bottomSheetView = layoutInflater
            .inflate(
                R.layout.sheet_update_document,null)


        btnUpdateScan=bottomSheetView!!.findViewById(R.id.btnUpdateScan)
        btnViewScan=bottomSheetView!!.findViewById(R.id.btnViewScan)
        btnCancel=bottomSheetView!!.findViewById(R.id.btnCancel)

        if(scanImageTv.text == "Photo added"){
            btnViewScan.show()
        }
        else{
            btnViewScan.gone()
        }
        btnCancel!!.setOnClickListener {
            bottomSheetDialog!!.cancel()
        }

        btnViewScan!!.setOnClickListener{

            bottomSheetDialog!!.cancel()
            showScanBottomSheet()


        }
        btnUpdateScan!!.setOnClickListener {
            bottomSheetDialog!!.cancel()
            val bottomSheet = SelectDocumentBottomSheet("signup")
            bottomSheet.isCancelable = true
            bottomSheet.setContext(this)
            bottomSheet.show(fragmentManager!!, bottomSheet.tag)

        }

        bottomSheetDialog!!.setContentView(bottomSheetView!!)

        bottomSheetDialog!!.show()
    }
    /*Show employee action bottom sheet layout*/
    var showScanbottomSheetDialog: BottomSheetDialog? = null
    var showScanbottomSheetView: View? = null
    var imgShowScan: RoundedImageView?=null
    var btnCancelShowScan: RegularTextView?=null
    fun showScanBottomSheet() {
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
                R.layout.sheet_show_scan,null)


        imgShowScan=showScanbottomSheetView!!.findViewById(R.id.imgShowScan)
        btnCancelShowScan=showScanbottomSheetView!!.findViewById(R.id.btnCancel)

        btnCancelShowScan!!.setOnClickListener {
            showScanbottomSheetDialog!!.cancel()
        }
        if(tempStr=="signup") {
            if(photoSelectedBitmap!=null) {
                imgShowScan!!.setImageBitmap(photoSelectedBitmap)
            }
        }
        else{
            if(photoSelectedBitmap!=null) {
                imgShowScan!!.setImageBitmap(photoSelectedBitmap)
            }
            else{
                HelperMethods.downloadImage(ProfileEditActivity.driverLicenceInfo.get("scan").asString,context!!,imgShowScan)
            }
        }


        showScanbottomSheetDialog!!.setContentView(showScanbottomSheetView!!)

        showScanbottomSheetDialog!!.show()
    }
}