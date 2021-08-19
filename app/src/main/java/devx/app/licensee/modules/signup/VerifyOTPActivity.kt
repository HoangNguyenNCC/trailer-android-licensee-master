package devx.app.licensee.modules.signup

import android.os.Bundle
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.CallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_verify_otp.*
import devx.app.seller.webapi.response.CommonResponse
class VerifyOTPActivity : BaseActivity() {

    private var mobileNumber = ""
    private var email = ""
    private var isLoginFlow = false
    private var isSignupFlow = false
    var userType="employee"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        mobileNumber = intent.getStringExtra(IntentParams.MOBILE_NUMBER).toString()
        email = intent.getStringExtra(IntentParams.EMAIL).toString()
        if(!intent.hasExtra("user"))
            userType= intent.getStringExtra("user").toString()
        if(intent.hasExtra(IntentParams.IS_LOGIN_FLOW))
            isLoginFlow = intent.getBooleanExtra(IntentParams.IS_LOGIN_FLOW, false)
        if(intent.hasExtra(IntentParams.IS_SIGNUP_FLOW))
            isSignupFlow = intent.getBooleanExtra(IntentParams.IS_SIGNUP_FLOW, false)

        if(Utils.isNetworkAvailable(this))
            sendOTPForLogin(mobileNumber, email)
        else
            showToast("No Internet Connectivity!")

        verifyOTP.setOnClickListener {
            val otp = otp_view.otp
            if(otp!!.length==4){

                if(Utils.isNetworkAvailable(this)) {
                    verifyOTP(otp)
                }
                else
                    showToast("No Internet Connectivity!")
            }
            else{
                showToast("Please enter OTP")
            }
        }

        resendOTP.setOnClickListener {
            if(Utils.isNetworkAvailable(this)) {
                sendOTPForLogin(mobileNumber, email)
            }
            else
                showToast("No Internet Connectivity!")
        }
    }

    private fun sendOTPForLogin(mobileNumber: String, email: String) {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("mobile",mobileNumber)
        jsonObject.addProperty("country", "india")
        jsonObject.addProperty("user", userType)
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
                    showToast(e.message)

                }
            })
    }

    private fun verifyOTP(otp: String) {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("mobile", mobileNumber)
        jsonObject.addProperty("country", "india")
        jsonObject.addProperty("otp", otp)
        if(userType!="employee") {
            jsonObject.addProperty("user", userType)
            jsonObject.addProperty("testMode", "true")
        }
        CallApi().verifyOTP(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null ) {
                        showToast("OTP Verified Successfully")
                        verifyOtpLayout.gone()
                       // successLayout.show()

                        if(isLoginFlow){
                            HelperMethods.setIsUserLogin(true)
                            HelperMethods.closeEverythingOpenHome(context)
                        }
                        else if(isSignupFlow){
                            HelperMethods.closeEverythingOpenLogin(context)
                        }
                        else{
                            onBackPressed()
                        }
                    } else
                        showToast(response.message)
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    dismissProgressDialog()
                    showToast(e.message)

                }
            })
    }
}
