package devx.app.licensee.modules.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import devx.app.licensee.modules.ezphotopicker.api.models.EZPhotoPickConfig
import devx.app.licensee.modules.ezphotopicker.api.models.PhotoSource
import devx.app.licensee.modules.home.requesttab.StartReturnBottomSheetFragment
import devx.app.licensee.modules.trailerTracking.TrackingLicenseBottomSheetFragment
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import kotlinx.android.synthetic.main.fragment_select_document_sheet.*

class SelectDocumentBottomSheet(flag: String) : BottomSheetDialogFragment() {

    private var bottomSheet: SignUpBottomSheetFragment? = null
    private var bottomSheetTracking: TrackingLicenseBottomSheetFragment? = null
    private var bottomSheetReturn: StartReturnBottomSheetFragment? = null
    var isPhotoAdded = false
    var flag1=flag

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        return inflater.inflate(R.layout.fragment_select_document_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionLayout.show()
        optionLayout1.gone()


        if(flag1=="signup") {
            txtSelectAFile.show()
        }
        else{
            txtSelectAFile.gone()
        }


        if (PermissionUtils.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(activity, Manifest.permission.CAMERA)) {
            } else
                PermissionUtils.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    PermissionUtils.CAMERA_ACCESS
                )
        } else
            PermissionUtils.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionUtils.WRITE_EXTERNAL_STORAGE
            )

        selectedImageView.setOnClickListener {
            showImageSelectionPopUp()
        }

        capture.setOnClickListener {
            pickFileFromCamera(true)
        }

        txtSelectAFile.setOnClickListener {
            pickFileFromCamera(false)
        }

        tryAgain.setOnClickListener {
            showImageSelectionPopUp()
        }

        confirmButton.setOnClickListener {
            if(flag1=="signup") {
                if (isPhotoAdded) {
                    bottomSheet?.setScanPhotoAdded()
                    HelperMethods.showToastbar(context, "File Selected Successfully")
                    dismiss()
                }
            }
            else if (flag1 == "return"){
                if (isPhotoAdded){
                    bottomSheetReturn?.setScanPhotoAdded()
                    HelperMethods.showToastbar(context, "File Selected Successfully")
                    dismiss()
                }
            }
            else{

                if (isPhotoAdded) {
                    bottomSheetTracking?.setScanPhotoAdded()
                    HelperMethods.showToastbar(context, "File Selected Successfully")
                    dismiss()
                }
            }
        }

        commonBack.setOnClickListener {
            dismiss()
        }
    }

    fun setContext(bottomSheet: SignUpBottomSheetFragment) {
        this.bottomSheet = bottomSheet
    }
    fun setContext(bottomSheet: TrackingLicenseBottomSheetFragment) {

        this.bottomSheetTracking = bottomSheet
    }
    fun setContext(bottomSheet: StartReturnBottomSheetFragment) {
        this.bottomSheetReturn = bottomSheet
    }
    private fun showImageSelectionPopUp() {
        if (PermissionUtils.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(activity, Manifest.permission.CAMERA)) {
                showMenuOption(selectedImageView!!)
            } else
                PermissionUtils.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    PermissionUtils.CAMERA_ACCESS
                )
        } else
            PermissionUtils.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PermissionUtils.WRITE_EXTERNAL_STORAGE
            )
    }

    private fun showMenuOption(view: View) {
        val popupMenu = PopupMenu(context!!, view)
        popupMenu.inflate(R.menu.menu_item)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.camera_selection -> {
                    try {
                        val config = EZPhotoPickConfig()
                        config.photoSource = PhotoSource.CAMERA
                        config.isAllowMultipleSelect = false
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

    private fun pickFileFromCamera(value: Boolean) {
        if (value) {
            try {
                val config = EZPhotoPickConfig()
                config.photoSource = PhotoSource.CAMERA
                config.isAllowMultipleSelect = false
                config.needToAddToGallery = true
                config.needToRotateByExif = true
                config.isGenerateUniqueName = true
                config.storageDir = "LicenseeApp"
                EZPhotoPick.startPhotoPickActivity(this, config)
            } catch (e: Exception) {
            }
        } else {
            try {
                val config = EZPhotoPickConfig()
                config.photoSource = PhotoSource.GALLERY
                config.isAllowMultipleSelect = true
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.hasPermissions(activity, Manifest.permission.CAMERA)) {
                    showMenuOption(selectedImageView!!)
                } else
                    PermissionUtils.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.CAMERA),
                        PermissionUtils.CAMERA_ACCESS
                    )
            } else
                HelperMethods.showToastbar(context, "Permission Denied")
        } else if (requestCode == PermissionUtils.CAMERA_ACCESS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.hasPermissions(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    showMenuOption(selectedImageView!!)
                } else
                    PermissionUtils.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionUtils.WRITE_EXTERNAL_STORAGE
                    )
            } else
                HelperMethods.showToastbar(context, "Permission Denied")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val pickedPhotoBitmap: Bitmap
                    val storage = EZPhotoPickStorage(context)
                    pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmap()
                    selectedImageView.setImageBitmap(pickedPhotoBitmap)

                    if(flag1=="signup") {
                        bottomSheet?.photoSelected =
                            HelperMethods.getBitmapInto64BaseEncoded(pickedPhotoBitmap)

                        SignUpBottomSheetFragment.photoSelectedBitmap = pickedPhotoBitmap
                        isPhotoAdded = true;
                    }
                    else{

                        bottomSheetTracking?.photoSelected =
                            HelperMethods.getBitmapInto64BaseEncoded(pickedPhotoBitmap)

                        TrackingLicenseBottomSheetFragment.photoSelectedBitmap = pickedPhotoBitmap
                        isPhotoAdded = true;
                    }

                    displayTitle.text = "Is the photo clear?"
                    displayText.text =
                        "The photo should clearly show the ID - nothing blurry or cut-off, and no glares."
                    optionLayout.gone()
                    optionLayout1.show()
                } catch (ee: java.lang.Exception) {
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                HelperMethods.showToastbar(context, "Image selection canceled")
        }
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }


}
