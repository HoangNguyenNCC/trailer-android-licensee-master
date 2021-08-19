package devx.app.seller.modules.signup

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import com.google.gson.JsonObject
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.panWithCallback
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.*
import devx.app.licensee.common.utils.*
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.modules.signup.*
import devx.app.licensee.webapi.APIError
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ErrorUtils
import devx.app.licensee.webapi.ServiceGenerator
import kotlinx.android.synthetic.main.activity_signup.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : BaseActivity(), AddLocationListener, PickiTCallbacks,
    TimePickerDialog.OnTimeSetListener  , DatePickerDialog.OnDateSetListener {

    private var licenseData = ""

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()

    private var businessType = "Individual"

    companion object{
        var employeeObj = JsonObject()
        var profilePicData = ""
        lateinit var profilePicBitmap :Bitmap
         var additionalDocumentselectedFileData=""
         var additionalDocumentFile:File?=null
        var isSelectUserAddress = false
        var driverLicenceInfo = JsonObject()
        var licenseAdded = false
    }
    var licenseeObj = JsonObject()
    private var selectedFileData = ""

    private var isDocumentPicker = false
    //

    private var imageSelectionView: ImageView? = null
    private var pickiT: PickiT? = null
    private var isBusinessAddressSet = false
    private var isBusinessTimeFromSet = false
    private var isBusinessTimeEndSet = false
    private var isStartTimePicker = -1
    private var isLicenseSet = false
    private var isBusinessTypeSelected = false
    private var isBusinessDocumentSet= false

    private var selectedWeekDays = arrayListOf<String>()
    private var bottomSheetFragment: SignUpBottomSheetFragment? = null
    private var workingStartTime = ""
    private var workingEndTime = ""


    var businessAddressInfo = JsonObject()
    var userAddressInfo = JsonObject()
    private var isAddressSet = false
    private var locationBottomSheet = LocationBottomSheetFragment("signup")

    //additional parameter
    private var isAdditionalDocumentSet= false


    private var businessprofilePicData = ""
    private var isBusinessLOGOSet= false
    private lateinit var businessprofilePicBitmap :Bitmap
    private var selectedFileDataFile:File? = null
    private var DOB = ""

    var strFlagUser=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        pickiT = PickiT(this, this)
        HelperMethods.disableEditText(addressEditText)
        HelperMethods.disableEditText(businessLocationEditText)
        HelperMethods.disableEditText(addDrivingLicenseEditText)
        setUpBusinessTypeSpinner()



        lytSelectUserType.show()
        paymentLayout.gone()
        userDetailsLayout.gone()
        businessDetailsLayout.gone()
        lytSignupButton.gone()
        txtHeaderTitle.text = "Business"
        closeKeyBoard()

        closeIcon.setOnClickListener {
            onBackPressed()
        }

        btnOwner.setOnClickListener {
            strFlagUser="owner"
            lytSignupButton.show()

            lytSelectUserType.gone()
            paymentLayout.gone()
            userDetailsLayout.show()
            businessDetailsLayout.gone()

            createAccount.text = "NEXT : PAYMENT"
            txtHeaderTitle.text = "Business"
            signupScrollview.fullScroll(ScrollView.FOCUS_UP);
        }
        btnEmployee.setOnClickListener {
            strFlagUser="employee"
            lytSignupButton.show()

            lytSelectUserType.gone()
            paymentLayout.gone()
            userDetailsLayout.show()
            businessDetailsLayout.gone()

            createAccount.text = "TOKEN VERIFICATION"
            txtHeaderTitle.text = "Employee"
            signupScrollview.fullScroll(ScrollView.FOCUS_UP);
        }

        createAccount.setOnClickListener {
            closeKeyBoard()
            if (userDetailsLayout.visibility == View.VISIBLE) {
                if(isInvalidUserDetails()) return@setOnClickListener

                signupScrollview.fullScroll(ScrollView.FOCUS_UP)
                if(strFlagUser=="employee") {
                    lytSelectUserType.gone()
                    userDetailsLayout.show()
                    businessDetailsLayout.gone()
                    paymentLayout.gone()
                    var inttt=Intent(context, VerifyTokenActivity::class.java)
                    startActivity(inttt)
                }
                else{
                    lytSelectUserType.gone()
                    userDetailsLayout.gone()
                    businessDetailsLayout.show()
                    paymentLayout.gone()
                    createAccount.text = "NEXT : PAYMENT"
                    txtHeaderTitle.text = "Business"
                }

            } else if (businessDetailsLayout.visibility == View.VISIBLE) {

                if(isInvalidBusinessDetails()) return@setOnClickListener
                lytSelectUserType.gone()
                userDetailsLayout.gone()
                businessDetailsLayout.gone()
                //verifyTokenLayout.gone()
                paymentLayout.show()
                createAccount.text = "SIGNUP"
                txtHeaderTitle.text = "Payment"
            } else if (paymentLayout.visibility == View.VISIBLE) {

                if(isValidOtherDetails())return@setOnClickListener

                if(Utils.isNetworkAvailable(this)){
                    /*FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                showDebugToast("FirebaseToken Failed" + task.exception)
                                return@OnCompleteListener
                            }
                            val token = task.result?.token
                        })*/

                    signupApi()
                }
                else{
                    showToast("No Internet Connectivity!")
                }

            }
        }

        setListener()
        setUpWeekDaysRecyclerView()
    }


    var weekDaysAdapter:WeekDaysAdapter ? = null
    private fun setUpWeekDaysRecyclerView() {
        val weekDaysList = arrayListOf<WeekDay>()
        weekDaysList.add(WeekDay("M", false, "Monday"))
        weekDaysList.add(WeekDay("T", false, "Tuesday"))
        weekDaysList.add(WeekDay("W", false, "Wednesday"))
        weekDaysList.add(WeekDay("T", false, "Thursday"))
        weekDaysList.add(WeekDay("F", false, "Friday"))
        weekDaysList.add(WeekDay("S", false, "Saturday"))
        weekDaysList.add(WeekDay("S", false, "Sunday"))
        weekDaysAdapter = WeekDaysAdapter(context, weekDaysList, "signup")
        weekDaysRecyclerView.adapter = weekDaysAdapter
    }

    fun onDaySelected(isSelected: Boolean, day: String) {
        if (isSelected) selectedWeekDays.add(day) else selectedWeekDays.remove(day)
    }

    private fun setUpBusinessTypeSpinner() {
        val businessTypeList = arrayListOf<String>()
        //businessTypeList.add("Business Type")
        businessTypeList.add("individual")
        businessTypeList.add("company")

        val adapter =
            ArrayAdapter(context!!, R.layout.simple_spinner_item, businessTypeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        businessTypeSpinner.adapter = adapter

        businessTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                businessType = businessTypeList[i]
                isBusinessTypeSelected=true
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }
    }

    fun dismissBottomSheet() {
        if (bottomSheetFragment != null) {
            bottomSheetFragment!!.dismiss()
            bottomSheetFragment = null
        }
    }

    fun setLicenseAdded(driverlicenceInfo: JsonObject) {
        isLicenseSet = true
        driverLicenceInfo = driverlicenceInfo
        addDrivingLicenseEditText.setText("License details added")
        licenseAdded = true
    }

    fun setUserAddress(addressObject: JsonObject) {
        if (isSelectUserAddress) {
            isAddressSet = true
            isSelectUserAddress = false
            userAddressInfo = addressObject
            addressEditText.setText(
                    userAddressInfo.get("addressTitle").asString
            )
        } else {
            isBusinessAddressSet = true
            businessAddressInfo = addressObject
            businessLocationEditText.setText(
                    businessAddressInfo.get("addressTitle").asString
            )
        }
    }

    private fun openPDFAndImageChooser() {
        val mimeTypes = arrayOf("application/pdf")
        //val mimeTypes = arrayOf( "application/pdf")

        if(true){//android 10 file picker code
            // Request code for selecting a PDF document.
//            const val PICK_PDF_FILE = 2

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
//                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

                    // Optionally, specify a URI for the file that should appear in the
                    // system file picker when it loads.
//                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }

                startActivityForResult(intent, Constants.FILE_SELECTION_REQUEST_CODE)
            return
        }
        val intent: Intent =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            } else
                Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.FILE_SELECTION_REQUEST_CODE)
    }

    override fun PickiTonCompleteListener(
            path: String?,
            wasDriveFile: Boolean,
            wasUnknownProvider: Boolean,
            wasSuccessful: Boolean,
            Reason: String?
    ) {
        if (path == null) return
        if (userDetailsLayout.visibility == View.VISIBLE) {

            additionalDocumentLayoutTxtNoFileAdded.text = "File added"
            isAdditionalDocumentSet = true
            additionalDocumentselectedFileData = HelperMethods.getBase64FromFile(context, File(path))
            additionalDocumentFile=File(path)
            LogUtil.info("selectedFile : ", additionalDocumentselectedFileData)
        }
        else if (businessDetailsLayout.visibility == View.VISIBLE) {

            txtNoFileAdded.text = "File added"
            isBusinessDocumentSet = true
            selectedFileData = HelperMethods.getBase64FromFile(context, File(path))
            selectedFileDataFile=File(path)
            LogUtil.info("selectedFile : ", selectedFileData)
        }
    }

    var isBusinessLogoFlow = false

    private fun showImageSelectionPopUp() {
        if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                showMenuOption(imageSelectionView!!)
            } else
                PermissionUtils.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        PermissionUtils.CAMERA_ACCESS
                )
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                PermissionUtils.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PermissionUtils.READ_EXTERNAL_STORAGE
                )
            } else
                PermissionUtils.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PermissionUtils.READ_EXTERNAL_STORAGE
                )
        }
    }

    private fun showMenuOption(view: View) {
        val popupMenu = PopupMenu(this@SignUpActivity, view)
        popupMenu.inflate(R.menu.menu_item)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.camera_selection -> {
                    try {
                        val config = EZPhotoPickConfig()
                        config.photoSource = PhotoSource.CAMERA
                        config.isAllowMultipleSelect = false
                        config.exportingSize = 1000
                        config.exportingThumbSize = 200
                        config.needToExportThumbnail = true
                        config.needToAddToGallery = true
                        config.needToRotateByExif = true
                        config.isGenerateUniqueName = true
                        config.storageDir = "LicenseeApp"
                        EZPhotoPick.startPhotoPickActivity(this@SignUpActivity, config)
                    } catch (e: Exception) {
                    }
                }

                R.id.gallery_selection -> {
                    try {
                        val config = EZPhotoPickConfig()
                        config.photoSource = PhotoSource.GALLERY
                        config.isAllowMultipleSelect = true
                        config.exportingSize = 1000
                        config.exportingThumbSize = 200
                        config.needToExportThumbnail = true
                        config.needToAddToGallery = true
                        config.isAllowMultipleSelect = false
                        config.needToRotateByExif = true
                        config.isGenerateUniqueName = true
                        config.storageDir = "LicenseeApp"
                        EZPhotoPick.startPhotoPickActivity(this@SignUpActivity, config)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            false
        }
        popupMenu.show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
            if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isDocumentPicker) {
                        if(Environment.isExternalStorageManager()) {
                            isDocumentPicker = false
                            openPDFAndImageChooser()
                            return
                        }
                        else{
                            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }

                    if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                        showMenuOption(imageSelectionView!!)
                    } else
                        PermissionUtils.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.CAMERA),
                                PermissionUtils.CAMERA_ACCESS
                        )
                } else
                    showToast("Permission Denied")
            } else if (requestCode == PermissionUtils.CAMERA_ACCESS) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.hasPermissions(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                    ) {
                        showMenuOption(imageSelectionView!!)
                    } else
                        PermissionUtils.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                PermissionUtils.READ_EXTERNAL_STORAGE
                        )
                } else
                    showToast("Permission Denied")
            }
        }
        else {
            if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isDocumentPicker) {
                        isDocumentPicker = false
                        openPDFAndImageChooser()
                        return
                    }

                    if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                        showMenuOption(imageSelectionView!!)
                    } else
                        PermissionUtils.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.CAMERA),
                                PermissionUtils.CAMERA_ACCESS
                        )
                } else
                    showToast("Permission Denied")
            } else if (requestCode == PermissionUtils.CAMERA_ACCESS) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.hasPermissions(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                    ) {
                        showMenuOption(imageSelectionView!!)
                    } else
                        PermissionUtils.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                PermissionUtils.READ_EXTERNAL_STORAGE
                        )
                } else
                    showToast("Permission Denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(this@SignUpActivity)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                    setImageData(pickedPhotoBitmap)
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection cancelled")
        } else if (requestCode == Constants.FILE_SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var selectedFileType: String
                if (data != null) {
                    if (userDetailsLayout.visibility == View.VISIBLE) {
                        data.data?.let {
                            selectedFileType = getFileMimeType(it)!!
                            if (selectedFileType == "png" || selectedFileType == "jpeg" || selectedFileType == "jpg") {
                                additionalDocumentselectedFileData = HelperMethods.getBase64FromBitmap(
                                        MediaStore.Images.Media.getBitmap(
                                                contentResolver,
                                                data.data
                                        )
                                )
                                LogUtil.info("selectedFile : ", additionalDocumentselectedFileData)
                                additionalDocumentLayoutTxtNoFileAdded.text = "File added"
                                isAdditionalDocumentSet = true

                            } else if (selectedFileType == "pdf") {
                                showToast("File Selected")
//                                if(Build.VERSION.SDK_INT <= 29) {
                                pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)//content://com.android.providers.downloads.documents/document/msf%3A893
//                                }
//                                else {
//                                    val res = data.data
//                                    val path = PDFTools.getPDFPath(context, res)
//                                    Log.d("here", path!!)
//                                    if (userDetailsLayout.visibility == View.VISIBLE) {
//
//                                        additionalDocumentLayoutTxtNoFileAdded.text = "File added"
//                                        isAdditionalDocumentSet = true
//                                        additionalDocumentselectedFileData = HelperMethods.getBase64FromFile(File(path))
//                                        additionalDocumentFile = File(path)
//                                        LogUtil.info("selectedFile : ", additionalDocumentselectedFileData)
//                                    } else if (businessDetailsLayout.visibility == View.VISIBLE) {
//
//                                        txtNoFileAdded.text = "File added"
//                                        isBusinessDocumentSet = true
//                                        selectedFileData = HelperMethods.getBase64FromFile(File(path))
//                                        selectedFileDataFile = File(path)
//                                        LogUtil.info("selectedFile : ", selectedFileData)
//                                    }
//                                }
                            }
                        }
                    }
                    else if (businessDetailsLayout.visibility == View.VISIBLE) {
                        data.data?.let {
                            selectedFileType = getFileMimeType(it)!!
                            if (selectedFileType == "png" || selectedFileType == "jpeg" || selectedFileType == "jpg") {
                                selectedFileData = HelperMethods.getBase64FromBitmap(
                                        MediaStore.Images.Media.getBitmap(
                                                contentResolver,
                                                data.data
                                        )
                                )
                                LogUtil.info("selectedFile : ", selectedFileData)
                                txtNoFileAdded.text = "File added"
                                isBusinessDocumentSet = true

                            } else if (selectedFileType == "pdf") {
                                showToast("File Selected")
                                pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)//content://com.android.providers.downloads.documents/document/msf%3A893
                            }
                        }
                    }

                }

            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Cancelled")
        } else if (requestCode == Constants.LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                locationBottomSheet.onActivityResult(requestCode, resultCode, data)
            } else
                showToast("Location Permission Cancelled")
        } else if (requestCode == IntentParams.REQUEST_SELECT_PLACE) {
            locationBottomSheet.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setImageData(imageBitmap: Bitmap) {
        if(isBusinessLogoFlow){
            isBusinessLOGOSet = true
            businessprofilePicData = HelperMethods.getBase64FromBitmap(imageBitmap)
            businessprofilePicBitmap=imageBitmap
        }else {
            profilePicData = HelperMethods.getBase64FromBitmap(imageBitmap)
            profilePicBitmap = imageBitmap
        }

        imageSelectionView?.setImageBitmap(imageBitmap)
        if (userDetailsLayout.visibility == View.VISIBLE) {
            ringView.gone()
            icon.gone()
        } else {
            ringView1.gone()
            icon1.gone()
        }
        showToast("Image Picked")
    }

    private fun getFileMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
    }

    override fun onBackPressed() {
        when {

            lytSelectUserType.visibility == View.VISIBLE -> {
                SignUpBottomSheetFragment.photoSelectedBitmap=null
                super.onBackPressed()
            }
            userDetailsLayout.visibility == View.VISIBLE -> {

                paymentLayout.gone()
                businessDetailsLayout.gone()
                userDetailsLayout.gone()
                lytSelectUserType.show()
               lytSignupButton.gone()
                txtHeaderTitle.text = "Welcome"
            }
            businessDetailsLayout.visibility == View.VISIBLE -> {
                paymentLayout.gone()
                businessDetailsLayout.gone()
                userDetailsLayout.show()
                lytSignupButton.show()
                if(strFlagUser=="employee"){
                    createAccount.text = "TOKEN VERIFICATION"
                    txtHeaderTitle.text = "Employee"
                }
                else{
                    createAccount.text = "NEXT : PAYMENT"
                    txtHeaderTitle.text = "Business"
                }
            }
            paymentLayout.visibility == View.VISIBLE -> {
                paymentLayout.gone()
                userDetailsLayout.gone()
                businessDetailsLayout.show()
                lytSelectUserType.gone()
                lytSignupButton.show()
                createAccount.text = "NEXT : PAYMENT"
                txtHeaderTitle.text = "Business"
            }
        }
    }

    var partallInfo: MultipartBody.Part? = null
    var partProfilePic: MultipartBody.Part? = null
    var partAdditionalDocument: MultipartBody.Part? = null
    var partLicensePic: MultipartBody.Part? = null
    var partIncorporation: MultipartBody.Part? = null
    var partLicenseeLogo: MultipartBody.Part? = null
    val responseMap= mutableMapOf<String, ArrayList<String>>() as HashMap
    private fun signupApi() {
        showProgressDialog()

        val requestJsonObject = JsonObject()

        val tempDriverLicenseInfo=driverLicenceInfo
        tempDriverLicenseInfo.remove("employeeDriverLicenseScan")
        employeeObj.add("driverLicense", tempDriverLicenseInfo)

        requestJsonObject.add("employee", employeeObj)
        requestJsonObject.add("licensee", licenseeObj)

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), requestJsonObject.toString())


        if (profilePicData != null) {
            val file = Utils.convertBitmapToFile("temp.png", profilePicBitmap, context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partProfilePic =
                requestFile?.let {
                    MultipartBody.Part.createFormData("employeePhoto", file.name,
                            it
                    )
                }
        }
        if (SignUpBottomSheetFragment.photoSelectedBitmap!= null) {
            val file=Utils.convertBitmapToFile("temp.png",
                    SignUpBottomSheetFragment.photoSelectedBitmap!!, context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partLicensePic = requestFile?.let {
                MultipartBody.Part.createFormData("employeeDriverLicenseScan", file.name,
                        it
                )
            }
        }
        if ( businessprofilePicData!= null) {
            val file = Utils.convertBitmapToFile("temp.png", businessprofilePicBitmap, context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partLicenseeLogo =
                requestFile?.let {
                    MultipartBody.Part.createFormData("licenseeLogo", file.name,
                            it
                    )
                }
        }


        if (additionalDocumentselectedFileData != null) {
            val file = additionalDocumentFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("application/pdf".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
                partAdditionalDocument = requestFile?.let {
                    MultipartBody.Part.createFormData("employeeAdditionalDocumentScan",
                            file.name, it
                    )
                }

        }
        if ( selectedFileData!= null) {
            val file=selectedFileDataFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.asRequestBody("application/pdf".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            partIncorporation = requestFile?.let {
                MultipartBody.Part.createFormData("licenseeProofOfIncorporation",
                        file.name, it
                )
            }
        }

        val signupCall: Call<ResponseBody> = webService.signUp(requestBodyAllInfo, partProfilePic, partAdditionalDocument, partLicensePic, partIncorporation, partLicenseeLogo)

        signupCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
            ) {
                if (response.isSuccessful()) {

                    var jsonObject: JSONObject? = null
                    //val gson = Gson()

                    try {
                        jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getBoolean("success")) {

                            SignUpBottomSheetFragment.photoSelectedBitmap = null
                            /* val employeeObject=jsonObject.getJSONObject("employeeObj")
                            //val licenseeObj=`object`.getJSONObject("licenseeObj")
                            HelperMethods.setUserId(employeeObject.getString("_id"))
                            HelperMethods.setAuthorizationToken(jsonObject.getString("token"))
                            HelperMethods.setIsOwner(employeeObject.getBoolean("isOwner"))
                            HelperMethods.setUserLatitude(context,employeeObject.getJSONObject("address").getJSONObject("location").getJSONArray("coordinates").get(0).toString())
                            HelperMethods.setUserLongitude(context,employeeObject.getJSONObject("address").getJSONObject("location").getJSONArray("coordinates").get(1).toString())

                            val accessControlList =
                                `employeeObject`.getJSONObject("acl")

                            val keys: Iterator<*> = accessControlList.keys()
                            while (keys.hasNext()) {
                                // loop to get the dynamic key
                                val currentDynamicKey = keys.next() as String
                                // get the value of the dynamic key
                                val currentDynamicValue: JSONArray =
                                    accessControlList.getJSONArray(
                                        currentDynamicKey
                                    )
                                // do something here with the value...
                                val jsonArrayACL = currentDynamicValue
                                val appointmentsSize = jsonArrayACL.length()
                                var tempArr = ArrayList<String>()
                                for (count in 0 until appointmentsSize) {
                                    val objectData = jsonArrayACL[count]
                                    tempArr.add(objectData.toString())
                                }
                                responseMap.put(currentDynamicKey, tempArr)
                            }

                            HelperMethods.setACL(responseMap)


                            HelperMethods.setIsUserLogin(true)*/

                            showToast("Account created Successfully!")
                            if (strFlagUser == "employee") {
                                var inttt = Intent(context, VerifyOTPActivity::class.java)
                                inttt.putExtra(
                                        IntentParams.MOBILE_NUMBER,
                                        employeeObj.get("mobile").asString
                                )
                                inttt.putExtra(IntentParams.EMAIL, employeeObj.get("email").asString)
                                inttt.putExtra("user", "employee")
                                inttt.putExtra(IntentParams.IS_SIGNUP_FLOW, false)
                                startActivity(inttt)
                                finish()
                            } else {
                                var inttt = Intent(context, VerifyOTPActivity::class.java)
                                inttt.putExtra(
                                        IntentParams.MOBILE_NUMBER,
                                        licenseeObj.get("mobile").asString
                                )
                                inttt.putExtra(IntentParams.EMAIL, licenseeObj.get("email").asString)
                                inttt.putExtra("user", "licensee")
                                inttt.putExtra(IntentParams.IS_SIGNUP_FLOW, false)
                                startActivity(inttt)
                                finish()
                            }
                            // HelperMethods.closeEverythingOpenLogin(this@SignUpActivity)
                        } else {
                            showToast("Something went wrong ...")
                            //return
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
                dismissProgressDialog()
            }

            override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
    }


    override fun onLocationAdded(locationString: String) {
    }

    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonStartListener() {
    }

    /*Validate Business Details*/
    private fun isInvalidBusinessDetails(): Boolean {
        if (!isBusinessLOGOSet) {
            AnimUtils.shake(businessProfileImageView)
            showToast("Please upload business logo");
            signupScrollview.fullScroll(ScrollView.FOCUS_UP);
            return true
        }
        if (!isBusinessTypeSelected) {
            showToast("Please select business type");
            return true
        }

        if (businessNameEditText.text.isEmpty()) {
            businessNameEditText.requestFocus()
            showToast("Enter Business name ");
            return true
        }
        if (taxIdEditText.text.isEmpty()||taxIdEditText.text.toString().length <9) {
            taxIdEditText.requestFocus()
            showToast("Enter valid business tax ID ");
            return true
        }
        if (businessNumberEditText.text.isEmpty()) {
            openKeyBoard(businessNumberEditText)
            businessNumberEditText.requestFocus()
            showToast("Enter Mobile Number");
            return true
        }
        if (businessNumberEditText.text.toString().length <7) {
            openKeyBoard(businessNumberEditText)
            businessNumberEditText.requestFocus()
            showToast("Enter valid Mobile Number");
            return true
        }


        if (!isBusinessAddressSet) {
            showToast("Please add business address details")
            return true
        }

        if (!isBusinessDocumentSet) {
            AnimUtils.shake(txtNoFileAdded)
            showToast("Please upload business document");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }


        licenseeObj.addProperty("name", businessNameEditText.text.toString())
        licenseeObj.addProperty("email", businessEmailEditText.text.toString())
        /*licenseeObj.addProperty("mobile", if(businessNumberEditText.text.toString().trim().contains("+91"))
            businessNumberEditText.text.toString().trim()
        else
            "+91"+businessNumberEditText.text.toString().trim())*/
        licenseeObj.addProperty("mobile", businessNumberEditText.text.toString().trim())
        licenseeObj.addProperty("taxId", taxIdEditText.text.toString())
        licenseeObj.addProperty("country", businessAddressInfo.get("country").asString)
        //licenseeObj.addProperty("licenseeProofOfIncorporation", selectedFileData)
        //licenseeObj.addProperty("licenseeLogo", businessprofilePicData)
        licenseeObj.addProperty("businessType", businessType)


        var tempbusinessAddressInfo = JsonObject()
        tempbusinessAddressInfo.addProperty("text", businessAddressInfo.get("text").asString)
        tempbusinessAddressInfo.addProperty("pincode", businessAddressInfo.get("pincode").asString)
        tempbusinessAddressInfo.addProperty("city", businessAddressInfo.get("city").asString)
        tempbusinessAddressInfo.addProperty("state", businessAddressInfo.get("state").asString)
        tempbusinessAddressInfo.addProperty("country", businessAddressInfo.get("country").asString)
        tempbusinessAddressInfo.add("coordinates", businessAddressInfo.getAsJsonArray("coordinates"))

        licenseeObj.add("address", tempbusinessAddressInfo)



        return false
    }

    /*Validate user Details*/
    private fun isInvalidUserDetails(): Boolean {

        if (profilePicData.isEmpty()) {
            AnimUtils.shake(userProfileImageView)
            signupScrollview.fullScroll(ScrollView.FOCUS_UP);
            showToast("Please add profile pic")
            return true
        }

        if (fullNameEditText.text.toString().isEmpty()) {
            openKeyBoard(fullNameEditText)
            fullNameEditText.requestFocus()
            showToast("Enter Name as per Driving License");
            return true
        }
        if (titleEditText.text.toString().isEmpty()) {
            openKeyBoard(titleEditText)
            titleEditText.requestFocus()
            showToast("Enter title");
            return true
        }
        if (mobileEditText.text.isEmpty()) {
            openKeyBoard(mobileEditText)
            mobileEditText.requestFocus()
            showToast("Enter Mobile Number");
            return true
        }
        if (mobileEditText.text.toString().length <7) {
            openKeyBoard(mobileEditText)
            mobileEditText.requestFocus()
            showToast("Enter valid Mobile Number");
            return true
        }

        if (!HelperMethods.isEmailValid(emailAddressEditText.text.toString())) {
            openKeyBoard(emailAddressEditText)
            emailAddressEditText.requestFocus()
            showToast("Enter valid email");
            return true
        }
        if (userDOB.text.toString().isEmpty()) {
            showToast("Enter Date of Birth");
            return true
        }
        val df = SimpleDateFormat("yyyy-MM-dd")
        val birthdate = df.parse(userDOB.text.toString())
        if (HelperMethods.calculateAge(birthdate)<17) {
            showToast("Your age must be above 16 years")
            return true
        }
        val password = passwordEditText.text.toString()
        if (password.length < 8) {
            passwordEditText.requestFocus()
            showToast("Password must be of length 8 or greater")
            return true//8 characters at least, 1 capital, 1 symbol, 1 number
        }

        if (!isValidPassword(password.trim())) {
            passwordEditText.requestFocus()
            openKeyBoard(passwordEditText)
            showToast("Password must have at least 1 capital, 1 symbol, 1 number");
            return true
        }

        if (!password.contentEquals(confirmPasswordEditText.text.toString())) {
            openKeyBoard(confirmPasswordEditText)
            confirmPasswordEditText.requestFocus()
            showToast("Confirm passwords should match")
            return true
        }

        if (!isAddressSet) {
            showToast("Please add user address details")
            return true
        }
        if (!isLicenseSet) {
            showToast("Please add Driving license details")
            return true
        }
        if (!isAdditionalDocumentSet) {
            AnimUtils.shake(additionalDocumentLayoutTxtNoFileAdded)
            showToast("Please upload additional document");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }


        employeeObj.addProperty("country", userAddressInfo.get("country").asString)
        employeeObj.addProperty("name", fullNameEditText.text.toString().trim())
       /* employeeObj.addProperty("mobile", if(mobileEditText.text.toString().trim().contains("+91"))
            mobileEditText.text.toString().trim()
        else
            "+91"+mobileEditText.text.toString().trim())*/
        employeeObj.addProperty("mobile", mobileEditText.text.toString().trim())
        employeeObj.addProperty("email", emailAddressEditText.text.toString())
        employeeObj.addProperty("password", passwordEditText.text.toString())
        employeeObj.addProperty("title", titleEditText.text.toString())

        employeeObj.addProperty("dob", userDOB.text.toString())
        // employeeObj.addProperty("employeePhoto", profilePicData)
        // employeeObj.add("driverLicense",driverLicenceInfo)



        var tempUserAddressInfo = JsonObject()
        tempUserAddressInfo.addProperty("text", userAddressInfo.get("text").asString)
        tempUserAddressInfo.addProperty("pincode", userAddressInfo.get("pincode").asString)
        tempUserAddressInfo.addProperty("city", userAddressInfo.get("city").asString)
        tempUserAddressInfo.addProperty("state", userAddressInfo.get("state").asString)
        tempUserAddressInfo.addProperty("country", userAddressInfo.get("country").asString)
        tempUserAddressInfo.add("coordinates", userAddressInfo.getAsJsonArray("coordinates"))

        employeeObj.add("address", tempUserAddressInfo)
        //employeeObj.addProperty("employeeAdditionalDocumentScan", additionalDocumentselectedFileData)


        return false
    }
    private fun isValidOtherDetails(): Boolean {

        if(weekDaysAdapter!!.isNothingSelected()){
            AnimUtils.shake(weekDaysRecyclerView)
            showToast("Please select working days");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }

        if(!isBusinessTimeFromSet){
            AnimUtils.shake(operationFrom)
            showToast("Please select business start time");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }
        if(!isBusinessTimeEndSet){
            AnimUtils.shake(operationTo)
            showToast("Please select business end time");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }
        if(!HelperMethods.checktimings(workingStartTime, workingEndTime)){
            AnimUtils.shake(operationTo)
            showToast("End time should be after start time");
            signupScrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true
        }
        if (bsbNumberEditText.text.toString().isEmpty()) {
            bsbNumberEditText.requestFocus()
            showToast("Please enter BSB Number of 6 digits")
            return true
        }
        if (accountNumberEditText.text.toString().isEmpty()) {
            accountNumberEditText.requestFocus()
            showToast("Please enter valid Account Number of 4 digits")
            return true
        }
        if (urlEditText.text.toString().isEmpty()) {
            showToast("Please enter company website URL");
            return true
        }
        /*if (mccEditText.text.toString().isEmpty()) {
            showToast("Please enter MCC");
            return true
        }

        if (productDescriptionEditText.text.toString().isEmpty()) {
            showToast("Please enter product description");
            return true
        }*/

        licenseeObj.add("workingDays", weekDaysAdapter!!.getSelectedWeekdays())
        var gmtStartTime=HelperMethods.formatDateTimeToString(workingStartTime, "HH:mm", "GMT")
        var gmtEndTime=HelperMethods.formatDateTimeToString(workingEndTime, "HH:mm", "GMT")
        licenseeObj.addProperty("workingHours", gmtStartTime + "-" + gmtEndTime)
        licenseeObj.addProperty("bsbNumber", bsbNumberEditText.text.toString().trim())
        licenseeObj.addProperty("accountNumber", accountNumberEditText.text.toString().trim())
        //licenseeObj.addProperty("mcc", mccEditText.text.toString().trim())
        //licenseeObj.addProperty("productDescription",productDescriptionEditText.text.toString().trim())
        licenseeObj.addProperty("url", urlEditText.text.toString().trim())
        return false
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)-16
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val dpdd = DatePickerDialog(
                this, this,
                year,
                day,
                month
        )

        val dpd = dpdd.datePicker
        dpd.maxDate = System.currentTimeMillis() - 1000
        dpdd.show()

        dpdd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorAccent))
        dpdd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorAccent))
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var date = StringBuilder()
        val strDay: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val strMonth: String =
            if (month < 10) "0" + (month + 1).toString() else (month + 1).toString()
        date.append(year).append("-").append(strMonth).append("-").append(strDay)
        DOB = date.toString()
        val inputPattern = SimpleDateFormat("MM yyyy")
        val outputPattern = SimpleDateFormat("yyyy-MM-dd")
        date = StringBuilder()
        try {
            date.append(outputPattern.format(inputPattern.parse("$strMonth $year")))
        } catch (e23: Throwable) {
        }
        userDOB?.text = date
    }


    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val minute: Int = cal.get(Calendar.MINUTE)
        var timePickerDialog = TimePickerDialog(context, this, hour, minute, true)
        timePickerDialog.setOnShowListener {
            val positiveColor: Int = ResourcesUtil.getColor(R.color.light_orange)
            val negativeColor: Int = ResourcesUtil.getColor(R.color.light_orange)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        //val selectedTime = "$hourOfDay:$minute"
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        currentTime[Calendar.HOUR_OF_DAY] = hourOfDay
        currentTime[Calendar.MINUTE] = minute
        currentTime.clear(Calendar.SECOND)
        val selectedTime = timeFormat.format(currentTime.timeInMillis)

        if (isStartTimePicker == 1) {
            isStartTimePicker = -1
            workingStartTime = selectedTime
            operationFrom.text = selectedTime
            isBusinessTimeFromSet = true
        } else if (isStartTimePicker == 0) {
            isStartTimePicker = -1
            workingEndTime = selectedTime
            operationTo.text = selectedTime
            isBusinessTimeEndSet = true
        }
    }

    private fun setListener() {
        fullNameInfo.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("Name must be as per Driving License")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        infoAdditionalDocument.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("i.e.Passport, ID card ( Front & Back ), Photo card")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        titleInfo.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("i.e. Title in Company")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        bLocationInfo.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("Business Address as per Incorporation Document")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }

        bNameInfo.setOnClickListener {
            it.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("Business Name as per Incorporation Document")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }

        infoPassword.setOnClickListener {
            infoPassword.panWithCallback(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    showToast("Password must have at least 1 capital, 1 symbol, 1 number and should be atleast 8 characters");
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        userDOBInfoLayout.setOnClickListener{
            showDatePicker()
        }
        incorporationDoc.setOnClickListener {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openPDFAndImageChooser()
            } else {
                isDocumentPicker = true
                PermissionUtils.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PermissionUtils.READ_EXTERNAL_STORAGE
                )
            }
        }
        additionalDocumentText.setOnClickListener {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openPDFAndImageChooser()
            } else {
                isDocumentPicker = true
                PermissionUtils.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PermissionUtils.READ_EXTERNAL_STORAGE
                )
            }
        }
        userProfileImageView.setOnClickListener {
            imageSelectionView = it as ImageView?
            isBusinessLogoFlow = false
            showImageSelectionPopUp()
        }

        businessProfileImageView.setOnClickListener {
            imageSelectionView = it as ImageView?
            isBusinessLogoFlow=true
            showImageSelectionPopUp()
        }

        addressEditText.setOnClickListener {

            if(Utils.isNetworkAvailable(context)){
                if (locationBottomSheet.isAdded) return@setOnClickListener
                isSelectUserAddress = true
                locationBottomSheet.isCancelable = true
                locationBottomSheet.show(supportFragmentManager, locationBottomSheet.tag)
            }
            else{
                showToast("No Internet Connectivity!")
            }
        }

        businessLocationEditText.setOnClickListener {
            if(Utils.isNetworkAvailable(context)){
                if (locationBottomSheet.isAdded) return@setOnClickListener
                isSelectUserAddress = false
                locationBottomSheet.isCancelable = true
                locationBottomSheet.show(supportFragmentManager, locationBottomSheet.tag)
            }
            else{
                showToast("No Internet Connectivity!")
            }

        }

        addDrivingLicenseEditText.setOnClickListener {
            if (bottomSheetFragment == null) bottomSheetFragment = SignUpBottomSheetFragment("signup")
            if (bottomSheetFragment?.isAdded!!) return@setOnClickListener
            bottomSheetFragment!!.isCancelable = true
            bottomSheetFragment!!.show(supportFragmentManager, "")
        }

        operationFrom.setOnClickListener {
            isStartTimePicker = 1
            openTimePicker()
        }

        operationTo.setOnClickListener {
            isStartTimePicker = 0
            openTimePicker()
        }
    }
}

