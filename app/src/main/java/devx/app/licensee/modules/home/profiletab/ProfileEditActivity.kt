package devx.app.licensee.modules.home.profiletab

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import devx.app.seller.webapi.response.CommonResponse
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.view.View
import android.view.animation.Animation
import android.webkit.MimeTypeMap
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.androidquery.callback.BitmapAjaxCallback
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.panWithCallback
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.*
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.*
import devx.app.licensee.modules.PdfViewerActivity
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.modules.signup.*
import devx.app.licensee.webapi.*
import devx.app.seller.modules.signup.SignUpActivity
import devx.app.seller.webapi.response.EmployeeObj
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.activity_profile_edit.accountNumberEditText
import kotlinx.android.synthetic.main.activity_profile_edit.addDrivingLicenseEditText
import kotlinx.android.synthetic.main.activity_profile_edit.additionalDocumentLayoutTxtNoFileAdded
import kotlinx.android.synthetic.main.activity_profile_edit.additionalDocumentText
import kotlinx.android.synthetic.main.activity_profile_edit.bsbNumberEditText
import kotlinx.android.synthetic.main.activity_profile_edit.businessDetailsLayout
import kotlinx.android.synthetic.main.activity_profile_edit.businessEmailEditText
import kotlinx.android.synthetic.main.activity_profile_edit.businessLocationEditText
import kotlinx.android.synthetic.main.activity_profile_edit.businessNameEditText
import kotlinx.android.synthetic.main.activity_profile_edit.businessNumberEditText
import kotlinx.android.synthetic.main.activity_profile_edit.businessProfileImageView
import kotlinx.android.synthetic.main.activity_profile_edit.fullNameInfo
import kotlinx.android.synthetic.main.activity_profile_edit.incorporationDoc
import kotlinx.android.synthetic.main.activity_profile_edit.operationFrom
import kotlinx.android.synthetic.main.activity_profile_edit.operationTo
import kotlinx.android.synthetic.main.activity_profile_edit.titleEditText
import kotlinx.android.synthetic.main.activity_profile_edit.titleInfo
import kotlinx.android.synthetic.main.activity_profile_edit.txtNoFileAdded
import kotlinx.android.synthetic.main.activity_profile_edit.userDOB
import kotlinx.android.synthetic.main.activity_profile_edit.userDetailsLayout
import kotlinx.android.synthetic.main.activity_profile_edit.userProfileImageView
import kotlinx.android.synthetic.main.activity_profile_edit.weekDaysRecyclerView
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileEditActivity : BaseActivity(), DatePickerDialog.OnDateSetListener, PickiTCallbacks,
    AddLocationListener, TimePickerDialog.OnTimeSetListener {

    companion object{
        var isSelectUserAddress = false
        var driverLicenceInfo = JsonObject()
    }
    private var emailAddress = "NA"
    private var name = "NA"
    private var address = "NA"
    private var DOB = "NA"
    private var mobileNumber = "NA"
    private var driverLicenseImage = "NA"
    private var bottomSheetFragment: SignUpBottomSheetFragment? = null

    private var isLicenseSet = false

    private var selectedFileData = ""
    private var selectedFileDataFile:File? = null
    private var isBusinessDocumentSet= false
    //additional document parameter
    private var isAdditionalDocumentSet= false
    private var additionalDocumentselectedFileData=""
    private var additionalDocumentFile:File?=null
    private var isDocumentPicker = false

    private var pickiT: PickiT? = null
    private var imageSelectionView: ImageView? = null

    private var profilePicData = ""
    private var businessprofilePicData = ""
    private var isBusinessLOGOSet= false
    private lateinit var profilePicBitmap :Bitmap
    private lateinit var businessprofilePicBitmap :Bitmap
    private lateinit var resultBitmpa :Bitmap

    var businessAddressInfo = JsonObject()
    var userAddressInfo = JsonObject()
    private var isAddressSet = false
    private var isBusinessAddressSet = false
    private var locationBottomSheet = LocationBottomSheetFragment("profile")
    private var isSelectUserAddress = false

    /*intent*/
    var tempObj: EmployeeObj? =null
    var flag:String=""

    private var workingStartTime = ""
    private var workingEndTime = ""
    private var isStartTimePicker = -1


    private var isBusinessTimeFromSet = false
    private var isBusinessTimeEndSet = false

    var urlIncorporationDoc=""
    var urlAdditonalDoc=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        flag= intent.extras?.getString("flag").toString()

        if(flag=="userInfo"){
            userDetailsLayout.show()
            businessDetailsLayout.gone()
            txtChangePwd.show()
        }
        else{
            userDetailsLayout.gone()
            businessDetailsLayout.show()
            txtChangePwd.gone()
        }
        pickiT = PickiT(this, this)
        HelperMethods.disableEditText(addDrivingLicenseEditText)
        //setUserInfo()
        commonBack.setOnClickListener {
            SignUpBottomSheetFragment.photoSelectedBitmap=null
            onBackPressed()
        }

        txtChangePwd.setOnClickListener {
            startActivity(Intent(context,ChangePasswordActivity::class.java))
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
        txtVerifyEmail.setOnClickListener{
            if(flag=="userInfo") {
                setEmailVerfication("employee",userEmailAddress.text.toString())
            }
            else{
                setEmailVerfication("licensee",businessEmailEditText.text.toString())
            }
        }
        txtVerifyMobile.setOnClickListener{
            if(flag=="userInfo") {
                var inttt = Intent(context, VerifyOTPActivity::class.java)
                inttt.putExtra(
                    "mobileNumber",
                    userMobileNumber.text.toString()
                )
                inttt.putExtra("EMAIL", userEmailAddress.text.toString())
                inttt.putExtra("user", "employee")
                startActivity(inttt)
            }
            else{
                var inttt = Intent(context, VerifyOTPActivity::class.java)
                inttt.putExtra(
                    "mobileNumber",
                    businessNumberEditText.text.toString()
                )
                inttt.putExtra("EMAIL", businessEmailEditText.text.toString())
                inttt.putExtra("user", "licensee")
                startActivity(inttt)
            }
        }

        saveProfileInfo.setOnClickListener {

            if(flag=="userInfo"){
                if(Utils.isNetworkAvailable(context)){
                    if(validateUserFields()) {
                        updateProfileDetails()
                    }
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            }
            else{
                val userBitmap: Bitmap = (businessProfileImageView.getDrawable() as BitmapDrawable).getBitmap()
                if(userBitmap!=null) {
                    businessprofilePicBitmap = userBitmap
                    profilePicData = HelperMethods.getBase64FromBitmap(userBitmap)
                    isBusinessLOGOSet = true
                    businessprofilePicData = HelperMethods.getBase64FromBitmap(userBitmap)
                }

                if (!isBusinessLOGOSet) {
                    AnimUtils.shake(businessProfileImageView)
                    showToast("Please upload business logo");
                }

                else if (businessNameEditText.text.isEmpty()) {
                    businessNameEditText.requestFocus()
                    showToast("Enter Business name ");
                }
                else if (!isBusinessAddressSet) {
                    showToast("Please add business address details")
                }
               else if(weekDaysAdapter!!.isNothingSelected()){
                    AnimUtils.shake(weekDaysRecyclerView)
                    showToast("Please select working days");
                }
                else if(!isBusinessTimeFromSet){
                    AnimUtils.shake(operationFrom)
                    showToast("Please select business start time");
                }
               else if(!isBusinessTimeEndSet){
                    AnimUtils.shake(operationTo)
                    showToast("Please select business end time");
                }
                else if(!HelperMethods.checktimings(workingStartTime,workingEndTime)){
                    AnimUtils.shake(operationTo)
                    showToast("End time should be after start time");
                }
                else{
                    if(Utils.isNetworkAvailable(context)){
                        updateLicenseeProfileDetails()
                    }
                    else{
                        showToast("No Internet Connectivity!")
                    }

                }
            }

        }

        operationFrom.setOnClickListener {
            isStartTimePicker = 1
            openTimePicker()
        }

        operationTo.setOnClickListener {
            isStartTimePicker = 0
            openTimePicker()
        }
        /*drivingLicenseImageView.setOnClickListener {
            showMenuOption(drivingLicenseImageView)
        }*/

        userDOB.setOnClickListener {
            showDatePicker()
        }
        showincorporationDoc.setOnClickListener{
            /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlIncorporationDoc))
            startActivity(browserIntent)*/
            PDFTools.openPDFThroughGoogleDrive(this,urlIncorporationDoc)
        }
        lytAdditionalProof.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAdditonalDoc))
            startActivity(browserIntent)
        }
        addDrivingLicenseEditText.setOnClickListener {
            if (bottomSheetFragment == null) bottomSheetFragment = SignUpBottomSheetFragment("profile")
            if (bottomSheetFragment?.isAdded!!) return@setOnClickListener
            bottomSheetFragment!!.isCancelable = true
            bottomSheetFragment!!.show(supportFragmentManager, "")
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
        userAddress.setOnClickListener {
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

        if(Utils.isNetworkAvailable(context)){
            if(intent.hasExtra("id")){
                saveProfileInfo.gone()
                txtChangePwd.gone()
                lytVerfication.gone()
                drivingLabel.gone()
                addDrivingLicenseEditText.gone()
                HelperMethods.disableEditText(userEmailAddress)
                HelperMethods.disableEditText(userMobileNumber)
                HelperMethods.disableEditText(userName)
                userDOB.isClickable=false
                userAddress.isClickable=false
                userProfileImageView.isClickable=false

                HelperMethods.downloadImage(intent.getStringExtra("photo"),this,userProfileImageView)
                userEmailAddress.text=intent.getStringExtra("email").toString().toEditable()
                userMobileNumber.text=intent.getStringExtra("mobile").toString().toEditable()
                userName.text=intent.getStringExtra("name").toString().toEditable()
                userAddress.text=intent.getStringExtra("address")
                userDOB.text=intent.getStringExtra("dob")
                textView.text="User Profile"
            }
            else{
                getProfile()
            }
        }
        else{
            showToast("No Internet Connectivity!")
            finish()
        }

    }


    /*validate user details*/
    private fun validateUserFields(): Boolean {
        val df = SimpleDateFormat("yyyy-MM-dd")


        val userBitmap: Bitmap = (userProfileImageView.getDrawable() as BitmapDrawable).getBitmap()
        profilePicData = HelperMethods.getBase64FromBitmap(userBitmap)

        if (userProfileImageView.getDrawable()==null) {
            AnimUtils.shake(userProfileImageView)
            showToast("Please add profile pic")
            return false
        }
         if (userName.text.toString().isEmpty()) {
            openKeyBoard(userName)
            userName.requestFocus()
            showToast("Enter name as per Driving License")
             return false
         }
         if (titleEditText.text.toString().isEmpty()) {
            openKeyBoard(titleEditText)
            titleEditText.requestFocus()
            showToast("Enter title")
             return false
         }

         if (!isAddressSet) {
            showToast("Please add user address details")
             return false
         }

         if (userMobileNumber.text.isEmpty()) {
            openKeyBoard(userMobileNumber)
            userMobileNumber.requestFocus()
            showToast("Enter Mobile Number")
             return false
         }
         if (userMobileNumber.text.toString().length <7) {
            openKeyBoard(userMobileNumber)
            userMobileNumber.requestFocus()
            showToast("Enter valid Mobile Number")
             return false
        }

         if (!HelperMethods.isEmailValid(userEmailAddress.text.toString())) {
            openKeyBoard(userEmailAddress)
            userEmailAddress.requestFocus()
            showToast("Enter valid email");
             return false
        }
        if (userDOB.text.toString().isEmpty()) {
            showToast("Enter Date of Birth");
              return false
        }
         if (HelperMethods.calculateAge(df.parse(userDOB.text.toString()))<16) {
            showToast("Your age must be above 16 years")
             return false
        }
        return true
    }

    private fun setEmailVerfication(user: String, email: String) {
        var jsonObject=JsonObject();
        jsonObject.addProperty("user",user);
        jsonObject.addProperty("email",email);
        val sendEmailVerifyLinkCall: Call<CommonResponse> = webService.sendEmailVerifyLink(jsonObject)

        sendEmailVerifyLinkCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast("Email verification link send successfully!")

                    } else {
                        showToast("Something went wrong ...")
                        //return
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
                dismissProgressDialog()
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
    }

    fun onFileDownloaded(fileName: String, filePath: String) {
        showToast("File Downloaded Successfully")
        val pdfViewerIntent = Intent(context, PdfViewerActivity::class.java)
        pdfViewerIntent.putExtra(IntentParams.FILE_PATH, filePath)
        pdfViewerIntent.putExtra(IntentParams.FILE_NAME, fileName)
        startActivity(pdfViewerIntent)
    }

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    var partLicenseeLogo: MultipartBody.Part? = null
    var partProfilePic: MultipartBody.Part? = null
    var partLicensePic: MultipartBody.Part? = null
    var partAdditionalDocument: MultipartBody.Part? = null
    var partIncorporation: MultipartBody.Part? = null
    private fun updateProfileDetails() {

        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", userName.text.toString())
        jsonObject.addProperty("title", titleEditText.text.toString())
        jsonObject.addProperty("dob", userDOB.text.toString())
        if(userEmail!=userEmailAddress.text.toString()) {
            jsonObject.addProperty("email", userEmailAddress.text.toString())
        }
        if(userMobile!=userMobileNumber.text.toString()) {
            jsonObject.addProperty("mobile", userMobileNumber.text.toString())
        }

        var tempUserAddressInfo = JsonObject()
        tempUserAddressInfo.addProperty("text",userAddressInfo.get("text").asString)
        tempUserAddressInfo.addProperty("pincode",userAddressInfo.get("pincode").asString)
        tempUserAddressInfo.addProperty("city",userAddressInfo.get("city").asString)
        tempUserAddressInfo.addProperty("state",userAddressInfo.get("state").asString)
        tempUserAddressInfo.addProperty("country",userAddressInfo.get("country").asString)
        tempUserAddressInfo.add("coordinates", userAddressInfo.getAsJsonArray("coordinates"))

        jsonObject.add("address", tempUserAddressInfo)

        if(isLicenseSet) {
            val tempDriverLicenseInfo = driverLicenceInfo
            tempDriverLicenseInfo.remove("employeeDriverLicenseScan")
            jsonObject.add("driverLicense", tempDriverLicenseInfo)
        }
        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),jsonObject.toString())

        //convert image drawable to bitmap to send
        val userBitmap: Bitmap = (userProfileImageView.getDrawable() as BitmapDrawable).getBitmap()
        profilePicData = HelperMethods.getBase64FromBitmap(userBitmap)

        if (profilePicData != null) {
            val file = Utils.convertBitmapToFile("temp.png",userBitmap,context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partProfilePic =
                requestFile?.let {
                    MultipartBody.Part.createFormData("employeePhoto", file?.name,
                        it
                    )
                }
        }

        if (SignUpBottomSheetFragment.photoSelectedBitmap != null) {
            val file = Utils.convertBitmapToFile(
                "temp.png",
                SignUpBottomSheetFragment.photoSelectedBitmap!!,
                context
            )
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partLicensePic = requestFile?.let {
                MultipartBody.Part.createFormData(
                    "employeeDriverLicenseScan",
                    file.name,
                    it
                )
            }
        }
        if (additionalDocumentselectedFileData != "") {
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

        var updateEmployeeProfileCall: Call<CommonResponse>
        if(SignUpBottomSheetFragment.photoSelectedBitmap!=null) {
            if (additionalDocumentselectedFileData=="") {
                updateEmployeeProfileCall = webService.updateEmployeeWithoutDocument(
                    requestBodyAllInfo,
                    partProfilePic,
                    partLicensePic
                )
            }
            else {
                updateEmployeeProfileCall = webService.updateEmployeeProfile(
                    requestBodyAllInfo,
                    partProfilePic,
                    partAdditionalDocument,
                    partLicensePic
                )
            }
        }
        else if (additionalDocumentselectedFileData!="") {
            if(SignUpBottomSheetFragment.photoSelectedBitmap!=null) {

                updateEmployeeProfileCall = webService.updateEmployeeWithoutLicense(
                    requestBodyAllInfo,
                    partProfilePic,
                    partAdditionalDocument
                )
            }
            else{
                updateEmployeeProfileCall = webService.updateEmployeeProfile(
                    requestBodyAllInfo,
                    partProfilePic,
                    partAdditionalDocument,
                    partLicensePic
                )
            }

            }
        else{
            updateEmployeeProfileCall = webService.updateEmployeeProfileWithoutLicenseDoc(
                requestBodyAllInfo,
                partProfilePic
            )
        }


        updateEmployeeProfileCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast(response.body()!!.message)
                        SignUpBottomSheetFragment.photoSelectedBitmap=null
                        onBackPressed()
                    } else {
                        showToast("Something went wrong ...")
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }
    private fun updateLicenseeProfileDetails() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", businessNameEditText.text.toString())
        //jsonObject.addProperty("_id", HelperMethods.getUserId())
        jsonObject.add("workingDays", weekDaysAdapter!!.getSelectedWeekdays())
        if(businessEmail!=businessEmailEditText.text.toString()) {
            jsonObject.addProperty("email", businessEmailEditText.text.toString())
        }
        if(businessMobile!=businessNumberEditText.text.toString()) {
            jsonObject.addProperty("mobile", businessNumberEditText.text.toString())
        }

        var gmtStartTime=HelperMethods.formatDateTimeToString(workingStartTime,"HH:mm","GMT")
        var gmtEndTime=HelperMethods.formatDateTimeToString(workingEndTime,"HH:mm","GMT")
        jsonObject.addProperty("workingHours", gmtStartTime+"-"+gmtEndTime)

        jsonObject.addProperty("bsbNumber",bsbNumberEditText.text.toString())
        jsonObject.addProperty("accountNumber",accountNumberEditText.text.toString())
        jsonObject.addProperty("taxId",taxIDEditText.text.toString())

        var tempUserAddressInfo = JsonObject()
        tempUserAddressInfo.addProperty("text",businessAddressInfo.get("text").asString)
        tempUserAddressInfo.addProperty("pincode",businessAddressInfo.get("pincode").asString)
        tempUserAddressInfo.addProperty("city",businessAddressInfo.get("city").asString)
        tempUserAddressInfo.addProperty("state",businessAddressInfo.get("state").asString)
        tempUserAddressInfo.addProperty("country",businessAddressInfo.get("country").asString)
        tempUserAddressInfo.add("coordinates", businessAddressInfo.getAsJsonArray("coordinates"))
        jsonObject.add("address", tempUserAddressInfo)

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),jsonObject.toString())

        if ( businessprofilePicData!= null) {
            val file = Utils.convertBitmapToFile("temp.png",businessprofilePicBitmap,context)
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

        if ( selectedFileData!= "") {
            val file=selectedFileDataFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("application/pdf".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partIncorporation = requestFile?.let {
                MultipartBody.Part.createFormData("licenseeProofOfIncorporation",
                    file.name, it
                )
            }
        }
        var updateLicenseeProfileCall: Call<CommonResponse>
        if(!isBusinessDocumentSet){
            updateLicenseeProfileCall = webService.updateLicenseeProfile(
                requestBodyAllInfo,
                partLicenseeLogo
            )
        }
        else{
            updateLicenseeProfileCall = webService.updateLicenseeProfile(
                requestBodyAllInfo,
                partLicenseeLogo,
                partIncorporation
            )
        }

        updateLicenseeProfileCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {

                        showToast(response.body()!!.message)
                        onBackPressed()
                    } else {
                        showToast("Something went wrong ...")
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val minute: Int = cal.get(Calendar.MINUTE)
        var timePickerDialog = TimePickerDialog(context, this, hour, minute, true)
        timePickerDialog.setOnShowListener {
            val positiveColor: Int = ResourcesUtil.getColor(R.color.themeDark)
            val negativeColor: Int = ResourcesUtil.getColor(R.color.themeDark)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
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

    var userEmail=""
    var userMobile=""
    var businessEmail=""
    var businessMobile=""
    var url:String=""
    private fun getProfile() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)

        url= ConstantApi.LIVE_BASE_URL+"employee/profile?"

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization",HelperMethods.getAuthorizationToken())
        cb.timeout(20000)

            cb.url(url+"employeeId="+HelperMethods.getUserId())


            cb.type(
                JSONObject::class.java
            ).weakHandler(
                this,
                "getRequestedData"
            )
        mAQuery.progress(mProgress).ajax(cb)

    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    fun getRequestedData(
        url: String?, `object`: JSONObject?,
        status: AjaxStatus
    ) {

        val aQuery=AQuery(context)
        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals( ConstantApi.LIVE_BASE_URL+"employee/profile?"+"employeeId="+HelperMethods.getUserId(), ignoreCase = true)) {
                        if (`object` != null) {

                            if (`object`.getBoolean("success")) {

                                val employeeObj=`object`.getJSONObject("employeeObj")
                                val licenseeObj=`object`.getJSONObject("licenseeObj")

                                if(flag=="userInfo") {
                                    lytVerfication.show()
                                    if(employeeObj.getBoolean("isMobileVerified")){
                                        txtVerifyMobile.gone()
                                    }
                                    else{
                                        txtVerifyMobile.show()
                                    }
                                    if(employeeObj.getBoolean("isEmailVerified")){
                                        txtVerifyEmail.gone()
                                    }
                                    else{
                                        txtVerifyEmail.show()
                                    }

                                    if(employeeObj.getBoolean("isOwner")) {
                                        saveProfileInfo.show()
                                        txtChangePwd.show()
                                        lytVerfication.show()
                                    }
                                    else{
                                        saveProfileInfo.gone()
                                        txtChangePwd.gone()
                                        lytVerfication.gone()
                                    }
                                    var imgUrl=employeeObj.getJSONObject("photo").getString("data")
                                    HelperMethods.downloadImage(imgUrl, context, userProfileImageView,1)

                                    /*save Employee object*/
                                    userName.text = employeeObj.getString("name").toEditable()
                                    titleEditText.text = employeeObj.getString("title").toEditable()
                                    if(employeeObj.has("address")) {
                                        userAddress.text = (employeeObj.getJSONObject("address")
                                            .getString("text") + "," + employeeObj.getJSONObject("address")
                                            .getString("city") + "," + employeeObj.getJSONObject("address")
                                            .getString("state") + "," + employeeObj.getJSONObject("address")
                                            .getString("country"))

                                        //saving employee address
                                        val tempAddressObj=employeeObj.getJSONObject("address")
                                        val tempuserAddressInfo= JsonObject()
                                        tempuserAddressInfo.addProperty("text",tempAddressObj.getString("text"))
                                        tempuserAddressInfo.addProperty("pincode",tempAddressObj.getString("pincode"))
                                        tempuserAddressInfo.addProperty("city",tempAddressObj.getString("city"))
                                        tempuserAddressInfo.addProperty("state",tempAddressObj.getString("state"))
                                        tempuserAddressInfo.addProperty("country",tempAddressObj.getString("country"))
                                        val tempCordinateArr=tempAddressObj.getJSONObject("location").getJSONArray("coordinates")
                                        val arr=JsonArray()
                                        arr.add(tempCordinateArr.getDouble(0))
                                        arr.add(tempCordinateArr.getDouble(1))
                                        tempuserAddressInfo.add("coordinates",arr)

                                        isAddressSet = true
                                        isSelectUserAddress = false
                                        userAddressInfo = tempuserAddressInfo
                                        userAddress.setText(userAddressInfo.get("text").asString)


                                    }
                                    userMobile = employeeObj.getString("mobile")
                                    userEmail = employeeObj.getString("email")

                                    userMobileNumber.text = employeeObj.getString("mobile").toEditable()
                                    userEmailAddress.text = employeeObj.getString("email").toEditable()
                                    userDOB.text = HelperMethods.convertUTCtoNormal(employeeObj.getString("dob").toString())


                                    //saving driving license
                                    val tempdrivingLicense=employeeObj.getJSONObject("driverLicense")
                                    driverLicenceInfo= JsonObject()
                                    driverLicenceInfo.addProperty("card",tempdrivingLicense.getString("card"));
                                    driverLicenceInfo.addProperty("expiry",tempdrivingLicense.getString("expiry"));
                                    driverLicenceInfo.addProperty("state",tempdrivingLicense.getString("state"));
                                    driverLicenceInfo.addProperty("scan",tempdrivingLicense.getJSONObject("scan").getString("data"));
                                    //HelperMethods.downloadImage(tempdrivingLicense.getString("scan"), context, drivingLicenseImageView)
                                    addDrivingLicenseEditText.setText(tempdrivingLicense.getString("card"))
                                    isLicenseSet=true


                                   /*
                                    aQuery.id(userProfileImageView).image(imgUrl,true,true,0,0, object : BitmapAjaxCallback(){
                                        override fun callback(
                                            url: String?,
                                            iv: ImageView?,
                                            bm: Bitmap?,
                                            status: AjaxStatus?
                                        ) {
                                            super.callback(url, iv, bm, status)
                                            if (bm != null) {
                                                iv!!.setImageBitmap(bm)
                                            }
                                        }
                                    }.header("User-Agent", "android"))*/

                                    if(employeeObj.has("additionalDocument")) {
                                        if (employeeObj.getJSONObject("additionalDocument")
                                                .has("scan")
                                        ) {
                                            lytAdditionalProof.show()
                                            urlAdditonalDoc =
                                                employeeObj.getJSONObject("additionalDocument")
                                                    .getJSONObject("scan").getString("data")
                                        }
                                        else{
                                            lytAdditionalProof.gone()
                                        }
                                    }
                                    else{
                                        lytAdditionalProof.gone()
                                    }

                                }
                                else{
                                    lytVerfication.show()
                                    if(licenseeObj.getBoolean("isMobileVerified")){
                                        txtVerifyMobile.gone()
                                    }
                                    else{
                                        txtVerifyMobile.show()
                                    }
                                    if(licenseeObj.getBoolean("isEmailVerified")){
                                        txtVerifyEmail.gone()
                                    }
                                    else{
                                        txtVerifyEmail.show()
                                    }

                                    if(employeeObj.getBoolean("isOwner")) {
                                        saveProfileInfo.show()
                                        txtChangePwd.gone()
                                        lytVerfication.show()
                                    }
                                    else{
                                        saveProfileInfo.gone()
                                        txtChangePwd.gone()
                                        lytVerfication.gone()
                                    }


                                    var imgUrl=licenseeObj.getJSONObject("logo").getString("data")
                                    HelperMethods.downloadImage(
                                        imgUrl,
                                        context,
                                        businessProfileImageView,1)

                                    /*show licensee object*/
                                    businessNameEditText.text = licenseeObj.getString("name").toEditable()
                                    businessNumberEditText.text = licenseeObj.getString("mobile").toEditable()
                                    businessLocationEditText.text = (licenseeObj.getJSONObject("address").getString("text")).toEditable()

                                    businessMobile = licenseeObj.getString("mobile")
                                    businessEmail = licenseeObj.getString("email")
                                    if(licenseeObj.has("email")) {
                                        businessEmailEditText.text =
                                            licenseeObj.getString("email").toEditable()
                                    }
                                    else{
                                        businessEmailEditText.text = "NA".toEditable()
                                    }

                                    bsbNumberEditText.text = licenseeObj.getJSONObject("payment").getString("bsbNumber").toEditable()
                                    accountNumberEditText.text = licenseeObj.getJSONObject("payment").getString("accountNumber").toEditable()
                                    taxIDEditText.text = licenseeObj.getJSONObject("payment").getString("taxId").toEditable()
                                    //saving employee address
                                    val tempAddressObj=licenseeObj.getJSONObject("address")
                                    val tempuserAddressInfo= JsonObject()

                                    tempuserAddressInfo.addProperty("text",if(tempAddressObj.has("text"))
                                        tempAddressObj.getString("text")
                                    else "")
                                    tempuserAddressInfo.addProperty("pincode",if(tempAddressObj.has("pincode"))
                                        tempAddressObj.getString("pincode")
                                    else "")
                                    tempuserAddressInfo.addProperty("city",if(tempAddressObj.has("city"))
                                        tempAddressObj.getString("city")
                                    else "")
                                    tempuserAddressInfo.addProperty("state",if(tempAddressObj.has("state"))
                                        tempAddressObj.getString("state")
                                    else "")
                                    tempuserAddressInfo.addProperty("country",if(tempAddressObj.has("country"))
                                        tempAddressObj.getString("country")
                                    else "")
                                    if(tempAddressObj.has("location")) {
                                        val tempCordinateArr =
                                            tempAddressObj.getJSONObject("location")
                                                .getJSONArray("coordinates")
                                        val arr = JsonArray()
                                        arr.add(tempCordinateArr.getDouble(0))
                                        arr.add(tempCordinateArr.getDouble(1))
                                        tempuserAddressInfo.add("coordinates", arr)
                                    }
                                    else{
                                        val tempCordinateArr =
                                            tempAddressObj.getJSONArray("coordinates")
                                        val arr = JsonArray()
                                        arr.add(tempCordinateArr.getDouble(0))
                                        arr.add(tempCordinateArr.getDouble(1))
                                        tempuserAddressInfo.add("coordinates", arr)
                                    }

                                    isBusinessAddressSet = true
                                    businessAddressInfo = tempuserAddressInfo
                                    businessLocationEditText.setText(
                                        businessAddressInfo.get("text").asString
                                    )

                                    if(licenseeObj.getJSONObject("proofOfIncorporation").has("doc")) {
                                        urlIncorporationDoc =
                                            licenseeObj.getJSONObject("proofOfIncorporation")
                                                .getString("doc")
                                        showincorporationDoc.show()
                                    }
                                    else{
                                        showincorporationDoc.gone()
                                    }
                                    if(!licenseeObj.getString("workingDays").isEmpty()) {
                                        setUpWeekDaysRecyclerView(licenseeObj.getString("workingDays"))
                                    }

                                    workingStartTime=HelperMethods.formatDateTimeToString(licenseeObj.getString("workingHours").split("-")[0],"HH:mm","UTC")
                                    workingEndTime=HelperMethods.formatDateTimeToString(licenseeObj.getString("workingHours").split("-")[1],"HH:mm","UTC")
                                    operationFrom.text=workingStartTime
                                    operationTo.text=workingEndTime
                                    isBusinessTimeFromSet = true
                                    isBusinessTimeEndSet = true

                                    /*
                                    aQuery.id(businessProfileImageView).image(imgUrl,true,true,0,0, object : BitmapAjaxCallback(){
                                        override fun callback(
                                            url: String?,
                                            iv: ImageView?,
                                            bm: Bitmap?,
                                            status: AjaxStatus?
                                        ) {
                                            super.callback(url, iv, bm, status)
                                            if (bm != null) {
                                                iv!!.setImageBitmap(bm)
                                            }
                                        }
                                    }.header("User-Agent", "android"))*/


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

    class GetBitmapFromURLAsync : AsyncTask<String, Void, Bitmap>()
    {

        private lateinit var resultBitmpa: Bitmap

        override fun onPostExecute(bitmap:Bitmap) {
            //  return the bitmap by doInBackground and store in result
            resultBitmpa= bitmap;
        }

        override fun doInBackground(vararg params: String?): Bitmap {
            return (if(HelperMethods.getBitmapFromURL(params[0])!=null)
                HelperMethods.getBitmapFromURL(params[0])
            else
                ProfileFragment.dummyBitmap)!!
        }
    }

    var weekDaysAdapter: WeekDaysAdapter? = null
    private var selectedWeekDays = arrayListOf<String>()
    private fun setUpWeekDaysRecyclerView(string: String) {
        val weekDaysList = arrayListOf<WeekDay>()
        val strArr:ArrayList<String> = string.split(",") as ArrayList<String>
        weekDaysList.add(WeekDay("M", getFlag("MON",strArr), "Monday"))
        weekDaysList.add(WeekDay("T",  getFlag("TUE",strArr), "Tuesday"))
        weekDaysList.add(WeekDay("W",  getFlag("WED",strArr), "Wednesday"))
        weekDaysList.add(WeekDay("T",  getFlag("THU",strArr), "Thursday"))
        weekDaysList.add(WeekDay("F",  getFlag("FRI",strArr), "Friday"))
        weekDaysList.add(WeekDay("S",  getFlag("SAT",strArr), "Saturday"))
        weekDaysList.add(WeekDay("S",  getFlag("SUN",strArr), "Sunday"))
        weekDaysAdapter = WeekDaysAdapter(context, weekDaysList,"profile")
        weekDaysRecyclerView.adapter = weekDaysAdapter
    }

    private fun getFlag(strToCheck: String, strArr: java.util.ArrayList<String>): Boolean {

        for(temp in strArr){
            if(strToCheck==temp.trim()){
                return true
            }
        }
        return false
    }

    fun onDaySelected(isSelected: Boolean, day: String) {
        if (isSelected) selectedWeekDays.add(day) else selectedWeekDays.remove(day)
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
        } else
            PermissionUtils.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PermissionUtils.READ_EXTERNAL_STORAGE
            )
    }
    private fun openPDFAndImageChooser() {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        //val mimeTypes = arrayOf( "application/pdf")

        if(true){//android 10 file picker code
            // Request code for selecting a PDF document.
//            const val PICK_PDF_FILE = 2

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

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
        intent.type = "image/*|application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.FILE_SELECTION_REQUEST_CODE)
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
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
            additionalDocumentFile= File(path)
            LogUtil.info("selectedFile : ", additionalDocumentselectedFileData)
        }
        else if (businessDetailsLayout.visibility == View.VISIBLE) {

            txtNoFileAdded.text = "File added"
            isBusinessDocumentSet = true
            selectedFileData = HelperMethods.getBase64FromFile(context, File(path))
            selectedFileDataFile= File(path)
            LogUtil.info("selectedFile : ", selectedFileData)
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
        addDrivingLicenseEditText.text=driverLicenceInfo.get("card").toString().toEditable()
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
        //set min selectable date to today
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

    private fun showMenuOption(view: View) {
        val popupMenu = PopupMenu(this@ProfileEditActivity, view)
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
                        EZPhotoPick.startPhotoPickActivity(this@ProfileEditActivity, config)
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
                        EZPhotoPick.startPhotoPickActivity(this@ProfileEditActivity, config)
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
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(this@ProfileEditActivity)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                    setImageData(pickedPhotoBitmap)
                    //userProfileImageView.setImageBitmap(pickedPhotoBitmap)
                    showToast("Image Picked")
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection canceled")
        }else if (requestCode == Constants.FILE_SELECTION_REQUEST_CODE) {
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
                                pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)//content://com.android.providers.downloads.documents/document/msf%3A893
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
        }else if (requestCode == Constants.LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                locationBottomSheet.onActivityResult(requestCode, resultCode, data)
            } else
                showToast("Location Permission Cancelled")
        } else if (requestCode == IntentParams.REQUEST_SELECT_PLACE) {
            locationBottomSheet.onActivityResult(requestCode, resultCode, data)
        }
    }
    fun setUserAddress(addressObject: JsonObject) {
        if (isSelectUserAddress) {
            isAddressSet = true
            isSelectUserAddress = false
            userAddressInfo = addressObject
            userAddress.setText(userAddressInfo.get("addressTitle").asString
            )
        } else {
            isBusinessAddressSet = true
            businessAddressInfo = addressObject
            businessLocationEditText.setText(
                businessAddressInfo.get("addressTitle").asString
            )
        }
    }
    private fun setImageData(imageBitmap: Bitmap) {
        if(flag=="userInfo"){
            profilePicData = HelperMethods.getBase64FromBitmap(imageBitmap)
            profilePicBitmap = imageBitmap
            userProfileImageView.setImageBitmap(imageBitmap)
        }else {
            isBusinessLOGOSet = true
            businessprofilePicData = HelperMethods.getBase64FromBitmap(imageBitmap)
            businessprofilePicBitmap=imageBitmap
            businessProfileImageView.setImageBitmap(imageBitmap)
        }

        //imageSelectionView?.setImageBitmap(imageBitmap)
        showToast("Image Picked")
    }
    private fun compressImageBitmap(bitmap: Bitmap): String {
        return try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ALPHA_8
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: Exception) {
            ""
        }
    }
    private fun getFileMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
    }

    override fun onLocationAdded(locationString: String) {


    }
}

