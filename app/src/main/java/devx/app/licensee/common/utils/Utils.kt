package devx.app.licensee.common.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.ConnectivityManager
import java.io.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utils {
    companion object{
        fun convertBitmapToFile(fileName: String, bitmap: Bitmap,context:Context): File? {
            //create a file to write bitmap data

            //val file: File? = context.applicationContext.getExternalFilesDir(fileName)
            // Get the context wrapper
            val wrapper = ContextWrapper(context.applicationContext)

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
            file = File(file,"${UUID.randomUUID()}.jpg")

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos)
            val bitMapData = bos.toByteArray()

            //write the bytes in file
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                fos?.write(bitMapData)
                fos?.flush()
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }
        fun isNetworkAvailable(context:Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    public fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    }

}