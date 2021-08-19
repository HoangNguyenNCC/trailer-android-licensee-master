package devx.app.licensee.modules.login.forgotPassword

import android.os.Bundle
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.seller.webapi.response.CommonResponse
import devx.app.licensee.common.AnimUtils
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.CallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONObject

class ForgotPasswordActivity : BaseActivity() {

    private var authToken = "NA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotPasswordButton.setOnClickListener {


            //sendForgotPasswordOTP(emailIdET.text.toString())

            if(Utils.isNetworkAvailable(this)) {
                if (forgotPasswordButton.text.toString() == "Send Reset Link") {
                    if (emailIdET.text.toString().isEmpty()) {
                        showToast("Enter email address")
                        AnimUtils.shake(emailIdET)
                        openKeyBoard(emailIdET)
                        return@setOnClickListener
                    }
                    if (!HelperMethods.isEmailValid(emailIdET.text.toString())) {
                        openKeyBoard(emailIdET)
                        emailIdET.requestFocus()
                        showToast("Enter valid email");
                        return@setOnClickListener
                    }
                    //sendForgotPasswordOTP(emailIdET.text.toString())
                    forgotPwdAPI(emailIdET.text.toString())
                } else if (forgotPasswordButton.text.toString() == "Reset Password") {
                    if (passwordET.text.length > 6 ) {
                        resetPassword()
                    }
                    else
                        showToast("Please enter password")

                }
            }
            else
                showToast("No Internet Connectivity!")

        }

        //textView2.text = "BACK TO LOGIN"
        txtLoginResend.setOnClickListener {
            if(Utils.isNetworkAvailable(this)) {
                if (txtLoginResend.text.toString() == "BACK TO LOGIN") {
                    finish()
                }
                else if(txtLoginResend.text.toString() == "Resend Email"){
                    forgotPwdAPI(emailIdET.text.toString())
                }
            }
        }

    }


    private fun forgotPwdAPI(email: String) {

        val mAQuery= AQuery(this)
        val mProgress= TransparentProgressDialog(this,R.drawable.ic_loader)
        var url:String= ConstantApi.LIVE_BASE_URL+"employee/forgotpassword"

        try {
            val jsonObject = JSONObject()
            jsonObject.put("email", email)
            jsonObject.put("platform", "android" )
            mAQuery.progress(mProgress).put(url, jsonObject, JSONObject::class.java, object : AjaxCallback<JSONObject?>() {

                override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {
                    super.callback(url, `object`, status)

                    if (status != null) {
                        if (status.code==200) {
                            try {
                                if (url.equals(ConstantApi.LIVE_BASE_URL+"employee/forgotpassword", ignoreCase = true)) {
                                    if (`object` != null) {
                                        if (`object`.getBoolean("success")) {
                                            showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                                            forgotPasswordButton.text= "Reset Password"
                                            resetPasswordLayout.show()
                                            emailLayout.gone()
                                            txtLoginResend.text = "Resend Email"

                                        } else {
                                            showToast(HelperMethods.safeText("Please enter valid email address", ""))
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            if(status.code==400){
                                showToast(HelperMethods.safeText("Please enter valid email address", ""))
                            }
                            else{
                                showToast(HelperMethods.safeText(resources.getString(R.string.err_try_again), ""))
                            }
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun resetPassword() {
        showProgressDialog()
        CallApi().resetPassword(tokenET.text.toString(), passwordET.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        showToast("Password reset Successfully!")
                        finish()
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
