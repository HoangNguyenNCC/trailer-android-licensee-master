package devx.app.licensee.modules.home.requesttab

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import devx.app.licensee.R
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.modules.signup.SelectDocumentBottomSheet
import kotlinx.android.synthetic.main.sheet_driver_license.*
import kotlinx.android.synthetic.main.tracking_license_picture.*

class StartReturnBottomSheetFragment(
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
        return inflater.inflate(R.layout.tracking_license_picture, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtCloseBottomSheet.setOnClickListener {
            (activity as RequestApproveActivity).dismissBottomSheet()
        }
        txtAddLicense.setOnClickListener {


            if(photoSelectedBitmap==null){
                Toast.makeText(context!!,"Please pick customer driving license",Toast.LENGTH_LONG).show()
            }
            else{
                (activity as RequestApproveActivity).dismissBottomSheet()
                (activity as RequestApproveActivity).addLicenseToserver()
            }
        }
        imgLicense.setOnClickListener {

            val bottomSheet = SelectDocumentBottomSheet("return")
            bottomSheet.isCancelable = true
            bottomSheet.setContext(this)
            bottomSheet.show(fragmentManager!!, bottomSheet.tag)
        }

    }
    fun setScanPhotoAdded() {
        imgLicense.setImageBitmap(photoSelectedBitmap)
        //scanImageTv.text = "Photo added"
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
                    //photoSelectedBitmap=pickedPhotoBitmap
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
}