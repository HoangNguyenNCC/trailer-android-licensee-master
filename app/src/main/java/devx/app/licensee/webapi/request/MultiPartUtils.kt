package devx.app.licensee.webapi.request

import devx.app.licensee.webapi.CallApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MultiPartUtils {

    fun doMUp(file : File) {
        //pass it like this
//        var file = File("/storage/emulated/0/Download/Corrections 6.jpg");
        var requestFile =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull());

// MultipartBody.Part is used to send also the actual file name
        var body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

// add another part within the multipart request
        var fullName =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "Your Name");

//        CallApi().updateProfile(id, fullName, body, other);

    }

}