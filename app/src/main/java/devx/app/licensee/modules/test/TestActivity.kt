package devx.app.licensee.modules.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.widget.PopupMenu
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPick
import devx.app.licensee.modules.ezphotopicker.api.EZPhotoPickStorage
import kotlinx.android.synthetic.main.activity_test.*
import java.io.ByteArrayOutputStream

class TestActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        pickPhoto.setOnClickListener {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMenuOption()
            } else
                PermissionUtils.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PermissionUtils.READ_EXTERNAL_STORAGE
                )
        }
    }

    private fun showMenuOption() {
        val popupMenu: PopupMenu?
        popupMenu = PopupMenu(this@TestActivity, pickPhoto!!)
        popupMenu.inflate(R.menu.menu_item)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
//                camera_selection -> {
//                    try {
//                        val config = EZPhotoPickConfig()
//                        config.photoSource = PhotoSource.CAMERA // or PhotoSource.CAMERA
//                        config.isAllowMultipleSelect = false // only for GALLERY pick and API >18
//                        config.exportingSize = 1000
//                        config.exportingThumbSize = 200
//                        config.needToExportThumbnail = true
//                        config.needToAddToGallery = true
//                        config.needToRotateByExif = true
//                        config.isGenerateUniqueName = true
//                        config.storageDir = "TrailerApp"
//                        EZPhotoPick.startPhotoPickActivity(this@TestActivity, config)
//                    } catch (e: Exception) {
//                    }
//                }
//
//                gallery_selection -> {
//                    try {
//                        val config = EZPhotoPickConfig()
//                        config.photoSource = PhotoSource.GALLERY // or PhotoSource.CAMERA
//                        config.isAllowMultipleSelect = true // only for GALLERY pick and API >18
//                        config.exportingSize = 1000
//                        config.exportingThumbSize = 200
//                        config.needToExportThumbnail = true
//                        config.needToAddToGallery = true
//                        config.isAllowMultipleSelect = false
//                        config.needToRotateByExif = true
//                        config.isGenerateUniqueName = true
//                        config.storageDir = "TrailerApp"
//                        EZPhotoPick.startPhotoPickActivity(this@TestActivity, config)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
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
                showMenuOption()
            } else
                showToast("Permission Denied")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            try {
                val pickedPhotoBitmap: Bitmap
                val storage = EZPhotoPickStorage(this@TestActivity)
                pickedPhotoBitmap = storage.loadLatestStoredPhotoBitmapThumbnail()
                compressImageBitmap(pickedPhotoBitmap)
                showToast("Image Picked")
            } catch (ee: java.lang.Exception) {
            }
        }
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
            "NA"
        }
    }
}
