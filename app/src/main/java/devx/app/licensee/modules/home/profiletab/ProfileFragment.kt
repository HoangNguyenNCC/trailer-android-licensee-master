package devx.app.licensee.modules.home.profiletab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import devx.app.licensee.R
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.PermissionUtils
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.*
import devx.app.licensee.modules.PdfViewerActivity
import devx.app.licensee.modules.financial.FinancialActivity
import devx.app.licensee.webapi.response.MyProfile.profile.EmployeeObj
import devx.app.seller.modules.home.hometab.HomeActivity
import devx.app.seller.modules.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : BaseFragment() {

    private var licenseFileUrl = ""
    lateinit var employeeObject:EmployeeObj
    companion object{lateinit var dummyBitmap: Bitmap}
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dummyBitmap = BitmapFactory.decodeResource(
                context!!.resources,
                R.drawable.grey_account_icon
        )
        employeeObject=EmployeeObj()
        cardViewEmployeeProfile.setOnClickListener {
            val editProfileIntent = Intent(activity, ProfileEditActivity::class.java)
            editProfileIntent.putExtra("flag", "userInfo")
            startActivity(editProfileIntent)
        }

        logout.setOnClickListener {
            //FirebaseInstanceId.getInstance().deleteInstanceId()
            HelperMethods.setIsUserLogin(false)
            HelperMethods.setUserId("-1")
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        cardViewFinancial.setOnClickListener {
            if(HelperMethods.isOwner()) {
                startActivity(Intent(activity, FinancialActivity::class.java))
            }
            else {
                if (HelperMethods.checkACL("FINANCIALS", "VIEW")) {
                    startActivity(Intent(activity, FinancialActivity::class.java))
                } else {
                    Toast.makeText(activity, "You do not have access to view financials.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cardViewNotificationSettings.setOnClickListener{
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE", activity?.packageName)
            startActivity(intent)
        }


        profileSwipeToRefresh.setOnRefreshListener {
            profileSwipeToRefresh.isRefreshing = true
            getRequestedData()
        }

        cardViewLicenseeProfile.setOnClickListener{

            if(HelperMethods.isOwner()) {
                if(Utils.isNetworkAvailable(context)){
                    val editProfileIntent = Intent(activity, ProfileEditActivity::class.java)
                    editProfileIntent.putExtra("flag", "businessInfo")
                    startActivity(editProfileIntent)
                }
                else{
                    showToast("No Internet Connectivity!")
                }
            }
            else{
                    Toast.makeText(activity, "You do not have access to edit licensee profile.", Toast.LENGTH_SHORT).show()
            }
        }
        cardViewAboutUs.setOnClickListener {
               var url="https://www.trailers2000.com.au/who-we-are"

            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)


        }
    }

    override fun onResume() {
        super.onResume()
        if(Utils.isNetworkAvailable(context)) {
            getRequestedData()
        }
        else{
            showToast("No Internet Connectivity!")
        }
    }
    var url:String=""
    private fun getRequestedData() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)
        url= ConstantApi.LIVE_BASE_URL+"employee/profile?"
        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization", HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url + "employeeId=" + HelperMethods.getUserId())
            .type(
                    JSONObject::class.java
            ).weakHandler(
                        this,
                        "getRequestedData"
                )
        mAQuery.progress(mProgress).ajax(cb)

    }
    fun getRequestedData(
            url: String?, `object`: JSONObject?,
            status: AjaxStatus
    ) {
        if (profileSwipeToRefresh.isRefreshing)
            profileSwipeToRefresh.isRefreshing = false
        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals(ConstantApi.LIVE_BASE_URL + "employee/profile?" + "employeeId=" + HelperMethods.getUserId(), ignoreCase = true)) {
                        if (`object` != null) {

                            if (`object`.getBoolean("success")) {


                                val employeeObj=`object`.getJSONObject("employeeObj")
                                val licenseeObj=`object`.getJSONObject("licenseeObj")
                                userName.text = safeText("" + employeeObj.getString("name"))
                                userEmail.text = "" + employeeObj.getString("mobile")
                                pBusinessName.text = "" + licenseeObj.getString("name")
                                pBusinessMobile.text = "" + licenseeObj.getString("mobile")
                               // HelperMethods.downloadImage( employeeObj.getString("photo"), activity, userProfileImageView,resources.getDrawable(R.drawable.grey_account_icon))
                                HelperMethods.downloadImage(licenseeObj.getJSONObject("logo").getString("data"), activity, imageView)

                                try {
                                    var temp: AsyncTask<String, Void, Bitmap>? = GetBitmapFromURLAsync().execute(employeeObj.getJSONObject("photo").getString("data"))
                                    if (temp != null) {
                                        userProfileImageView.setImageBitmap(temp.get())
                                    }
                                } catch (e: IOException) {
                                    System.out.println(e)
                                }

                            } else {
                                showToast(HelperMethods.safeText(`object`.getString("message"), ""))
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else if(status.code==500){
                showToast(HelperMethods.safeText(`object`!!.getString("message"), ""))

            }else {
                ///handling bad request 400 response
                HelperMethods.setIsUserLogin(false)
                HelperMethods.setUserId("-1")
                showToast("Session expired! Please login again.")
                HelperMethods.closeEverythingOpenSplash(activity)
            }
        }
    }
    class GetBitmapFromURLAsync : AsyncTask<String, Void, Bitmap>()
    {

        private lateinit var resultBitmpa: Bitmap

        override fun onPostExecute(bitmap: Bitmap) {
            //  return the bitmap by doInBackground and store in result
            resultBitmpa= bitmap
        }

        override fun doInBackground(vararg params: String?): Bitmap {
            return (if(HelperMethods.getBitmapFromURL(params[0])!=null)
                HelperMethods.getBitmapFromURL(params[0])
            else
                dummyBitmap)!!
        }
    }


    fun onFileDownloaded(fileName: String, filePath: String) {
        showToast("File Downloaded Successfully")
        val pdfViewerIntent = Intent(activity, PdfViewerActivity::class.java)
        pdfViewerIntent.putExtra(IntentParams.FILE_PATH, filePath)
        pdfViewerIntent.putExtra(IntentParams.FILE_NAME, fileName)
        startActivity(pdfViewerIntent)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.hasPermissions(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                )
                    DownloadFile(activity as HomeActivity).execute("http://www.africau.edu/images/default/sample.pdf")
                else {
                    PermissionUtils.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PermissionUtils.WRITE_EXTERNAL_STORAGE
                    )
                }

            } else
                showToast("Permission Denied")
        } else if (requestCode == PermissionUtils.WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.hasPermissions(
                                context,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                )
                    DownloadFile(activity as HomeActivity).execute(licenseFileUrl)
                else {
                    PermissionUtils.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PermissionUtils.WRITE_EXTERNAL_STORAGE
                    )
                }
            } else
                showToast("Permission Denied")
        }
    }



}
