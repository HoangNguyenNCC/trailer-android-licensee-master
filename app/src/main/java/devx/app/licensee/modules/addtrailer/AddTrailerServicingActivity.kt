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
import android.text.Editable
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
import devx.app.licensee.common.utils.PDFTools
import devx.app.licensee.webapi.*
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.search.UpdateDocumentBottomSheet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import devx.app.seller.webapi.response.CommonResponse
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.*
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.documentLayout
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.insuranceDocument
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.textView1
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.trailerImage
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.trailerName
import kotlinx.android.synthetic.main.activity_add_trailer_servicing.txtNoFileAdded
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTrailerServicingActivity : BaseActivity(), DatePickerDialog.OnDateSetListener,
    PickiTCallbacks {

    private var dateSelectionMode = 0
    private var selectedDocumentData = ""
    private var selectedServicingDocumentData = ""
    private var itemType = "Trailer"
    var pickiT: PickiT? = null

    var serviceDate=""
    var nextDueDate=""
    var name=""
    var trailerId=""


    var partServicing: MultipartBody.Part? = null
    private var servicingDocumentFile:File? = null
    private var documentFlag=""
    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trailer_servicing)
        CommonTopbarView().setup(this)
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

        if(intent.getStringExtra("flag")=="add"){
            servicingIssueDate.text =""
            servicingExpiryDate.text = ""
            servicingName.text="".toEditable()
            trailerId=intent.getStringExtra("trailerId").toString()

            textView1.text="Add Servicing"
            addServicing.text="Save Servicing"
        }
        else{
            servicingIssueDate.text =
                intent.getStringExtra("serviceDate")
            servicingExpiryDate.text =
                intent.getStringExtra("nextDueDate")
            servicingName.text=intent.getStringExtra("name").toString().toEditable()
            trailerId=intent.getStringExtra("trailerId").toString()

            textView1.text="Edit Servicing"
            addServicing.text="Save Servicing"
        }

        HelperMethods.downloadImage(intent.getStringExtra("photo"), context,trailerImage)
        trailerName.text = "" + intent.getStringExtra("trailerName")

        pickiT = PickiT(this, this)

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
        servicingIssueDate.setOnClickListener {
            dateSelectionMode = 1
            showDatePicker()
        }

        servicingExpiryDate.setOnClickListener {
            dateSelectionMode = 2
            showDatePicker()
        }

        addServicing.setOnClickListener {
            if(intent.getStringExtra("flag")=="edit") {
                if (servicingIssueDate.text.toString().isEmpty()) {

                    showToast("Please select service issued date")
                } else if (servicingExpiryDate.text.toString().isEmpty()) {

                    showToast("Please select service expiry date")
                } else if (servicingName.text.toString().isEmpty()) {

                    showToast("Please enter service name")
                } else if (!HelperMethods.compareTwoDate(
                        servicingIssueDate.text.toString(),
                        servicingExpiryDate.text.toString()
                    )
                ) {
                    showToast("Issue date must be before the expiry date")
                }else {
                    updateOrAddServicing()
                }
            }
            else {
                if (servicingIssueDate.text.toString().isEmpty()) {

                    showToast("Please select service issued date")
                } else if (servicingExpiryDate.text.toString().isEmpty()) {

                    showToast("Please select service expiry date")
                } else if (servicingName.text.toString().isEmpty()) {

                    showToast("Please enter service name")
                } else if (!HelperMethods.compareTwoDate(
                        servicingIssueDate.text.toString(),
                        servicingExpiryDate.text.toString()
                    )
                ) {

                    showToast("Issue date must be before the expiry date")
                } else if (selectedDocumentData == "") {
                    showToast("Please select document")
                } else {
                    updateOrAddServicing()
                }
            }

        }

        insuranceDocument.setOnClickListener {
            if(intent.getStringExtra("flag")=="edit") {
                UpdateDocumentBottomSheet("SERVICING").show(supportFragmentManager, "")
            }
            else{
                chooseDocument()
            }

        }
    }
    /*call from update document bottom sheet*/
    fun showDocument(){
       /* val intent=Intent(this,ShowPDFUrlActivity.java::class)
        intent.putExtra("url",Uri.parse(intent.getStringExtra("document"))
                context.startActivity(intent)*/
        /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("document")))
        context.startActivity(browserIntent)*/
        PDFTools.openPDFThroughGoogleDrive(this,intent.getStringExtra("document"));
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
    private fun updateOrAddServicing() {

        var jsonServicing= JsonObject()
        jsonServicing.addProperty("itemType",itemType)
        jsonServicing.addProperty("itemId", trailerId)
        if(intent.getStringExtra("flag")=="edit") {
            jsonServicing.addProperty("_id", intent.getStringExtra("_id"))
        }
        jsonServicing.addProperty("serviceDate", HelperMethods.formatDateTimeToString(servicingIssueDate.text.toString(),"yyyy-MM-dd","GMT"))
        jsonServicing.addProperty("nextDueDate", HelperMethods.formatDateTimeToString(servicingExpiryDate.text.toString(),"yyyy-MM-dd","GMT"))
        jsonServicing.addProperty("name", servicingName.text.toString())

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),jsonServicing.toString())

        if (selectedDocumentData !="") {
            val file = servicingDocumentFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.let { it.asRequestBody("application/pdf".toMediaTypeOrNull()) }
            // MultipartBody.Part is used to send also the actual file name
            partServicing = requestFile?.let {
                MultipartBody.Part.createFormData("servicingDocument",
                    file?.name, it
                )
            }
        }
        showProgressDialog()

        var itemServicingCall: Call<CommonResponse>
        if(intent.getStringExtra("flag")=="edit") {
            if (selectedDocumentData != "") {
                itemServicingCall = webService.itemServicing(requestBodyAllInfo)
            } else {
                itemServicingCall =
                    webService.itemServicing(requestBodyAllInfo, partServicing)
            }
        }
        else{
            itemServicingCall =
                webService.itemServicing(requestBodyAllInfo, partServicing)
        }



        itemServicingCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast("Servicing updated Successfully")
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
        servicingDocumentFile=File(path)
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

        if (dateSelectionMode == 1) servicingIssueDate?.text = date else servicingExpiryDate.text =
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
}
