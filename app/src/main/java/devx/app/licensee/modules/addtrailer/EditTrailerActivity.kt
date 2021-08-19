package devx.app.licensee.modules.addtrailer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.TrailerApplication
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp
import kotlinx.android.synthetic.main.activity_add_trailer.aAgeET
import kotlinx.android.synthetic.main.activity_add_trailer.aCapacityET
import kotlinx.android.synthetic.main.activity_add_trailer.aDescriptionET
import kotlinx.android.synthetic.main.activity_add_trailer.aSizeET
import kotlinx.android.synthetic.main.activity_add_trailer.aTypeTv
import kotlinx.android.synthetic.main.activity_add_trailer.aVinNumberET
import kotlinx.android.synthetic.main.activity_add_trailer.rRentButton
import kotlinx.android.synthetic.main.activity_add_trailer.rTrailerPreview
import kotlinx.android.synthetic.main.activity_edit_trailer.*
import org.json.JSONObject

class EditTrailerActivity : BaseActivity(), AddTrailerContract.View {

   // private lateinit var presenterAdd: AddTrailerPresenter
    private var base64TrailerImage = arrayListOf<String>()
    var rTrailerPreviewImagesLL: LinearLayout? = null
    private var trailerDetailsResponse: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trailer)
        CommonTopbarView().setup(this, "")

        trailerDetailsResponse = TrailerApplication.trailerDetailsResponse
        setUpUI()

        rTrailerPreviewImagesLL = findViewById(R.id.rTrailerPreviewImagesLL)
        rTrailerPreviewImagesLL?.gone()

       // presenterAdd = AddTrailerPresenter()
       // presenterAdd.attachView(this, lifecycle)

        rTrailerPreview.setOnClickListener {
            pickImage(rTrailerPreview)
        }

        rRentButton.setOnClickListener {
            callAddTrailerAPI()
        }
    }

    private fun setUpUI() {
        if (trailerDetailsResponse == null) return

        /*val trailerData = trailerDetailsResponse?.trailerObj
        HelperMethods.downloadImage(trailerData?.photos?.get(0), context, rTrailerPreview)
        rTrailerName.setText(trailerData?.name)
        aVinNumberET.setText(trailerData?.vin)
        aSizeET.setText(trailerData?.size)
        aCapacityET.setText(trailerData?.capacity)
        aDescriptionET.setText(trailerData?.description)
        aTypeET.setText(trailerData?.type)
        aLocationET.setText(trailerData?.licensee)
        aAgeET.setText(trailerData?.age.toString())

        if (trailerData?.availability!!) aAvailabilityET.setText("ACTIVE") else aAvailabilityET.setText(
            "INACTIVE"
        )*/
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

    private fun callAddTrailerAPI() {

        var name = rTrailerName.text.toString()
        if (name.isEmpty()) {
            showToast("Enter name")
            return
        }

        var vin = aVinNumberET.text.toString()
        val type = aTypeTv.text.toString()
        val description = aDescriptionET.text.toString()
        if (description.isEmpty()) {
            showToast("Enter description")
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
        val size = aSizeET.text.toString()
        val capacity = aCapacityET.text.toString()
        val tare = "tare"
        var age = aAgeET.text.toString()
        if (age.isEmpty()) age = "1"
        val features = arrayListOf<String>()
        var availability = true
        if (vin.isEmpty()) vin = "123"

        var dlrCharges = "304"

        var jsonObject = JsonObject()
        jsonObject.addProperty("name", name)
        var jsonArray = JsonArray()
        for (im in base64TrailerImage) {
            jsonArray.add(im)
        }
        jsonObject.add("photos", jsonArray)
        jsonObject.addProperty("vin", vin)
        jsonObject.addProperty("type", type)
        jsonObject.addProperty("description", description)
        jsonObject.addProperty("size", size)
        jsonObject.addProperty("capacity", capacity)
        jsonObject.addProperty("tare", tare)
        jsonObject.addProperty("age", age)
        var featrray = JsonArray()
        for (featUrls in features) {
            jsonArray.add(featUrls)
        }
        jsonObject.add("features", featrray)
        jsonObject.addProperty("availability", availability)

        var rentalCharges = JsonObject()
        var pickup = JsonArray()
        var pickObj = JsonObject()
        pickObj.addProperty("duration", "21600000")
        pickObj.addProperty("charges", "123")
        pickup.add(pickObj)
        rentalCharges.add("pickUp", pickup)

        var door = JsonArray()
        var doorObj = JsonObject()
        doorObj.addProperty("duration", "21600000")
        doorObj.addProperty("charges", "148")
        door.add(doorObj)
        rentalCharges.add("door2Door", door)

        jsonObject.add("rentalCharges", rentalCharges)
        jsonObject.addProperty("dlrCharges", dlrCharges)
        jsonObject.addProperty("licenseeId", HelperMethods.getUserId())

        showProgressDialog()
        /*CallApi().addTrailer(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    dismissProgressDialog()
                    if (response != null && response.success) {
                        showToast("Successfully uploaded Trailer..")
                        val intent =
                            Intent(this@EditTrailerActivity, SelectUpSellItemActivity::class.java)
                        intent.putExtra(IntentParams.IS_ADD_INSURANCE, true)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                    handleError(e)
                    val intent =
                        Intent(this@EditTrailerActivity, SelectUpSellItemActivity::class.java)
                    intent.putExtra(IntentParams.IS_ADD_INSURANCE, true)
                    startActivity(intent)
                }
            })*/
    }

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
                    var photoPath =
                        storage.getAbsolutePathOfStoredPhoto(Constants.APP_STORAGE_DIR, photoName);
                    var photoSelected = HelperMethods.getBase64FromBitmap(pickedPhotoBitmap)
                    if (imgIndex == 0)
                        rTrailerPreview.setImageBitmap(pickedPhotoBitmap)
                    addImageView(photoPath)
                    base64TrailerImage.add(photoSelected)


                    showToast("Image Picked")
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                showToast("Image selection canceled")
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

    override fun handlerResponse(t: TrailerDetailsResp) {
    }

    var imgIndex = 0
    private fun addImageView(imagePath: String) {
        rTrailerPreviewImagesLL?.show()
        rTrailerPreviewImagesLL?.removeAllViews()
        for (i in base64TrailerImage.indices) {
            val view = layoutInflater.inflate(R.layout.item_image_layout, null, false)
            val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
            rTrailerPreviewImage.setImageBitmap(HelperMethods.getBitmapFromPath(imagePath))
            rTrailerPreviewImage.setTag(imgIndex)
            rTrailerPreviewImagesLL?.addView(view)
        }

        val view = layoutInflater.inflate(R.layout.item_image_layout_addbutton, null, false)
        val rTrailerPreviewImage = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
        rTrailerPreviewImage.setOnClickListener {
            showMenuOption(rTrailerPreviewImage)
        }
        rTrailerPreviewImagesLL?.addView(view)

        imgIndex++
    }

}
