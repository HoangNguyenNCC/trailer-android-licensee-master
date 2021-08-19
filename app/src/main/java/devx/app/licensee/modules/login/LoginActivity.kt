package devx.app.seller.modules.login

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.licensee.common.AnimUtils
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.login.forgotPassword.ForgotPasswordActivity
import devx.app.licensee.modules.signup.VerifyOTPActivity
import devx.app.licensee.webapi.APIError
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ErrorUtils
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.login.LoginResponse
import devx.app.seller.modules.home.hometab.HomeActivity
import devx.app.seller.modules.signup.SignUpActivity
import devx.app.seller.webapi.response.CommonResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException


class LoginActivity : BaseActivity() {

    var intentSignalSender: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        login.setOnClickListener {
            if (false) {
                startActivity(Intent(this, HomeActivity::class.java))
                return@setOnClickListener
            }
            if(Utils.isNetworkAvailable(context)) {
                if (emailIdET.text.toString().isNotEmpty() && passwordET.text.toString().isNotEmpty()&&HelperMethods.isEmailValid(emailIdET.text.toString())) {

                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(TAG, "getInstanceId failed", task.exception)

                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token

                            val loginJsonObject = JsonObject()
                            loginJsonObject.addProperty("email", emailIdET.text.toString())
                            loginJsonObject.addProperty("password", passwordET.text.toString())
                            loginJsonObject.addProperty("fcmDeviceToken", "" + token)
                                signin(
                                    emailIdET.text.toString(),
                                    passwordET.text.toString(),
                                    "" + token
                                )

                        })

                } else
                    showToast("Enter email or password")

            }
            else{
                showToast("No Internet Connectivity!")
            }


        }

        forgotPassword.setOnClickListener {
            AnimUtils.panWithCallback(forgotPassword, object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }

        createAccount.setOnClickListener {
            AnimUtils.panWithCallback(createAccount, object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        /*if (!isMyServiceRunning(SignalSenderService::class.java)) {
            intentSignalSender = Intent(context, SignalSenderService::class.java)
            startService(intentSignalSender)
        }*/

    }

    val responseMap= mutableMapOf<String,ArrayList<String>>() as HashMap

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun signin(email: String, password: String, fcmToken: String) {

        val mAQuery=AQuery(this)
        val mProgress=TransparentProgressDialog(this,R.drawable.ic_loader)
        var url:String=ConstantApi.LIVE_BASE_URL+"employee/signin"

        try {
            val jsonObject = JSONObject()
            jsonObject.put("email", email)
            jsonObject.put("password", password )
            jsonObject.put("fcmDeviceToken", "" + fcmToken)

            /*val signinApiCall: Call<LoginResponse> = webService.singinApi(jsonObject)

            showProgressDialog()

            signinApiCall.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    dismissProgressDialog()
                    if (response.isSuccessful()) {
                        if (response.body()!!.success) {

                            if(response.body()!!.employeeObj.isOwner){
                                HelperMethods.setPersonUserName(response.body()!!.licenseeObj.name)
                            }
                            else{
                                HelperMethods.setPersonUserName(response.body()!!.employeeObj.name)
                            }
                            HelperMethods.setUserId(response.body()!!.employeeObj._id)
                            HelperMethods.setAuthorizationToken(response.body()!!.token)
                            HelperMethods.setIsOwner(response.body()!!.employeeObj.isOwner)
                            HelperMethods.setUserLatitude(context,
                                response.body()!!.employeeObj.address.location!!.coordinates!![0].toString())
                            HelperMethods.setUserLongitude(context,
                                response.body()!!.employeeObj.address.location!!.coordinates!![1].toString())

                            val keys: Iterator<*> = response.body()!!.employeeObj.acl.iterator()
                            while (keys.hasNext()) {
                                // loop to get the dynamic key
                                val currentDynamicKey = keys.next() as String
                                // get the value of the dynamic key
                                val currentDynamicValue: ArrayList<String> =
                                    response.body()!!.employeeObj.acl.getValue(
                                        currentDynamicKey
                                    )
                                // do something here with the value...
                                val jsonArrayACL = currentDynamicValue
                                val appointmentsSize = jsonArrayACL.size
                                var tempArr = ArrayList<String>()
                                for (count in 0 until appointmentsSize) {
                                    val objectData = jsonArrayACL[count]
                                    tempArr.add(objectData.toString())
                                }
                                responseMap.put(currentDynamicKey, tempArr)
                            }

                            HelperMethods.setACL(responseMap)
                            showToast("Logged in Successfully")


                            HelperMethods.setIsUserLogin(true)
                            HelperMethods.closeEverythingOpenHome(this@LoginActivity)
                        } else {
                            showToast("Something went wrong ...")
                        }
                    } else {
                        ///handling bad request 400 response
                        val apiError: APIError = ErrorUtils.parseError(response)
                        showToast(apiError.message())
                    }
                    dismissProgressDialog()
                }

                override fun onFailure(
                    call: Call<LoginResponse>,
                    t: Throwable
                ) {
                    dismissProgressDialog()
                }
            })*/
            mAQuery.progress(mProgress).post(url, jsonObject, JSONObject::class.java, object : AjaxCallback<JSONObject?>() {

                override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {
                    super.callback(url, `object`, status)

                    if (status != null) {
                        try {
                        if (status.code==200) {
                                if (url.equals(ConstantApi.LIVE_BASE_URL+"employee/signin", ignoreCase = true)) {
                                    if (`object` != null) {
                                        if (`object`.getBoolean("success")) {
                                            //val jsonArray = `object`.getJSONArray("result")
                                            val employeeObject=`object`.getJSONObject("employeeObj")
                                            val licenseeObj=`object`.getJSONObject("licenseeObj")
                                            if(employeeObject.getBoolean("isOwner")){
                                                HelperMethods.setPersonUserName(licenseeObj.getString("name"))
                                            }
                                            else{
                                                HelperMethods.setPersonUserName(employeeObject.getString("name"))
                                            }
                                            HelperMethods.setUserId(employeeObject.getString("_id"))
                                            HelperMethods.setAuthorizationToken(`object`.getString("token"))
                                            HelperMethods.setIsOwner(employeeObject.getBoolean("isOwner"))
                                            HelperMethods.setUserLatitude(context,employeeObject.getJSONObject("address").getJSONObject("location").getJSONArray("coordinates").get(0).toString())
                                            HelperMethods.setUserLongitude(context,employeeObject.getJSONObject("address").getJSONObject("location").getJSONArray("coordinates").get(1).toString())

                                            val accessControlList =
                                                `employeeObject`.getJSONObject("acl")

                                            val keys: Iterator<*> = accessControlList.keys()
                                            while (keys.hasNext()) {
                                                // loop to get the dynamic key
                                                val currentDynamicKey = keys.next() as String
                                                // get the value of the dynamic key
                                                val currentDynamicValue: JSONArray =
                                                    accessControlList.getJSONArray(
                                                        currentDynamicKey
                                                    )
                                                // do something here with the value...
                                                val jsonArrayACL = currentDynamicValue
                                                val appointmentsSize = jsonArrayACL.length()
                                                var tempArr = ArrayList<String>()
                                                for (count in 0 until appointmentsSize) {
                                                    val objectData = jsonArrayACL[count]
                                                    tempArr.add(objectData.toString())
                                                }
                                                responseMap.put(currentDynamicKey, tempArr)
                                            }

                                            HelperMethods.setACL(responseMap)

                                            showToast("Logged in Successfully")
                                            /*if (!employeeObject.getBoolean("isMobileVerified")) {
                                                var verr =
                                                    Intent(context, VerifyOTPActivity::class.java)
                                                verr.putExtra(IntentParams.IS_LOGIN_FLOW, true)
                                                verr.putExtra(
                                                    IntentParams.MOBILE_NUMBER,employeeObject.getString("mobile")
                                                )
                                                verr.putExtra(
                                                    IntentParams.EMAIL,
                                                    employeeObject.getString("email")
                                                )
                                                verr.putExtra(
                                                        "user",
                                                "employee"
                                                )
                                                startActivity(verr)
                                                return
                                            }*/
                                                HelperMethods.setIsUserLogin(true)
                                                HelperMethods.closeEverythingOpenHome(this@LoginActivity)

                                        } else {
                                            showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                                        }
                                    }
                                }

                        } else {

                            showToast(HelperMethods.safeText("Please enter valid credentials", ""))
                        }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
