package devx.app.licensee.modules.addtrailer

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.*
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.webapi.*
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse
import devx.app.licensee.webapi.response.addUpSell.UpsellItems
import devx.app.licensee.webapi.response.trailerslist.TrailerAddedByAdmin
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp
import devx.app.seller.webapi.response.trailerslist.Trailers
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_trailer.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import devx.app.seller.webapi.response.CommonResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTrailerActivity : BaseActivity(), DatePickerDialog.OnDateSetListener,
    PickiTCallbacks {//, AddTrailerContract.View

    var trailerId=""
    private var tempFeatures = arrayListOf<String>()
    var tempTrailerName=""
    var trailerModel=""
    var tempAvailability=false
    var tempAdminRentalItemId=""
    var preSelectedUpSellItemList= listOf<devx.app.seller.webapi.response.upSellItem.UpsellItems>()

   // private lateinit var presenterAdd: AddTrailerPresenter
     private var bitmapTrailerImages = arrayListOf<Bitmap>()
    var rTrailerPreviewImagesLL: LinearLayout? = null
    var rTrailerPreviewImagesScrollView:HorizontalScrollView?=null

    var trailers:Trailers?=null

    var trailersList = listOf<Trailers>()
    var upSellItemList= arrayListOf<UpsellItems>()

    var pickiT: PickiT? = null

    private var selectedDocumentData = ""
    private var selectedServicingDocumentData = ""

    private var insuranceDocumentFile:File?=null
    private var servicingDocumentFile:File? = null
    private var dateSelectionMode = 0
   //private lateinit var upsellingAddTrailerAdapter: UpsellingAddTrailerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trailer)

        if(intent.hasExtra(IntentParams.TRAILER_ID)){
            trailerId= intent.getStringExtra(IntentParams.TRAILER_ID).toString()
        }
        pickiT = PickiT(this, this)
        CommonTopbarView().setup(this, "")

       /* upsellingAddTrailerAdapter = UpsellingAddTrailerAdapter(
            context!!,
            upSellItemList
        )
        upSellItemRecyclerView.adapter = upsellingAddTrailerAdapter*/

        rTrailerPreviewImagesLL = findViewById(R.id.rTrailerPreviewImagesLL)
        rTrailerPreviewImagesScrollView=findViewById(R.id.rTrailerPreviewImagesScrollView)
        rTrailerPreviewImagesScrollView?.gone()
        rTrailerPreviewImagesLL?.gone()


       HelperMethods.disableEditText(aSizeET);
       HelperMethods.disableEditText(aCapacityET);
       HelperMethods.disableEditText(aTareET);
       HelperMethods.disableEditText(aDescriptionET);
       HelperMethods.disableEditText(aTypeET);
        /*show,hide layout*/

        lytTrailerDetails.show()
        //lytAddUpsellItem.gone()
        lytTrailerInsurance.gone()
        lytTrailerServicing.gone()
        if(trailerId=="") {
            rRentButton.text = "NEXT : ADD INSURANCE"
            txtViewTitle.text = "Add Trailer"
        }
        else{
            rRentButton.text = "UPDATE TRAILER"
            txtViewTitle.text ="Edit Trailer"
        }

        //presenterAdd = AddTrailerPresenter()
        //presenterAdd.attachView(this, lifecycle)


        rTrailerPreview.setOnClickListener {
            pickImage(rTrailerPreview)
        }

        rRentButton.setOnClickListener {
            if(lytTrailerDetails.visibility==View.VISIBLE){

                 if(tempImgArr.size==0) {
                    showToast("Please select atleast one image")
                }
                 else if(aVinNumberET.text.toString().isEmpty()) {
                     showToast("Please enter VIN number")
                 }
                 else if(aAgeET.text.toString().isEmpty()) {
                     showToast("Please enter age number")
                 }
                else{
                     val bitmap: Bitmap =
                         (rTrailerPreview.getDrawable() as BitmapDrawable).getBitmap()
                     tempBitmap=bitmap
                     addTrailerInsuranceimageView.setImageBitmap(tempBitmap)
                    if (trailerId == "") {

                        lytTrailerDetails.gone()
                        //lytAddUpsellItem.visible()
                        lytTrailerInsurance.visible()
                        lytTrailerServicing.gone()
                        rRentButton.text = "NEXT : ADD SERVICING"
                        txtViewTitle.text = "Add Insurance"
                    }else{
                        if (Utils.isNetworkAvailable(this)) {
                            callAddTrailerAPI()
                        } else {
                            showToast("No Internet Connectivity!")
                        }
                    }
                }

            }
            else if(lytTrailerInsurance.visibility==View.VISIBLE){
                if(addTrailerinsuranceIssueDate.text.toString()==""){
                    showToast("Please select issue date")
                }
                else if(addTrailerinsuranceExpiryDate.text.toString()==""){
                    showToast("Please select expiry date")
                }
                else if(!HelperMethods.compareTwoDate(addTrailerinsuranceIssueDate.text.toString(),addTrailerinsuranceExpiryDate.text.toString())){

                    showToast("Issue date must be before the expiry date")
                }else  if (selectedDocumentData == "") {
                    showToast("Please select document")
                }
                else{
                    if(trailerId=="") {
                        lytTrailerDetails.gone()
                        lytTrailerInsurance.gone()
                        lytTrailerServicing.visible()
                        val bitmap: Bitmap =
                            (rTrailerPreview.getDrawable() as BitmapDrawable).getBitmap()
                        tempBitmap=bitmap
                        addTrailerServicingimageView.setImageBitmap(tempBitmap)
                        rRentButton.text = "SAVE TRAILER"
                        txtViewTitle.text = "Add Servicing"
                    }

                }
            }
            else if(lytTrailerServicing.visibility==View.VISIBLE){

                if(addTrailerservicingName.text.toString()==""){
                    addTrailerservicingName.requestFocus()
                    showToast("Please enter service name")
                }
                else if(addTrailerservicingIssueDate.text.toString()==""){
                    showToast("Please select issue date")
                }
                else if(addTrailerservicingExpiryDate.text.toString()==""){
                    showToast("Please select service due date")
                }
                else if(!HelperMethods.compareTwoDate(addTrailerservicingIssueDate.text.toString(),addTrailerservicingExpiryDate.text.toString())){

                    showToast("Issue date must be before the expiry date")
                }
                else  if (selectedServicingDocumentData == "") {
                    showToast("Please select document")
                }
                else {
                    if (Utils.isNetworkAvailable(this)) {
                        callAddTrailerAPI()
                    } else {
                        showToast("No Internet Connectivity!")
                    }
                }
            }
          /*  else if(lytAddUpsellItem.visibility==View.VISIBLE){


                if(trailerId=="") {
                    lytTrailerDetails.gone()
                    lytAddUpsellItem.gone()
                    lytTrailerInsurance.visible()
                    lytTrailerServicing.gone()
                    val bitmap: Bitmap =
                        (rTrailerPreview.getDrawable() as BitmapDrawable).getBitmap()
                    tempBitmap=bitmap
                    addTrailerInsuranceimageView.setImageBitmap(tempBitmap)
                    rRentButton.text = "NEXT : ADD SERVICING"
                    txtViewTitle.text = "Add Insurance"
                }
                else{
                    if (Utils.isNetworkAvailable(this)) {
                        callAddTrailerAPI()
                    } else {
                        showToast("No Internet Connectivity!")
                    }
                }


            }*/

        }
        spnrTrailer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                trailers=trailersList.get(i)

                tempFeatures= trailers!!.features as ArrayList<String>
                tempTrailerName=trailers!!.name
                tempAvailability=trailers!!.availability
                tempAdminRentalItemId=trailers!!._id
                trailerModel=trailers!!.trailerModel

                    //aVinNumberET.setText(trailers!!.vin)
                    aSizeET.setText(trailers!!.size)
                    aCapacityET.setText(trailers!!.capacity)
                    aTareET.setText(trailers!!.tare)
                    aDescriptionET.setText(trailers!!.description)
                    aTypeET.setText(trailers!!.type)
                    //aAgeET.setText(trailers!!.age.toString())

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }
        addTrailerinsuranceIssueDate.setOnClickListener {
            dateSelectionMode = 1
            showDatePicker()
        }

        addTrailerinsuranceExpiryDate.setOnClickListener {
            dateSelectionMode = 2
            showDatePicker()
        }
        addTrailerservicingIssueDate.setOnClickListener {
            dateSelectionMode = 3
            showDatePicker()
        }

        addTrailerservicingExpiryDate.setOnClickListener {
            dateSelectionMode = 4
            showDatePicker()
        }
        addTrailerinsuranceDocument.setOnClickListener {
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
        addTrailerservicingDocument.setOnClickListener {
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
        getTrailerTypEList();
       // getUpsellItemAPI()

    }


    private fun getTrailerTypEList() {

        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)
        mProgress.show()
        CallApi().getTrailersByAdmin(ConstantApi.LIVE_BASE_URL + "licensee/trailers/admin")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<TrailerAddedByAdmin>() {
                override fun onComplete() {
                }

                override fun onNext(response: TrailerAddedByAdmin) {
                    mProgress.hide()
                    if (response != null && response.success) {
                        trailersList=response.trailersList
                        spnrTrailer.adapter=TrailerByAdminAdapter(response.trailersList,this@AddTrailerActivity,"")
                        if(trailerId!=""){
                            if(Utils.isNetworkAvailable(context)) {
                                getRequestedUpsellItemDetails()
                            }
                        }

                    } else
                        showToast(safeText(response.message))
                }

                override fun onError(e: Throwable) {
                    mProgress.hide()
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                }
            })

    }
var spnrposition=0
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    var url=""
    private fun getRequestedUpsellItemDetails() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)

        url= ConstantApi.LIVE_BASE_URL+"trailer?"

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization",HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url+"id=$trailerId")
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

        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals( ConstantApi.LIVE_BASE_URL+"trailer?"+"id=$trailerId", ignoreCase = true)) {
                        if (`object` != null) {

                            if (`object`.getBoolean("success")) {
                                handlerResponse(`object`.getJSONObject("trailerObj"))

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
    private fun handlerResponse(jsonObject: JSONObject){

        for (i in 0 until trailersList.size){
            if(trailersList.get(i)._id==jsonObject.getString("adminRentalItemId")){
                spnrposition=i
            }
        }
        spnrTrailer.setSelection(spnrposition)

        aVinNumberET.setText(jsonObject.getString("vin"))
        aSizeET.setText(jsonObject.getString("size"))
        aCapacityET.setText(jsonObject.getString("capacity"))
        aTareET.setText(jsonObject.getString("tare"))
        aDescriptionET.setText(jsonObject.getString("description"))
        aTypeET.setText(jsonObject.getString("type"))
        aAgeET.setText(jsonObject.getInt("age").toString())

        for(j in 0 until jsonObject.getJSONArray("features").length()){
            tempFeatures.add(jsonObject.getJSONArray("features").get(j).toString())
        }
        //tempFeatures=response.trailerObj.
        tempTrailerName=jsonObject.getString("name")
        tempAvailability=jsonObject.getBoolean("availability")
        tempAdminRentalItemId=jsonObject.getString("adminRentalItemId")

        //preSelectedUpSellItemList=response.upsellItemsList

       /* if(upsellItemsListTemp.length()>0){
            for(j in 0 until upSellItemList.size){
                for(k in 0 until upsellItemsListTemp.length()) {
                    if (upSellItemList.get(j)._id == upsellItemsListTemp.getJSONObject(k).getString("_id")){
                        upSellItemList.get(j).flag=true
                    }
                }
            }
            upsellingAddTrailerAdapter.notifyDataSetChanged(upSellItemList)
        }*/


        if(jsonObject.has("insurance")) {
            addTrailerinsuranceIssueDate.text =jsonObject.getJSONObject("insurance").getString("issueDate")
            addTrailerinsuranceExpiryDate.text =jsonObject.getJSONObject("insurance").getString("expiryDate")
        }
        if(jsonObject.has("servicing")) {
            addTrailerservicingName.text =
                jsonObject.getJSONObject("servicing").getString("name").toEditable()
            addTrailerservicingIssueDate.text =jsonObject.getJSONObject("servicing").getString("serviceDate")
            addTrailerservicingExpiryDate.text =jsonObject.getJSONObject("servicing").getString("nextDueDate")

        }
        if (jsonObject.getJSONArray("photos").length()>0) {
                    HelperMethods.downloadImage(
                        jsonObject.getJSONArray("photos").getJSONObject(0).getString("data").toString(),
                        context,
                        rTrailerPreview
                    )
        }
        addImageView("from_server", jsonObject.getJSONArray("photos"))
    }

    private fun pickImage(rTrailerPreview: ImageView) {
        if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                showMenuOption(rTrailerPreview)
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


    var partInsurance: MultipartBody.Part? = null
    var partServicing: MultipartBody.Part? = null
    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun callAddTrailerAPI() {

        val vin = aVinNumberET.text.toString()
        val type = aTypeET.text.toString()
        val description = aDescriptionET.text.toString()
        if (aVinNumberET.text.toString().isEmpty()||aVinNumberET.text.toString()=="0") {
            showToast("Enter VIN")
            return
        }
        if (description.isEmpty()) {
            showToast("Enter description")
            return
        }
        if (aCapacityET.text.toString().isEmpty()) {
            showToast("Enter capacity")
            return
        }
        if (aAgeET.text.toString().isEmpty()||aAgeET.text.toString()=="0") {
            showToast("Enter age")
            return
        }
        val size = aSizeET.text.toString()
        val capacity = aCapacityET.text.toString()
        val tare = aTareET.text.toString()
        var age = aAgeET.text.toString()


        var jsonObject = JsonObject()


        var listofphotos = arrayListOf<MultipartBody.Part>()

        for (index in 0 until tempImgArr.size) {
            val bitmap: Bitmap =
                (tempImgArr.get(index).getDrawable() as BitmapDrawable).getBitmap()
            val file = Utils.convertBitmapToFile("temp.png",bitmap,context)
            val photoBody: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            photoBody?.let {
                MultipartBody.Part.createFormData("photos", file?.name,
                    it
                )
            }?.let { listofphotos.add(it) }
        }


        if(trailerId!="") {
            jsonObject.addProperty("_id", trailerId)
        }
        jsonObject.addProperty("name",tempTrailerName)
        jsonObject.addProperty("adminRentalItemId", tempAdminRentalItemId)
        jsonObject.addProperty("trailerModel", trailerModel)
        jsonObject.addProperty("vin", vin)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("size", size)
        jsonObject.addProperty("capacity", capacity)
        jsonObject.addProperty("tare", tare)
        jsonObject.addProperty("age", age)

        val features = tempFeatures
        var featrray = JsonArray()
        for (featUrls in features) {
            featrray.add(featUrls)
        }
        jsonObject.add("features", featrray)

        jsonObject.addProperty("availability", tempAvailability)

        //selected upsell item
       /* if(upSellItemList.size>0) {
            val tempUpsellItem = upSellItemList
            var upSellArr = JsonArray()
            for (featUrls in tempUpsellItem) {
                if (featUrls.flag) {
                    upSellArr.add(featUrls._id)
                }
            }
            jsonObject.add("upsellitems", upSellArr)
        }*/


        var upSellArr = JsonArray()
        jsonObject.add("upsellitems", upSellArr)
        if(trailerId=="") {
            //insurance details
            var jsonInsurance = JsonObject()
            jsonInsurance.addProperty("issueDate", HelperMethods.formatDateTimeToString(addTrailerinsuranceIssueDate.text.toString(),"yyyy-MM-dd","GMT"))
            jsonInsurance.addProperty("expiryDate",HelperMethods.formatDateTimeToString( addTrailerinsuranceExpiryDate.text.toString(),"yyyy-MM-dd","GMT"))
            jsonObject.add("insurance", jsonInsurance)


            var jsonServicing = JsonObject()
            jsonServicing.addProperty("serviceDate", HelperMethods.formatDateTimeToString(addTrailerservicingIssueDate.text.toString(),"yyyy-MM-dd","GMT"))
            jsonServicing.addProperty("nextDueDate", HelperMethods.formatDateTimeToString(addTrailerservicingExpiryDate.text.toString(),"yyyy-MM-dd","GMT"))
            jsonServicing.addProperty("name", addTrailerservicingName.text.toString())
            jsonObject.add("servicing", jsonServicing)

            if (selectedDocumentData != "") {
                val file = insuranceDocumentFile
                // create RequestBody instance from file
                val requestFile: RequestBody? =
                        file?.asRequestBody("application/pdf".toMediaTypeOrNull())
                // MultipartBody.Part is used to send also the actual file name
                partInsurance = requestFile?.let {
                    MultipartBody.Part.createFormData("insuranceDocument",
                        file?.name, it
                    )
                }
            }
            if ( selectedServicingDocumentData!= "") {
                val file=servicingDocumentFile
                // create RequestBody instance from file
                val requestFile: RequestBody? =
                        file?.asRequestBody("application/pdf".toMediaTypeOrNull())
                // MultipartBody.Part is used to send also the actual file name
                partServicing = requestFile?.let {
                    MultipartBody.Part.createFormData("servicingDocument",
                        file?.name, it
                    )
                }
            }
        }

        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),jsonObject.toString())


        showProgressDialog()
        var editAddTrailerCall: Call<CommonResponse>
        if(trailerId=="") {

            editAddTrailerCall = webService.addTrailer(requestBodyAllInfo,listofphotos ,partInsurance,partServicing)

        }
        else{
            editAddTrailerCall =
                webService.editTrailer(requestBodyAllInfo,listofphotos)
        }



        editAddTrailerCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {

                        showToast("Successfully updated Trailer!")
                        HelperMethods.closeEverythingOpenHome(context)
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
    private fun compressImageBitmap(bitmap: Bitmap): Bitmap {
        return try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ALPHA_8
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            BitmapFactory.decodeStream(ByteArrayInputStream(baos.toByteArray()))

        } catch (e: Exception) {
            ""
        } as Bitmap
    }

     var tempBitmap:Bitmap?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(this)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                    if (pickedPhotoBitmap == null) {
                        showToast("Null bitmap")
                        return
                    }
                    var photoName = data!!.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
                    //var photoPath = storage.getAbsolutePathOfStoredPhoto(Constants.APP_STORAGE_DIR, photoName);
                    //var photoSelected = HelperMethods.getBase64FromBitmap(pickedPhotoBitmap)
                    rTrailerPreview.setImageBitmap(pickedPhotoBitmap)
                    //tempBitmap=pickedPhotoBitmap
                    bitmapTrailerImages.add(pickedPhotoBitmap)
                    addImageView("from_gallery",null)
                    showToast("Image Picked")
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection canceled")
        }
        else if (requestCode == Constants.FILE_SELECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.data?.let {
                        showToast("File Selected")
                        pickiT!!.getPath(data.data, Build.VERSION.SDK_INT)
                    }
                }
            }
        }
    }

    private fun showMenuOption(view: View) {
        val popupMenu = PopupMenu(this, view)
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
                        EZPhotoPick.startPhotoPickActivity(this, config)
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
                        EZPhotoPick.startPhotoPickActivity(this, config)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            false
        }
        popupMenu.show()
    }



    //var imgIndex = 0
    var tempImgArr=ArrayList<ImageView>()
    private fun addImageView(flag:String, jsonArray: JSONArray?){//imagePath: String) {

        if(flag=="from_server") {
            rTrailerPreviewImagesLL?.show()
            rTrailerPreviewImagesScrollView?.show()
            rTrailerPreviewImagesLL?.removeAllViews()
            tempImgArr.clear()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val view = layoutInflater.inflate(R.layout.item_image_layout, null, false)
                    val rTrailerPreviewImages = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
                    HelperMethods.downloadImage(
                        jsonArray.getJSONObject(i).getString("data").toString(),
                        context,
                        rTrailerPreviewImages
                    )
                    tempImgArr.add(rTrailerPreviewImages)

                    rTrailerPreviewImagesLL!!.addView(view)
                }
            }

            val view = layoutInflater.inflate(R.layout.item_image_layout_addbutton, null, false)
            val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
            rTrailerPreviewImage.setOnClickListener {
                showMenuOption(rTrailerPreviewImage)
            }
            rTrailerPreviewImagesLL?.addView(view)
        }
        else{
            if (!tempImgArr.isEmpty()) {
                for (j in 0 until tempImgArr.size) {
                    val bitmap: Bitmap =
                        (tempImgArr.get(j).getDrawable() as BitmapDrawable).getBitmap()
                    bitmapTrailerImages.add(bitmap)
                }
            }
            rTrailerPreviewImagesLL?.show()
            rTrailerPreviewImagesScrollView?.show()
            rTrailerPreviewImagesLL?.removeAllViews()
            tempImgArr.clear()

            for (i in bitmapTrailerImages.indices) {
                val view = layoutInflater.inflate(R.layout.item_image_layout, null, false)
                val rTrailerPreviewImages = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
                tempImgArr.add(rTrailerPreviewImages)
                rTrailerPreviewImages.setImageBitmap(bitmapTrailerImages.get(i))
                rTrailerPreviewImagesLL!!.addView(view)
            }

            val view = layoutInflater.inflate(R.layout.item_image_layout_addbutton, null, false)
            val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
            rTrailerPreviewImage.setOnClickListener {
                showMenuOption(rTrailerPreviewImage)
            }
            rTrailerPreviewImagesLL?.addView(view)
            bitmapTrailerImages.clear()
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



    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (path == null) return
        if(lytTrailerServicing.visibility== View.VISIBLE){

            txtNoServiceFileAdded.text = "File added"
            selectedServicingDocumentData = HelperMethods.getBase64FromFile(context, File(path))
            servicingDocumentFile=File(path)
        }
        else{

            txtNoFileAdded.text = "File added"
            selectedDocumentData = HelperMethods.getBase64FromFile(context, File(path))
            insuranceDocumentFile=File(path)
        }
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
       // dpd.maxDate = System.currentTimeMillis() - 1000
       // dpd.maxDate = System.currentTimeMillis() - 1000
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

        val inputPattern = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
        val outputPattern = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        date = StringBuilder()
        try {
            date.append(outputPattern.format(inputPattern.parse("$dayOfMonth $strMonth $year")))
        } catch (e23: Throwable) {
        }

        if (dateSelectionMode == 1) addTrailerinsuranceIssueDate?.text = date
        else if (dateSelectionMode == 2) addTrailerinsuranceExpiryDate.text = date
        else if (dateSelectionMode == 3) addTrailerservicingIssueDate.text = date
        else addTrailerservicingExpiryDate.text = date
    }

    override fun onBackPressed() {
        pickiT!!.deleteTemporaryFile()
        when {
            lytTrailerDetails.visibility == View.VISIBLE -> {

                super.onBackPressed()
            }

            lytTrailerInsurance.visibility == View.VISIBLE -> {
                lytTrailerInsurance.gone()
                lytTrailerServicing.gone()
                lytTrailerDetails.visible()
                if(trailerId=="") {
                    rRentButton.text = "NEXT : ADD INSURANCE"
                    txtViewTitle.text = "Add Trailer"
                }

            }
            lytTrailerServicing.visibility == View.VISIBLE -> {
                lytTrailerInsurance.visible()
                lytTrailerServicing.gone()
                lytTrailerDetails.gone()
                if(trailerId=="") {
                    rRentButton.text = "NEXT : ADD SERVICING"
                    txtViewTitle.text = "Add Insurance"
                }

            }
            /*
            lytAddUpsellItem.visibility == View.VISIBLE -> {
                lytTrailerInsurance.gone()
                lytTrailerServicing.gone()
                lytAddUpsellItem.gone()
                lytTrailerDetails.show()
                if(trailerId=="") {
                    rRentButton.text = "NEXT : LINK UPSELL"
                    txtViewTitle.text = "Add Trailer"
                }
                else{

                    rRentButton.text = "SAVE TRAILER"
                    txtViewTitle.text ="Link Upsell Items"
                }
            }
            * */
        }
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

private fun <T> ObservableSource<T>.subscribe(disposableObserver: DisposableObserver<TrailerDetailsResp>) {

}


/*minusPickupChargesDur.setOnClickListener {
           if (pickUpChargesDuration.text.toString().toInt() != 0) {
               pickUpChargesDuration.text =
                   pickUpChargesDuration.text.toString().toInt().minus(1).toString()
           }
       }

       plusPickupChargesDur.setOnClickListener {
           if (pickUpChargesDuration.text.toString().toInt() >= 0) {
               pickUpChargesDuration.text =
                   pickUpChargesDuration.text.toString().toInt().plus(1).toString()
           }
       }

       minusD2dChargesDur.setOnClickListener {
           if (d2dChargesDuration.text.toString().toInt() != 0) {
               d2dChargesDuration.text =
                   d2dChargesDuration.text.toString().toInt().minus(1).toString()
           }
       }

       plusD2dChargesDur.setOnClickListener {
           if (d2dChargesDuration.text.toString().toInt() >= 0) {
               d2dChargesDuration.text =
                   d2dChargesDuration.text.toString().toInt().plus(1).toString()
           }
       }*/

/*
*


{
	"name": "Hydraulic Tipping Trailer",
	"vin": "123456789",
	"type": "hydraulic_tipping_trailer",
	"description": "When you need a trailer made especially for tipping extra heavy loads the 3500kg hydraulic tipping trailer is made for the job. The galvanised tandem trailers in our range are 100% Australian made and built to handle the big stuff.",
	"size": "6",
	"capacity": "6980 lbs",
	"tare": "2920 lbs",
	"age": 4,

	"features": [
		"Capacity of 3500kg GVM (2400kg Load)"
	],

	"availability": true,
	"rentalCharges": {
		"pickUp": [{
				"duration": 21600000,
				"charges": 123
			}
		],
		"door2Door": [{
				"duration": 21600000,
				"charges": 148
			}
		]
	},
	"dlrCharges": 400,
	"licenseeId": "5e45473faeeab754171906db",
	"photos": [" data:image/jpeg;base64,/9j/4QAYRXhf/Z"]
}


*
  private fun getUpsellItemAPI() {
        //upSellItemRecyclerView.adapter = PlaceHolderAdapter(context, R.layout.trailer1_placeholder)
        val endPoint = "licensee/upsellitems?"

        val requestMap = HashMap<String, String>()
        requestMap["count"] = "5"
        requestMap["skip"] = "0"
        //requestMap["trailerId"]=trailers!!._id

        CallApi().getUpSellOrTrailerItems(ConstantApi.LIVE_BASE_URL + endPoint, requestMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddUpSellItemResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: AddUpSellItemResponse) {
                    if (response != null && response.success ) {

                        if (response.upsellItemsList.size > 0) {
                            upSellItemRecyclerView.visible()
                            txtNoUpsellItemAdded.gone()
                            upSellItemList.clear()
                            upSellItemList= response.upsellItemsList
                            upsellingAddTrailerAdapter.notifyDataSetChanged(upSellItemList)

                        }
                        else{
                            upSellItemRecyclerView.gone()
                            txtNoUpsellItemAdded.visible()
                        }

                        getTrailerTypEList();
                    }

                     else
                        showToast(safeText(response.message))
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                }
            })
    }

* */
