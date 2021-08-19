package devx.app.licensee.modules.addtrailer

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import com.google.gson.JsonObject
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.PDFTools
import devx.app.licensee.modules.signup.VerifyOTPActivity
import devx.app.licensee.webapi.*
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.search.ChooseAddUpdateBottomSheet
import devx.app.seller.modules.search.UpdateDocumentBottomSheet
import devx.app.seller.modules.signup.SignUpActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.*
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.txtNoFileAdded
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import devx.app.seller.webapi.response.CommonResponse
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.trailerImage
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.trailerName
import kotlinx.android.synthetic.main.activity_details_trailer.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTrailerInsuranceActivity : BaseActivity(), DatePickerDialog.OnDateSetListener,
    PickiTCallbacks {

    private var selectedDocumentData = ""
    private var documentFlag=""
    private var dateSelectionMode = 0
    private var itemType = "Trailer"
    var pickiT: PickiT? = null

    private var insuranceDocumentFile:File?=null
    var partInsurance: MultipartBody.Part? = null
    var trailerId=""
    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trailer_insurance)
        CommonTopbarView().setup(this)


        pickiT = PickiT(this, this)

        if(intent.getStringExtra("flag")=="add"){
            insuranceIssueDate.text =""
            insuranceExpiryDate.text = ""
            trailerId= intent.getStringExtra("trailerId").toString()
            textView1.text="Add Insurance"
            addInsurance.text="Save Insurance"
        }
        else{
            insuranceIssueDate.text = intent.getStringExtra("issueDate")
            insuranceExpiryDate.text = intent.getStringExtra("expiryDate")
            trailerId= intent.getStringExtra("trailerId").toString()
            textView1.text="Edit Insurance"
            addInsurance.text="Save Insurance"
        }

        HelperMethods.downloadImage(intent.getStringExtra("photo"), context, trailerImage)
        trailerName.text = "" + intent.getStringExtra("trailerName")
        if(intent.hasExtra("document")) {
            if (intent.getStringExtra("document") != "" || intent.getStringExtra("document") != null) {
                documentLayout.show()
                txtNoFileAdded.text = "File added"
                documentFlag = "added";
            } else {
                documentLayout.show()
                txtNoFileAdded.text = "No File added"
                documentFlag = "";
            }
        }
        insuranceIssueDate.setOnClickListener {
            dateSelectionMode = 1
            showDatePicker()
        }

        insuranceExpiryDate.setOnClickListener {
            dateSelectionMode = 2
            showDatePicker()
        }

        addInsurance.setOnClickListener {
            Log.d("here", intent.getStringExtra("flag").toString());
            if (intent.getStringExtra("flag")=="add") {
                if (insuranceIssueDate.text.toString().isEmpty()) {
                    showToast("Please select issue date")
                } else if (insuranceExpiryDate.text.toString().isEmpty()) {
                    showToast("Please select expiry date")
                } else if (!HelperMethods.compareTwoDate(
                        insuranceIssueDate.text.toString(),
                        insuranceExpiryDate.text.toString()
                    )
                ) {
                    showToast("Issue date must be before the expiry date")
                } else if (selectedDocumentData == "") {
                        showToast("Please select document")
                } else {
                    updateOrAddInsurance()
                }
            }
            else{

                if (insuranceIssueDate.text.toString().isEmpty()) {
                    showToast("Please select issue date")
                } else if (insuranceExpiryDate.text.toString().isEmpty()) {
                    showToast("Please select expiry date")
                } else if (!HelperMethods.compareTwoDate(
                        insuranceIssueDate.text.toString(),
                        insuranceExpiryDate.text.toString()
                    )
                ) {
                    showToast("Issue date must be before the expiry date")
                }    else {
                    updateOrAddInsurance()
                }
            }
        }

        insuranceDocument.setOnClickListener {

            if(intent.getStringExtra("flag")=="edit") {
                UpdateDocumentBottomSheet("INSURANCE").show(supportFragmentManager, "")
            }
            else{
                chooseDocument()
            }
        }
    }

    /*call from update document bottom sheet*/
    fun showDocument(){
        PDFTools.openPDFThroughGoogleDrive(this,intent.getStringExtra("document"));
       /* val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("document")))
        context.startActivity(browserIntent)*/
       /* val intent1=Intent(this, ShowPDFActivity::class.java)
        intent1.putExtra("url",intent.getStringExtra("document"))
        context.startActivity(intent1)*/
    }
     fun chooseDocument() {

        if (PermissionUtils.hasPermissions(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openPDFChooser()
        } else {
            PermissionUtils.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PermissionUtils.READ_EXTERNAL_STORAGE
            )
        }
    }


    private fun openPDFChooser() {
        val intent: Intent =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
            } else {
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.INTERNAL_CONTENT_URI
                )
            }
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.FILE_SELECTION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPDFChooser()
            } else
                showToast("Permission Denied")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.FILE_SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.data?.let {
                        showToast("File Selected")
                        pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (path == null) return
        txtNoFileAdded.text = "File added"
        selectedDocumentData = HelperMethods.getBase64FromFile(context, File(path))
        insuranceDocumentFile=File(path)
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val dpdd = DatePickerDialog(
            this, this,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        val dpd = dpdd.datePicker
        //dpd.maxDate = System.currentTimeMillis() - 1000
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

        val inputPattern = SimpleDateFormat("dd MM yyyy")
        val outputPattern = SimpleDateFormat("yyyy-MM-dd")
        date = StringBuilder()
        try {
            date.append(outputPattern.format(inputPattern.parse("$dayOfMonth $strMonth $year")))
        } catch (e23: Throwable) {
        }

        if (dateSelectionMode == 1) insuranceIssueDate?.text = date else insuranceExpiryDate.text =
            date
    }

    override fun onBackPressed() {
        pickiT!!.deleteTemporaryFile()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            pickiT!!.deleteTemporaryFile()
        }
    }

    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonStartListener() {
    }


    private fun updateOrAddInsurance() {

        showProgressDialog()
        val requestObject = JsonObject()
        requestObject.addProperty("itemType", itemType)
        requestObject.addProperty("itemId", trailerId)
        if(intent.getStringExtra("flag")=="edit") {
            requestObject.addProperty("_id", intent.getStringExtra("_id"))
        }
        requestObject.addProperty("issueDate", HelperMethods.formatDateTimeToString(insuranceIssueDate.text.toString(),"yyyy-MM-dd","GMT"))
        requestObject.addProperty("expiryDate", HelperMethods.formatDateTimeToString(insuranceExpiryDate.text.toString(),"yyyy-MM-dd","GMT"))

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),requestObject.toString())

        if (selectedDocumentData != "") {
            val file = insuranceDocumentFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { RequestBody.create("application/pdf".toMediaTypeOrNull(), it) }
            // MultipartBody.Part is used to send also the actual file name
            partInsurance = requestFile?.let {
                MultipartBody.Part.createFormData("insuranceDocument",
                    file?.name, it
                )
            }
        }
        var itemInsuranceCall: Call<CommonResponse>
        if(intent.getStringExtra("flag")=="edit") {
            if (selectedDocumentData != "") {
                itemInsuranceCall = webService.itemInsurance(requestBodyAllInfo)
            } else {
                itemInsuranceCall =
                    webService.itemInsurance(requestBodyAllInfo, partInsurance)
            }
        }
        else{
            itemInsuranceCall =
                webService.itemInsurance(requestBodyAllInfo, partInsurance)
        }



        itemInsuranceCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast("Insurance updated Successfully")
                        finish()
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
}

