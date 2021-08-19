package devx.app.licensee.modules.addUpSellItem

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import de.hdodenhof.circleimageview.CircleImageView
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.*
import devx.app.licensee.common.utils.HelperMethods.getUniquesElement
import devx.app.licensee.modules.addtrailer.AddTrailerContract
import devx.app.licensee.modules.addtrailer.UpsellingAddTrailerAdapter
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.home.hometab.MyAllTrailerAdapter
import devx.app.seller.webapi.response.CommonResponse
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp
import devx.app.seller.webapi.response.trailerslist.Trailers
import devx.app.seller.webapi.response.upSellItem.UpSellItemByAdminResponse
import devx.app.seller.webapi.response.upSellItem.UpsellItemsByAdmin
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_up_sell_item.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddUpSellItemActivity : BaseActivity(), AddTrailerContract.View {

    private var upsellId = ""
    private var upSellItemImageList = arrayListOf<String>()
    
    var upsellPreviewImagesLL: LinearLayout? = null
    var upsellPreviewImagesScrollViewL: HorizontalScrollView?=null

    var upsellItem:UpsellItemsByAdmin?=null
    private lateinit var upsellingAddTrailerAdapter: UpsellingAddTrailerAdapter

    companion object{
        lateinit var dummyBitmap: Bitmap
        var upSellItemImageBitmapList = arrayListOf<Bitmap>()
    }
    var imageLoadr:ImageLoader?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_up_sell_item)
        CommonTopbarView().setup(this, "")
        imageLoadr=ImageLoader(context.applicationContext,"sam")
        dummyBitmap = BitmapFactory.decodeResource(
            context!!.resources,
            R.drawable.grey_account_icon
        )
        upsellingAddTrailerAdapter = UpsellingAddTrailerAdapter(
            context!!,
            mTrailerList
        )
        trailerModelRecyclerView.adapter = upsellingAddTrailerAdapter
        
       upsellPreviewImagesLL = findViewById(R.id.upSellItemPreviewImagesLL)
        upsellPreviewImagesScrollViewL=findViewById(R.id.upsellPreviewImagesScrollView)

        upsellPreviewImagesScrollViewL?.gone()
        upsellPreviewImagesLL?.gone()
        upSellItemImageBitmapList.clear()
        if (intent.hasExtra(IntentParams.UPSELL_ID))
            upsellId = intent.getStringExtra(IntentParams.UPSELL_ID).toString()

        if(intent.getStringExtra("flag")=="edit"){
            txtTitle.text="Edit Upsell Item"
            txtSubtitle.text="Edit Upsell Item"
        }
        else{
            txtTitle.text="Add Upsell Item"
            txtSubtitle.text="Add New Upsell Item"
        }
        addUpSellItem.text="NEXT: LINK TRAILER MODEL"

        upSellItemPreviewImage.setOnClickListener {
            showImageSelectionPopUp()
        }

        addUpSellItem.setOnClickListener {

            if(lytUpsellDetails.visibility==View.VISIBLE){
                if(tempImgArr.size==0) {
                    showToast("Please select atleast one image")
                }
                else {
                    lytUpsellDetails.gone()
                    lytAddTrailerModel.show()
                    txtTitle.text="LINK TRAILER MODEL"
                    txtSubtitle.text="Add new item"

                    addUpSellItem.text="UPDATE UPSELL"
                }
            }
            else {
                if (Utils.isNetworkAvailable(this)) {
                    callAddUpSellItem()
                } else {
                    showToast("No Internet Connectivity!")
                }
            }

        }

        minusQuantity.setOnClickListener {
            if (txtQuantity.text.toString().toInt() != 0) {
                txtQuantity.text = txtQuantity.text.toString().toInt().minus(1).toString()
            }
        }

        plusQuantity.setOnClickListener {
            if (txtQuantity.text.toString().toInt() >= 0) {
                txtQuantity.text = txtQuantity.text.toString().toInt().plus(1).toString()
            }
        }
        spnrUpsell.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                upsellItem=upsellList.get(i)
                upSellItemDescription.setText(upsellItem!!.description)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }
        if(Utils.isNetworkAvailable(context)) {
            getUpsellTypEList()
        }
        else{
            showToast("No Internet Connectivity!")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        when {
            lytUpsellDetails.visibility == View.VISIBLE -> {

                super.onBackPressed()
            }
            lytAddTrailerModel.visibility == View.VISIBLE -> {
                lytAddTrailerModel.gone()
                lytUpsellDetails.show()
                if(intent.getStringExtra("flag")=="edit"){
                    txtTitle.text="Edit Upsell Item"
                    txtSubtitle.text="Edit Upsell Item"
                }
                else{
                    txtTitle.text="Add Upsell Item"
                    txtSubtitle.text="Add New Upsell Item"
                }
                addUpSellItem.text="NEXT: LINK TRAILER MODEL"

            }


        }
    }
    /*get upsell item added by admin*/
    var upsellList = listOf<UpsellItemsByAdmin>()
    private fun getUpsellTypEList() {

        showProgressDialog()
        CallApi().getUpsellByAdmin(ConstantApi.LIVE_BASE_URL + "licensee/upsellitems/admin")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<UpSellItemByAdminResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: UpSellItemByAdminResponse) {
                    if (response != null && response.success) {
                        upsellList=response.upsellItemsList
                        spnrUpsell.adapter=UpsellByAdminAdapter(response.upsellItemsList,context,"")

                        if(Utils.isNetworkAvailable(context)){
                            /*get added trailers*/
                            getFeaturedTrailer()

                        }

                        dismissProgressDialog()

                    } else
                        showToast(safeText(response.message))
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                }
            })

    }

    /**
     * get web services api call
     */
    lateinit var adapter: MyAllTrailerAdapter
    var mTrailerList :List<Trailers> = ArrayList<Trailers>()
    var webService: Api = ServiceGenerator.getApi()
    private fun getFeaturedTrailer() {

        val requestMap = HashMap<String, String>()
        requestMap["count"] = "10"
        requestMap["skip"] = "0"

        val getAllTrailerCall: Call<HomeResponse> =webService.getAllTrailers(ConstantApi.LIVE_BASE_URL + "licensee/trailers?",requestMap)

        getAllTrailerCall.enqueue(object : Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>
            ) {

                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.trailersList != null && response.body()!!.trailersList.isNotEmpty()) {
                            if(response.body()!!.trailersList.size>0) {
                                trailerModelRecyclerView.visible()
                                txtNoTrailerModelAdded.gone()

                                var noRepeat: List<Trailers> = ArrayList<Trailers>()
                                noRepeat=getUniquesElement(response.body()!!.trailersList)
                                mTrailerList=noRepeat
                                upsellingAddTrailerAdapter.notifyDataSetChanged(mTrailerList)

                            }
                            else{
                                trailerModelRecyclerView.gone()
                                txtNoTrailerModelAdded.visible()
                            }
                            if(intent.getStringExtra("flag")=="edit"){
                                getRequestedUpsellItemDetails()
                            }
                        }  else{
                            trailerModelRecyclerView.gone()
                            txtNoTrailerModelAdded.visible()
                        }
                    } else {
                        showToast("Something went wrong ...")
                    }
                } else {
                    showToast("Something went wrong ...")
                    onBackPressed()
                }
            }

            override fun onFailure(
                call: Call<HomeResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }



    /*get upsell item details to edit*/
    var url:String=""
    var spnrPosition=0
    private fun getRequestedUpsellItemDetails() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)

        url= ConstantApi.LIVE_BASE_URL+"licensee/upsellitem?"

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization",HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url+"id=$upsellId")
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
                    if (url.equals( ConstantApi.LIVE_BASE_URL+"licensee/upsellitem?"+"id=$upsellId", ignoreCase = true)) {
                        if (`object` != null) {

                            if (`object`.getBoolean("success")) {

                                val upsellItemObj=`object`.getJSONObject("upsellItemObj")

                                txtQuantity.text =
                                    BaseFragment.safeText("" + upsellItemObj.getString("quantity"))

                                for(i in 0 until upsellList.size){
                                    if(upsellList[i]._id==upsellItemObj.getString("adminRentalItemId")){
                                        spnrPosition=i
                                    }
                                }
                                spnrUpsell.setSelection(spnrPosition)

                                /*set trailer */
                                if(upsellItemObj.has("trailerModel")){
                                    for(j in 0 until mTrailerList.size){
                                            if (mTrailerList.get(j).trailerModel == upsellItemObj.getString("trailerModel")){
                                                mTrailerList.get(j).flag=true
                                            }
                                    }
                                    upsellingAddTrailerAdapter.notifyDataSetChanged(mTrailerList)
                                }

                                try {
                                   // upSellItemImageBitmapList.clear()
                                   if(upsellItemObj.getJSONArray("photos").length()>0){

                                       Picasso.get().load(upsellItemObj.getJSONArray("photos").getJSONObject(0).getString("data").toString()).into(upSellItemPreviewImage);

                                   }
                                    loadImgFromServer(upsellItemObj.getJSONArray("photos"))

                                } catch (e: IOException) {
                                    System.out.println(e)
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

    var tempImgArr=ArrayList<ImageView>()
    private fun loadImgFromServer(jsonArray: JSONArray) {

        upsellPreviewImagesLL?.show()
        upsellPreviewImagesScrollViewL?.show()
        upsellPreviewImagesLL?.removeAllViews()
        tempImgArr.clear()
       // upSellItemImageBitmapList.clear()

        for (i in 0 until jsonArray.length()) {

            val view = layoutInflater.inflate(R.layout.up_sell_item_image_layout, null, false)
            val previewImage = view.findViewById<CircleImageView>(R.id.upSellItemPreviewImage)
            HelperMethods.downloadImage(
                jsonArray.getJSONObject(i).getString("data").toString(),
                context,
                previewImage
            )
            /*imageLoadr?.DisplayImage(
                jsonArray.get(i).toString(),
                this,
                previewImage)*/
            //Picasso.get().load(jsonArray.get(i).toString()).into(previewImage);
            tempImgArr.add(previewImage)
            //previewImage.setTag(imgIndex)
            upsellPreviewImagesLL?.addView(view)
        }

        val view: View = layoutInflater.inflate(R.layout.item_image_layout_addbutton, null, false)
        val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
        rTrailerPreviewImage.setOnClickListener {
            showImageSelectionPopUp()
        }
        upsellPreviewImagesLL?.addView(view)
        imgIndex++
    }


    private fun <T> ObservableSource<T>.subscribe(disposableObserver: DisposableObserver<UpSellItemByAdminResponse>) {

    }
    private fun callAddUpSellItem() {
        showProgressDialog()
        var jsonObject = JsonObject()

        if(intent.getStringExtra("flag")=="edit") {
            jsonObject.addProperty("_id", upsellId)
        }
        jsonObject.addProperty("adminRentalItemId", upsellItem!!._id)
        jsonObject.addProperty("name", upsellItem!!.name)
        jsonObject.addProperty("description", upsellItem!!.description)
        jsonObject.addProperty("availability", upsellItem!!.avilability)
        jsonObject.addProperty("quantity",txtQuantity.text.toString())
        jsonObject.addProperty("type",upsellItem!!.type)

        if(mTrailerList.size>0) {
            val tempUpsellItem = mTrailerList
            var upSellArr = JsonArray()
            var trailerModel=""
            for (item in tempUpsellItem) {
                if (item.flag) {
                    upSellArr.add(item._id)
                    trailerModel=item.trailerModel
                    break
                }
            }
            jsonObject.addProperty("trailerModel", trailerModel)
        }

        var photos = arrayListOf<MultipartBody.Part>()
        for (index in 0 until tempImgArr.size) {
            val bitmap: Bitmap =
                (tempImgArr.get(index).getDrawable() as BitmapDrawable).getBitmap()
            //tempBitmap=bitmap
            val file = Utils.convertBitmapToFile("temp.png",compressImageBitmap(bitmap),context)
            val photoBody: RequestBody? =
                file?.let { it.asRequestBody("image/jpg".toMediaTypeOrNull()) }
            photoBody?.let {
                MultipartBody.Part.createFormData(
                    "photos", file?.name, it
                )
            }?.let { photos.add(it) }
        }
        val requestBodyAllInfo: RequestBody =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        CallApi().addUpSellItem(requestBodyAllInfo,photos)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        dismissProgressDialog()
                    }

                    showToast("Upsell item updated successfully!")
                    HelperMethods.closeEverythingOpenHome(context)
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                    showMenuOption(upSellItemPreviewImage)
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
                showMenuOption(upSellItemPreviewImage)
            } else
                showToast("Permission Denied")
        }
    }

    private fun showMenuOption(view: View) {
        val popupMenu = PopupMenu(this@AddUpSellItemActivity, view)
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
                        EZPhotoPick.startPhotoPickActivity(this@AddUpSellItemActivity, config)
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
                        EZPhotoPick.startPhotoPickActivity(this@AddUpSellItemActivity, config)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            false
        }
        popupMenu.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(this@AddUpSellItemActivity)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                    var photoName = data!!.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
                    var photoPath = storage.getAbsolutePathOfStoredPhoto(Constants.APP_STORAGE_DIR, photoName);
                    upSellItemPreviewImage.setImageBitmap(pickedPhotoBitmap)
                    upSellItemImageBitmapList.add(pickedPhotoBitmap)
                   // upSellItemImageList.add(compressImageBitmap(pickedPhotoBitmap))
                    loadUpSellItemPreviewImages(pickedPhotoBitmap)
                    showToast("Image Picked")
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection canceled")
        }
    }

    var imgIndex = 0

     fun loadUpSellItemPreviewImages(imageBitmap: Bitmap) {
        // upSellItemImageBitmapList.clear()
         if(intent.getStringExtra("flag")=="edit") {

             if (!tempImgArr.isEmpty()) {
                 for (j in 0 until tempImgArr.size) {
                     val bitmap: Bitmap =
                         (tempImgArr.get(j).getDrawable() as BitmapDrawable).getBitmap()
                     upSellItemImageBitmapList.add(bitmap)
                 }
             }
         }

        upsellPreviewImagesLL?.show()
        upsellPreviewImagesScrollViewL?.show()
        upsellPreviewImagesLL?.removeAllViews()
        tempImgArr.clear()

        for (i in upSellItemImageBitmapList.indices) {
            val view = layoutInflater.inflate(R.layout.up_sell_item_image_layout, null, false)
            val previewImage = view.findViewById<CircleImageView>(R.id.upSellItemPreviewImage)
            previewImage.setImageBitmap(upSellItemImageBitmapList.get(i))
            tempImgArr.add(previewImage)
            previewImage.setTag(imgIndex)
            upsellPreviewImagesLL?.addView(view)
        }

        val view: View = layoutInflater.inflate(R.layout.item_image_layout_addbutton, null, false)
        val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
        rTrailerPreviewImage.setOnClickListener {
            showImageSelectionPopUp()
        }
        upsellPreviewImagesLL?.addView(view)
        imgIndex++
    }

    private fun showImageSelectionPopUp() {
        if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                showMenuOption(upSellItemPreviewImage)
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

    override fun handlerResponse(t: TrailerDetailsResp) {
    }
}
