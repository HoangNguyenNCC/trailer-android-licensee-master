package devx.app.licensee.modules.inviteEmployees

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.seller.webapi.response.CommonResponse
import devx.app.licensee.common.AnimUtils
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.*
import devx.app.licensee.webapi.response.accessControlList.AccessControlList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_invite_employees.*
import kotlinx.android.synthetic.main.activity_invite_employees.stateSpinner
import kotlinx.android.synthetic.main.sheet_driver_license.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InviteEmployeesActivity : BaseActivity() {

    private val accessControlList = arrayListOf<String>()
    var permissionBottomSheetFragment: PermissionBottomSheetFragment? = null
    var selectedPermissions = arrayListOf<String>()
    var tempSelectedpermissionList= mutableMapOf<String,ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_employees)

        setUpTypeListSpinner()
        addPermissionButton.setOnClickListener {
            showPermissionBottomSheet()
        }

        sendInviteButton.setOnClickListener {
            AnimUtils.panWithCallback(iconLayout, object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (!HelperMethods.isEmailValid(emailIdET.text.toString())) {
                        openKeyBoard(emailIdET)
                        emailIdET.requestFocus()
                        showToast("Enter valid email address");
                        return
                    }
                    if (selectedType.isEmpty() || selectedType == "" || selectedType == "Select Employee Type") {
                        HelperMethods.showToastbar(context, "Please select  employee type")
                        Handler().postDelayed({ showPermissionBottomSheet() }, 1000)
                        return
                    }
                    if (tempSelectedpermissionList.isEmpty()) {
                        HelperMethods.showToastbar(context, "Add permissions to proceed")
                        Handler().postDelayed({ showPermissionBottomSheet() }, 1000)
                        return
                    }


                    if(Utils.isNetworkAvailable(context)) {
                        sendEmployeeInvitation()
                    }
                    else
                        showToast("No Internet Connectivity!")
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }

        imgClose.setOnClickListener{
            HelperMethods.closeEverythingOpenHome(context)
        }
        if(Utils.isNetworkAvailable(context)) {
            getAccessControlList()
        }
        else
            showToast("No Internet Connectivity!")
    }

    private var selectedType = ""
    val typeList = arrayListOf<String>()
    private fun setUpTypeListSpinner() {
        typeList.clear()
        typeList.add("Select Employee Type")
        typeList.add("Employee")
        typeList.add("Representative")
        typeList.add("Director")
        typeList.add("Executive")

        val adapter =
            ArrayAdapter(context!!, R.layout.sgl_item_spnr_type, typeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = adapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                selectedType = typeList[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                selectedType = "Select Employee Type"
            }
        }
    }

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun sendEmployeeInvitation() {
        showProgressDialog()
        val tempJson=JsonObject()
        if(tempSelectedpermissionList.size>0){
            for((key,value) in tempSelectedpermissionList){
                val tempJArr=JsonArray()
                for(t in value) {
                    tempJArr.add(t)
                }
                tempJson.add(key,tempJArr)
            }
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("email", emailIdET.text.toString())
        jsonObject.add("acl",tempJson)
        jsonObject.addProperty("type", selectedType)

        val sendEmployeeInvitationCall: Call<CommonResponse> = webService.sendEmployeeInvitation(jsonObject)

        sendEmployeeInvitationCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast("Invitation send successfully!")
                        HelperMethods.closeEverythingOpenHome(context)

                    } else {
                        showToast("Something went wrong ...")
                        //return
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
                dismissProgressDialog()
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
       
    }

    fun dismissPermissionBottomSheet() {
        if (permissionBottomSheetFragment != null) {
            permissionBottomSheetFragment!!.dismiss()
            permissionBottomSheetFragment = null
        }
    }

    private fun showPermissionBottomSheet() {
        if (responseMap.isEmpty()) return

        val bundle = Bundle()
            bundle.putSerializable("permissionList", responseMap)

        if (permissionBottomSheetFragment == null) permissionBottomSheetFragment =
            PermissionBottomSheetFragment()
        permissionBottomSheetFragment!!.arguments = bundle
        permissionBottomSheetFragment!!.isCancelable = true
        permissionBottomSheetFragment!!.show(supportFragmentManager, "show")
    }

    val responseMap= mutableMapOf<String,ArrayList<String>>() as HashMap
    private fun getAccessControlList() {
        val mAQuery= AQuery(this)
        val mProgress= TransparentProgressDialog(this,R.drawable.ic_loader)
        var url:String= ConstantApi.LIVE_BASE_URL+"licensee/employee/acl"

        try {
            mAQuery.progress(mProgress).ajax(url, null, JSONObject::class.java, object : AjaxCallback<JSONObject?>() {

                override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {
                    super.callback(url, `object`, status)

                    if (status != null) {
                        if (status.code==200) {
                            try {
                                if (url.equals(ConstantApi.LIVE_BASE_URL+"licensee/employee/acl", ignoreCase = true)) {
                                    if (`object` != null) {
                                        if (`object`.get("success") as Boolean) {

                                            val accessControlList=`object`.getJSONObject("accessControlList")
                                            //val accessControlList =`object`.get("accessControlList") as JsonObject

                                            val keys: Iterator<*> = accessControlList.keys()
                                           // for ((dateKey, value) in accessControlList.entrySet()) {

                                                //this gets the dynamic keys
                                                //you can get any thing now json element,array,object according to json.

                                            //}
                                            while (keys.hasNext()) {
                                                // loop to get the dynamic key
                                                val currentDynamicKey = keys.next() as String
                                                // get the value of the dynamic key
                                                val currentDynamicValue: JSONArray = accessControlList.getJSONArray(currentDynamicKey)
                                                // do something here with the value...
                                                val jsonArrayACL = currentDynamicValue
                                                val appointmentsSize = jsonArrayACL.length()
                                                var tempArr=ArrayList<String>()
                                                for (count in 0 until appointmentsSize) {
                                                    val objectData = jsonArrayACL[count]
                                                    tempArr.add(objectData.toString())
                                                }
                                                responseMap.put(currentDynamicKey,tempArr)
                                            }
                                        } else {
                                            showToast(HelperMethods.safeText(`object`.get("message") as String, ""))
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast(HelperMethods.safeText(resources.getString(R.string.err_try_again), ""))
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
       /* CallApi().accessControlList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AccessControlListResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: AccessControlListResponse) {
                    if (response != null && response.accessControlList != null && response.success) {
                        onResponseReceived(response.accessControlList)

                    } else
                        showToast(response.message)
                }

                override fun onError(e: Throwable) {
                }
            })*/
    }

    private fun onResponseReceived(accessControl: AccessControlList) {

        if (accessControl.ADD.isNotEmpty()) {
            accessControlList.addAll(accessControl.ADD)
        }

        if (accessControl.VIEW.isNotEmpty()) {
            accessControlList.addAll(accessControl.VIEW)
        }

        if (accessControl.UPDATE.isNotEmpty()) {
            accessControlList.addAll(accessControl.UPDATE)
        }

        if (accessControl.DELETE.isNotEmpty()) {
            accessControlList.addAll(accessControl.DELETE)
        }
    }
}
