package devx.app.seller.modules.home.hometab

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.PlaceHolderAdapter
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.BoldTextView
import devx.app.licensee.common.custumViews.RegularTextView
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.editTrailer.TrailerDetailsActivity
import devx.app.licensee.modules.home.hometab.EmployeeListAdapter
import devx.app.licensee.modules.home.hometab.InvitePermissionAdapter
import devx.app.licensee.modules.home.hometab.PermissionBottomSheetFragment
import devx.app.licensee.modules.home.hometab.UpsellItemAdapter
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity
import devx.app.licensee.modules.inviteEmployees.PermissionItem
import devx.app.licensee.webapi.*
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse
import devx.app.licensee.webapi.response.addUpSell.UpsellItems
import devx.app.licensee.webapi.response.employee.EmployeeDetailsObj
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.seller.webapi.response.CommonResponse
import devx.app.seller.webapi.response.trailerslist.Trailers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : BaseFragment(),MyAllTrailerAdapter.OnLongClickListener {

    private var loadingDialog: ProgressDialog? = null

    private lateinit var upsellItemAdapter: UpsellItemAdapter
    private lateinit var employeeListAdapter: EmployeeListAdapter
    var upSellItemList:List<UpsellItems> = ArrayList<UpsellItems>()
    var mEmployeeList  = ArrayList<EmployeeDetailsObj>()
    var homeActivity:HomeActivity= HomeActivity()
    var permissionBottomSheetFragment: PermissionBottomSheetFragment? =
        PermissionBottomSheetFragment()
    companion object{
        var strFlag="trailer"
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loadingDialog = ProgressDialog(activity, R.style.MyTheme)
        loadingDialog!!.setCancelable(false)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.homeActivity= context as HomeActivity;
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAllTrailerRecyclerView.adapter = PlaceHolderAdapter(context)

        textView.text=HelperMethods.getPersonUserName()+" at a glance"
        upsellItemAdapter = UpsellItemAdapter(
                this@HomeFragment,
                upSellItemList
        )
        upSellItemRecyclerView.adapter = upsellItemAdapter

        employeeListAdapter = EmployeeListAdapter(
                this@HomeFragment,
                mEmployeeList
        )
        myEmployeeRecyclerView.adapter = employeeListAdapter

         adapter = MyAllTrailerAdapter(
                 this@HomeFragment,
                 mList,
                 isHorizontalList = true)
            { item ->
                if(HelperMethods.isOwner()) {
                    val intent = Intent(context, TrailerDetailsActivity::class.java)
                    intent.putExtra(IntentParams.TRAILER_ID, item._id)
                    startActivity(intent)
                }
                else{
                    if (HelperMethods.checkACL("TRAILER", "UPDATE")) {

                        val intent = Intent(context, TrailerDetailsActivity::class.java)
                        intent.putExtra(IntentParams.TRAILER_ID, item._id)
                        startActivity(intent)
                    } else {
                        Toast.makeText(context, "You do not have access to update.", Toast.LENGTH_SHORT).show()
                    }
                }
            };
        myAllTrailerRecyclerView.adapter = adapter

        homeSwipeToRefresh.setOnRefreshListener {
            homeSwipeToRefresh.isRefreshing = true
           if(strFlag=="trailer"){
               if(HelperMethods.isOwner()) {
                   showTrailer()
               }
               else{
                   if (HelperMethods.checkACL("TRAILER", "VIEW")) {
                       showTrailer()
                   } else {
                       if (homeSwipeToRefresh.isRefreshing)
                       homeSwipeToRefresh.isRefreshing = false
                       showToast("You do not have access to view.")
                   }
               }
           }
            else if(strFlag=="upsell"){
               if(HelperMethods.isOwner()) {
                   showUpsell()
               }
               else {
                   if (HelperMethods.checkACL("UPSELL", "VIEW")) {
                       showUpsell()
                   } else {
                       if (homeSwipeToRefresh.isRefreshing)
                           homeSwipeToRefresh.isRefreshing = false
                       showToast("You do not have access to view.")
                   }
               }
           }
            else if(strFlag=="employee"){
               if(HelperMethods.isOwner()) {
                   showEmployee()
               }
               else {
                   if (HelperMethods.checkACL("EMPLOYEES", "VIEW")) {
                       showEmployee()
                   } else {
                       if (homeSwipeToRefresh.isRefreshing)
                           homeSwipeToRefresh.isRefreshing = false
                       showToast("You do not have access to view.")
                   }
               }
           }
        }
        txtTrailers.setOnClickListener {
            strFlag="trailer"
            if(HelperMethods.isOwner()) {
                showTrailer()
            }
            else{
                if (HelperMethods.checkACL("TRAILER", "VIEW")) {
                    showTrailer()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }

        txtUpsell.setOnClickListener {
            strFlag="upsell"
            if(HelperMethods.isOwner()) {
                showUpsell()
            }
            else {
                if (HelperMethods.checkACL("UPSELL", "VIEW")) {
                    showUpsell()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }

        txtEmployee.setOnClickListener {
            strFlag="employee"
            if(HelperMethods.isOwner()) {
                showEmployee()
            }
            else {
                if (HelperMethods.checkACL("EMPLOYEES", "VIEW")) {
                    showEmployee()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }
    }
    fun showUpsell(){
        resetOptionLayoutUI()
        divider1.gone()
        divider.gone()
        txtUpsell.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
        txtUpsell.setTextColor(ResourcesUtil.getColor(R.color.black))

        lytTrailer.gone()
        lytUpsellItem.show()
        lytEmployee.gone()
        if(Utils.isNetworkAvailable(context!!)){
            getUpsellItemAPI()
        }
        else{
            showToast("No Internet Connectivity!")
        }
    }
    fun showEmployee(){
        resetOptionLayoutUI()
        divider.show()
        divider1.gone()
        txtEmployee.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
        txtEmployee.setTextColor(ResourcesUtil.getColor(R.color.black))

        lytTrailer.gone()
        lytUpsellItem.gone()
        lytEmployee.show()
        if(Utils.isNetworkAvailable(context!!)){
            getAccessControlList()
        }
        else{
            showToast("No Internet Connectivity!")
        }
    }
    fun showTrailer(){
        resetOptionLayoutUI()
        divider1.show()
        divider.gone()
        txtTrailers.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
        txtTrailers.setTextColor(ResourcesUtil.getColor(R.color.black))

        lytTrailer.show()
        lytUpsellItem.gone()
        lytEmployee.gone()

        if(Utils.isNetworkAvailable(context!!)){
            getFeaturedTrailer()
        }
        else{
            showToast("No Internet Connectivity!")
        }

    }
    var url:String=""
    private fun getEmployeeList() {
        val mAQuery= AQuery(activity)
        val mProgress= TransparentProgressDialog(activity, R.drawable.ic_loader)

        url= ConstantApi.LIVE_BASE_URL+"employees"
        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization", HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url)
            .type(
                    JSONObject::class.java
            ).weakHandler(
                        this,
                        "getRequestedData"
                )
        mAQuery.progress(mProgress).ajax(cb)

    }

    private var permissionList = arrayListOf<PermissionItem>()
    fun getRequestedData(
            url: String?, `object`: JSONObject?,
            status: AjaxStatus
    ) {
        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals(ConstantApi.LIVE_BASE_URL + "employees", ignoreCase = true)) {
                        if (`object` != null) {
                            if (`object`.getBoolean("success")) {
                                if(`object`.getJSONArray("employeeList").length()>0){
                                    myEmployeeRecyclerView.show()
                                    txtViewNoEmployeeFound.gone()
                                    mEmployeeList.clear()
                                    val count: Int=`object`.getJSONArray("employeeList").length()
                                    val jsonArray: JSONArray =`object`.getJSONArray("employeeList")
                                    for(empItem in 0 until count){

                                        var employeeDetailsObj :EmployeeDetailsObj= EmployeeDetailsObj()
                                        employeeDetailsObj.name=jsonArray.getJSONObject(empItem).getString("name")
                                        employeeDetailsObj.mobileVerified=jsonArray.getJSONObject(empItem).getBoolean("isMobileVerified")
                                        employeeDetailsObj.emailVerified=jsonArray.getJSONObject(empItem).getBoolean("isEmailVerified")
                                        employeeDetailsObj.name=jsonArray.getJSONObject(empItem).getString("name")
                                        employeeDetailsObj.owner=jsonArray.getJSONObject(empItem).getBoolean("isOwner")
                                        employeeDetailsObj.email=jsonArray.getJSONObject(empItem).getString("email")
                                        employeeDetailsObj._id=jsonArray.getJSONObject(empItem).getString("_id")
                                        employeeDetailsObj.mobile=jsonArray.getJSONObject(empItem).getString("mobile")
                                        employeeDetailsObj.photo=jsonArray.getJSONObject(empItem).getJSONObject("photo").getString("data")
                                        employeeDetailsObj.address=jsonArray.getJSONObject(empItem).getJSONObject("address").getString("text")
                                        employeeDetailsObj.dob=HelperMethods.convertUTCtoNormal(jsonArray.getJSONObject(empItem).getString("dob"))
                                        val accessControlList=jsonArray.getJSONObject(empItem).getJSONObject("acl")

                                        val keys: Iterator<*> = accessControlList.keys()
                                        employeeDetailsObj.acl=HashMap<String, ArrayList<String>>()
                                        var tempPermissionArr=ArrayList<PermissionItem>()

                                        while (keys.hasNext()) {
                                            // loop to get the dynamic key
                                            val currentDynamicKey = keys.next() as String
                                            // get the value of the dynamic key
                                            val currentDynamicValue: JSONArray = accessControlList.getJSONArray(currentDynamicKey)
                                            // do something here with the value...
                                            val jsonArrayACL = currentDynamicValue
                                            val jArrLenght = jsonArrayACL.length()
                                            var tempArr=ArrayList<String>()
                                            for (count in 0 until jArrLenght) {
                                                val objectData = jsonArrayACL[count]
                                                tempArr.add(objectData.toString())
                                            }
                                            employeeDetailsObj.acl.put(currentDynamicKey, tempArr)
                                        }

                                        var permissionList = arrayListOf<PermissionItem>()
                                        for (permissionitem in responseMap) {
                                            permissionList.add(PermissionItem(permissionitem.key, permissionitem.value, 0, ArrayList<String>()))
                                        }
                                        for(i in 0 until permissionList.size){
                                            for(item in employeeDetailsObj.acl){
                                                if (permissionList.get(i).atribute == item.key&&item.value.size>0) {
                                                    permissionList.get(i).isPermissionSelected=1
                                                    break
                                                }
                                            }

                                        }
                                        employeeDetailsObj.arrACL=permissionList
                                        mEmployeeList.add(employeeDetailsObj)

                                    }
                                    employeeListAdapter?.notifyDataSetChanged(mEmployeeList)
                                }
                                else{
                                    myEmployeeRecyclerView.gone()
                                    txtViewNoEmployeeFound.show()
                                }

                            } else {
                                myEmployeeRecyclerView.gone()
                                txtViewNoEmployeeFound.show()
                                showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                            }
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                HelperMethods.setIsUserLogin(false)
                HelperMethods.setUserId("-1")
                showToast("Session expired! Please login again.")
                HelperMethods.closeEverythingOpenSplash(homeActivity)
            }
        }
    }
    fun dismissPermissionBottomSheet() {
        if (permissionBottomSheetFragment != null) {
            permissionBottomSheetFragment!!.dismiss()
            permissionBottomSheetFragment = null
        }
    }
      fun showPermissionBottomSheet(
              arrACL: java.util.ArrayList<PermissionItem>,
              context: Context
      ) {

        val bundle = Bundle()
        bundle.putSerializable("permissionList", arrACL)

        if (permissionBottomSheetFragment == null) permissionBottomSheetFragment =
            PermissionBottomSheetFragment()
         permissionBottomSheetFragment!!.arguments = bundle
         permissionBottomSheetFragment!!.isCancelable = false
         permissionBottomSheetFragment!!.show(activity!!.supportFragmentManager, "show")
    }
    val responseMap= mutableMapOf<String, ArrayList<String>>() as HashMap
    private fun getAccessControlList() {
        val mAQuery = AQuery(activity)
        val mProgress = TransparentProgressDialog(activity, R.drawable.ic_loader)
        var url: String = ConstantApi.LIVE_BASE_URL + "licensee/employee/acl"
        if (homeSwipeToRefresh.isRefreshing)
            homeSwipeToRefresh.isRefreshing = false
        try {
            mAQuery.progress(mProgress)
                .ajax(url, null, JSONObject::class.java, object : AjaxCallback<JSONObject?>() {

                    override fun callback(
                            url: String?,
                            `object`: JSONObject?,
                            status: AjaxStatus?
                    ) {
                        super.callback(url, `object`, status)

                        if (status != null) {
                            if (status.code == 200) {
                                try {
                                    if (url.equals(
                                                    ConstantApi.LIVE_BASE_URL + "licensee/employee/acl",
                                                    ignoreCase = true
                                            )
                                    ) {
                                        if (`object` != null) {
                                            if (`object`.get("success") as Boolean) {

                                                val accessControlList =
                                                        `object`.getJSONObject("accessControlList")

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

                                                getEmployeeList()
                                            } else {
                                                showToast(
                                                        HelperMethods.safeText(
                                                                `object`.get("message") as String,
                                                                ""
                                                        )
                                                )
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                HelperMethods.setIsUserLogin(false)
                                HelperMethods.setUserId("-1")
                                showToast("Session expired! Please login again.")
                                HelperMethods.closeEverythingOpenSplash(homeActivity)
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetOptionLayoutUI() {
        txtTrailers.setBackgroundResource(0)
        txtTrailers.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))

        txtUpsell.setBackgroundResource(0)
        txtUpsell.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))

        txtEmployee.setBackgroundResource(0)
        txtEmployee.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))
    }
    override fun onResume() {
        super.onResume()
        if(strFlag=="trailer"){

            if(HelperMethods.isOwner()) {
                showTrailer()
            }
            else{
                if (HelperMethods.checkACL("TRAILER", "VIEW")) {
                    showTrailer()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }
        else if(strFlag=="upsell"){
            if(HelperMethods.isOwner()) {
                showUpsell()
            }
            else {
                if (HelperMethods.checkACL("UPSELL", "VIEW")) {
                    showUpsell()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }
        else if(strFlag=="employee"){
            if(HelperMethods.isOwner()) {
                showEmployee()
            }
            else {
                if (HelperMethods.checkACL("EMPLOYEES", "VIEW")) {
                    showEmployee()
                } else {
                    showToast("You do not have access to view.")
                }
            }
        }
    }

    /**
     * get web services api call
     */

    lateinit var adapter:MyAllTrailerAdapter
    var mList :List<Trailers> = ArrayList<Trailers>()
    var webService: Api = ServiceGenerator.getApi()
    private fun getFeaturedTrailer() {

        loadingDialog?.show()
        val requestMap = HashMap<String, String>()
        requestMap["count"] = "10"
        requestMap["skip"] = "0"
        if (homeSwipeToRefresh.isRefreshing)
            homeSwipeToRefresh.isRefreshing = false

        val getAllTrailerCall: Call<HomeResponse> =webService.getAllTrailers(ConstantApi.LIVE_BASE_URL + "licensee/trailers?", requestMap)

        getAllTrailerCall.enqueue(object : Callback<HomeResponse> {
            override fun onResponse(
                    call: Call<HomeResponse>,
                    response: Response<HomeResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.trailersList != null && response.body()!!.trailersList.isNotEmpty()) {
                            if (response.body()!!.trailersList.size > 0) {
                                myAllTrailerRecyclerView.visible()
                                txtViewNoDataFind.gone()
                                mList = response.body()!!.trailersList
                                adapter.notifyDataSetChanged(mList)
                                loadingDialog?.dismiss()
                            } else {
                                myAllTrailerRecyclerView.gone()
                                txtViewNoDataFind.visible()
                                loadingDialog?.dismiss()
                            }
                        } else {
                            myAllTrailerRecyclerView.gone()
                            txtViewNoDataFind.visible()
                            loadingDialog?.dismiss()
                        }
                    } else {
                        showToast("Something went wrong ...")
                        loadingDialog?.dismiss()
                    }
                } else {
                    ///handling bad request 400 response
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    loadingDialog?.dismiss()
                    HelperMethods.closeEverythingOpenSplash(homeActivity)
                }
            }

            override fun onFailure(
                    call: Call<HomeResponse>,
                    t: Throwable
            ) {
                loadingDialog?.dismiss()
            }
        })

    }
    private fun getUpsellItemAPI() {

        val endPoint = "licensee/upsellitems?"
        val requestMap = HashMap<String, String>()
        requestMap["count"] = "10"
        requestMap["skip"] = "0"
        if (homeSwipeToRefresh.isRefreshing)
            homeSwipeToRefresh.isRefreshing = false

        val getUpSellOrTrailerItemsCall: Call<AddUpSellItemResponse> =webService.getUpSellOrTrailerItems(ConstantApi.LIVE_BASE_URL + endPoint, requestMap)

        loadingDialog?.show()
        getUpSellOrTrailerItemsCall.enqueue(object : Callback<AddUpSellItemResponse> {
            override fun onResponse(
                    call: Call<AddUpSellItemResponse>,
                    response: Response<AddUpSellItemResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.upsellItemsList.size > 0) {

                            upSellItemList = response.body()!!.upsellItemsList
                            upSellItemRecyclerView.visible()
                            txtNoUpsellItemAdded.gone()
                            upsellItemAdapter.notifyDataSetChanged(upSellItemList)
                            loadingDialog?.dismiss()
                        } else {
                            upSellItemRecyclerView.gone()
                            txtNoUpsellItemAdded.visible()
                            loadingDialog?.dismiss()
                        }
                    } else {
                        showToast("Something went wrong ...")
                        loadingDialog?.dismiss()
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    HelperMethods.closeEverythingOpenSplash(homeActivity)
                    loadingDialog?.dismiss()
                }
            }

            override fun onFailure(
                    call: Call<AddUpSellItemResponse>,
                    t: Throwable
            ) {
                loadingDialog?.dismiss()
            }
        })

    }
    fun deleteEmployee(id: String) {

        loadingDialog?.show()
        //val mProgress= TransparentProgressDialog(activity, R.drawable.ic_loader)
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)

        CallApi().deleteEmployeeProfile(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        showToast("Employee deleted successfully!")
                        showEmployee()
                        // HelperMethods.closeEverythingOpenHome(activity)
                    } else
                        showToast(response.message)
                    loadingDialog?.dismiss()
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    loadingDialog?.dismiss()
                }
            })
    }

    fun deleteUpsell(id: String) {
       // val mProgress= TransparentProgressDialog(activity, R.drawable.ic_loader)
        loadingDialog?.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)

        CallApi().deleteUpsell(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        showToast("Upsell item deleted successfully!")
                        showUpsell()
                    } else
                        showToast(response.message)
                    loadingDialog?.dismiss()
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    loadingDialog?.dismiss()
                }
            })
    }



    override fun onLongClick(position: Int) {

    }

    /*Show employee action bottom sheet layout*/
    var bottomSheetDialog: BottomSheetDialog? = null
    var bottomSheetView: View? = null
    var addCancelBtn: RegularTextView?=null
    var empDetailsBtn: RegularTextView?=null
    var editACLBtn: RegularTextView?=null
    fun showBottomSheet(
            owner: Boolean?,
            arrACL: java.util.ArrayList<PermissionItem>?,
            position: Int
    ) {
        bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val behaviorField: BottomSheetBehavior<*> = bottomSheetDialog!!.getBehavior()
        behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
        behaviorField.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float
            ) {
            }
        })
        bottomSheetView = layoutInflater
            .inflate(
                    R.layout.choose_employee_action, null)


        editACLBtn=bottomSheetView!!.findViewById(R.id.editACLBtn)
        addCancelBtn=bottomSheetView!!.findViewById(R.id.addCancelBtn)
        empDetailsBtn=bottomSheetView!!.findViewById(R.id.empDetailsBtn)

        addCancelBtn!!.setOnClickListener {
            bottomSheetDialog!!.cancel()
        }
        if(owner!!){
            editACLBtn.gone()
        }
        empDetailsBtn!!.setOnClickListener{

            bottomSheetDialog!!.cancel()
            if(owner!!){
                val editProfileIntent = Intent(activity, ProfileEditActivity::class.java)
                editProfileIntent.putExtra("flag", "businessInfo")
                startActivity(editProfileIntent)
            }
            else{
                val editProfileIntent = Intent(activity, ProfileEditActivity::class.java)
                editProfileIntent.putExtra("flag", "userInfo")
                editProfileIntent.putExtra("id", mEmployeeList[position]._id)
                editProfileIntent.putExtra("photo", mEmployeeList[position].photo)
                editProfileIntent.putExtra("name", mEmployeeList[position].name)
                editProfileIntent.putExtra("address", mEmployeeList[position].address)
                editProfileIntent.putExtra("mobile", mEmployeeList[position].mobile)
                editProfileIntent.putExtra("email", mEmployeeList[position].email)
                editProfileIntent.putExtra("dob", mEmployeeList[position].dob)
                startActivity(editProfileIntent)
            }

        }
        editACLBtn!!.setOnClickListener {

            bottomSheetDialog!!.cancel()
                if (HelperMethods.checkACL("EMPLOYEES", "UPDATE")) {

                    showPermissionDialog(arrACL, position)
                } else {
                    Toast.makeText(activity, "You do not have access to update.", Toast.LENGTH_SHORT).show()
                }
        }

        bottomSheetDialog!!.setContentView(bottomSheetView!!)

        bottomSheetDialog!!.show()
    }

    private fun showPermissionDialog(arrACL: java.util.ArrayList<PermissionItem>?, position: Int) {

        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.fragment_permission_bottom_sheet, null, false)
        // LayoutInflater.inflate(R.layout.fragment_permission_bottom_sheet, null, false)
        val dialog =
            AlertDialog.Builder(context!!)
                .setView(view)
                .create()

        EmployeeListAdapter.tempSelectedpermissionList.clear()
        val closeView = view.findViewById<ImageView>(R.id.commonBack)
        val recyclerView=view.findViewById<RecyclerView>(R.id.invitePermissionsRecyclerView)
        val doneButton=view.findViewById<BoldTextView>(R.id.doneButton)

        EmployeeListAdapter.permissionAdapter = InvitePermissionAdapter(context!!, arrACL, mEmployeeList[position].acl)
        recyclerView.adapter = EmployeeListAdapter.permissionAdapter

        EmployeeListAdapter.permissionAdapter!!.notifyDataSetChanged(arrACL!!)

        closeView.setOnClickListener {
            dialog.dismiss()
        }

        doneButton.setOnClickListener {
            dialog.dismiss()
            saveEmployeePriviledges(mEmployeeList[position]._id)
        }

        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun saveEmployeePriviledges(_id: String) {
        val mProgress=TransparentProgressDialog(context!!, R.drawable.ic_loader)
        mProgress.show()
        val tempJson= JsonObject()
        if(EmployeeListAdapter.permissionAdapter!!.permissionList!!.size>0){

            for(i in 0 until EmployeeListAdapter.permissionAdapter!!.permissionList!!.size){
                val tempJArr= JsonArray()
                for(t in EmployeeListAdapter.permissionAdapter!!.permissionList!![i].selectedPermissionArr) {
                    tempJArr.add(t)
                }
                tempJson.add(EmployeeListAdapter.permissionAdapter!!.permissionList!![i].atribute, tempJArr)
            }
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("employeeId", _id)
        jsonObject.add("acl", tempJson)
        jsonObject.addProperty("type", "employee")
        val requestBodyAllInfo: RequestBody =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        CallApi().updateEmployeeProfile(requestBodyAllInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        Toast.makeText(activity, "Saved successfully!",
                                Toast.LENGTH_LONG
                        ).show()
                        EmployeeListAdapter.tempSelectedpermissionList.clear()
                        onResume()
                    } else
                        Toast.makeText(activity, response.message, Toast.LENGTH_LONG).show()
                    mProgress.dismiss()
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    mProgress.dismiss()
                }
            })
    }


}
