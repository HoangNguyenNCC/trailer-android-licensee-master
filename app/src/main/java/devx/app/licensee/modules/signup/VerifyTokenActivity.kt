package devx.app.licensee.modules.signup

import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.CallApi
import devx.app.seller.modules.signup.SignUpActivity
import devx.app.seller.modules.signup.SignUpActivity.Companion.additionalDocumentFile
import devx.app.seller.modules.signup.SignUpActivity.Companion.additionalDocumentselectedFileData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_token.*
import okhttp3.MediaType
import devx.app.seller.webapi.response.CommonResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class VerifyTokenActivity : BaseActivity() {

    private var mobileNumber = ""
    private var email = ""
    private var isLoginFlow = false

    var employeeDetails=JsonObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_token)

        //isLoginFlow = intent.getBooleanExtra(IntentParams.IS_LOGIN_FLOW, false)

        /*if(Utils.isNetworkAvailable(this)) {
            sendOTPForLogin(mobileNumber, email)
        }
        else
            showToast("No Internet Connectivity!")*/

        imgBack.setOnClickListener { finish() }
        verifyToken.setOnClickListener {

            if(edtToken.text.toString().isEmpty()){

                showToast("Please enter TOKEN")

            }
            else{if(Utils.isNetworkAvailable(this)) {
                verifyToken(edtToken.text.toString())
            }
            else
                showToast("No Internet Connectivity!")
            }
        }


    }


    var partProfilePic: MultipartBody.Part? = null
    var partAdditionalDocument: MultipartBody.Part? = null
    var partLicensePic: MultipartBody.Part? = null
    private fun verifyToken(otp: String) {
        showProgressDialog()
        var jsonObject = JsonObject()
        jsonObject=SignUpActivity.employeeObj
        jsonObject.addProperty("token",edtToken.text.toString())
        val requestBodyAllInfo: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),jsonObject.toString())

        if (SignUpActivity.profilePicData != null) {
            val file = Utils.convertBitmapToFile("temp.png", SignUpActivity.profilePicBitmap,context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.asRequestBody("image/jpg".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            partProfilePic =
                requestFile?.let {
                    MultipartBody.Part.createFormData("employeePhoto", file.name,
                        it
                    )
                }
        }
        if (SignUpBottomSheetFragment.photoSelectedBitmap!= null) {
            val file=Utils.convertBitmapToFile("temp.png",
                SignUpBottomSheetFragment.photoSelectedBitmap!!,context)
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.asRequestBody("image/jpg".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            partLicensePic = requestFile?.let {
                MultipartBody.Part.createFormData("employeeDriverLicenseScan", file.name,
                    it
                )
            }
        }
        if (additionalDocumentselectedFileData != null) {
            val file = additionalDocumentFile
            // create RequestBody instance from file
            val requestFile: RequestBody? =
                file?.asRequestBody("application/pdf".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            partAdditionalDocument = requestFile?.let {
                MultipartBody.Part.createFormData("employeeAdditionalDocumentScan",
                    file.name, it
                )
            }

        }
        CallApi().employeeInviteAccept(requestBodyAllInfo,partLicensePic,partAdditionalDocument,partProfilePic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null ) {
                        showToast("TOKEN Verified Successfully")

                        SignUpBottomSheetFragment.photoSelectedBitmap=null
                        verifyTokenLayout.gone()
                       // successLayout.show()
                        var inttt= Intent(context, VerifyOTPActivity::class.java)
                        inttt.putExtra(IntentParams.MOBILE_NUMBER, SignUpActivity.employeeObj.get("mobile").asString)
                        inttt.putExtra(IntentParams.EMAIL, SignUpActivity.employeeObj.get("email").asString)
                        inttt.putExtra("user", "employee")
                        startActivity(inttt)
                        finish()
                    } else
                        showToast(response.message)
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    dismissProgressDialog()
                    handleError(e)
                }
            })
    }
    /*
    private fun sendOTPForLogin(mobileNumber: String, email: String) {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("mobile", mobileNumber)
        jsonObject.addProperty("country", "india")
        jsonObject.addProperty("user", "licensee")
        CallApi().sendOTPForLogin(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null) {
                        showToast("OTP sent to $mobileNumber")
                    } else
                        showToast(response.message)
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    dismissProgressDialog()
                    handleError(e)
                }
            })
    }*/
}
