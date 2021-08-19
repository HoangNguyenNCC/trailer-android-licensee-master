package devx.app.licensee.modules.addUpSellItem

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.modules.addtrailer.AddTrailerActivity
import devx.app.licensee.modules.editTrailer.GalleryAdapter
import devx.app.licensee.modules.editTrailer.RentalHistoryActivity
import devx.app.licensee.modules.editTrailer.TrailerChargesActivity
import devx.app.licensee.modules.editTrailer.UpSellItemAdapter
import devx.app.licensee.webapi.CallApi
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.search.ChooseAddUpdateBottomSheet
import devx.app.seller.webapi.response.CommonResponse
import devx.app.seller.webapi.response.trailerslist.Photos
import devx.app.seller.webapi.response.upSellItem.UpsellItems
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details_trailer.*
import org.json.JSONArray
import org.json.JSONObject

class UpsellDetailsActivity : BaseActivity(){//, TrailerDetailsContract.View {

   // lateinit var presenter: TrailerDetailsPresenter
    var trailerId = ""
    private var licenseUrl = "http://www.africau.edu/images/default/sample.pdf"
    private var businessDocUrl = "http://www.africau.edu/images/default/sample.pdf"
    companion object{
        var trailerDetailsReponse=JSONObject()
        var upsellItemsList= ArrayList<UpsellItems>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_trailer)
        CommonTopbarView().setup(this)

        upSellItemRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        if (intent.hasExtra(IntentParams.TRAILER_ID))
            trailerId = intent.getStringExtra(IntentParams.TRAILER_ID).toString()

        HelperMethods.disableEditText(aSizeET)
        HelperMethods.disableEditText(aCapacityET)
        HelperMethods.disableEditText(aTareET)
        HelperMethods.disableEditText(aDescriptionET)
        HelperMethods.disableEditText(aTypeET)
        HelperMethods.disableEditText(aAgeET)
        HelperMethods.disableEditText(aVinNumberET)

        servicingImg.setOnClickListener {

            ChooseAddUpdateBottomSheet("SERVICING",traileObj).show(supportFragmentManager, "")
        }

        businessDocImage.setOnClickListener {
          
            ChooseAddUpdateBottomSheet("INSURANCE",traileObj).show(supportFragmentManager, "")
        }

        /*View trailer changes action*/
        viewTrailerCharges.setOnClickListener {
            if(!trailerDetailsReponse.isNull("rentalCharges")|| trailerDetailsReponse.has("rentalCharges")){
                var intent = Intent(context, TrailerChargesActivity::class.java)
                intent.putExtra(IntentParams.TRAILER_ID , trailerId)
                context.startActivity(intent)
            }
            else{
                showToast("No Rental charges generated, yet!")
            }

        }

        /*Edit trailer button action*/
        editTrailerButton.setOnClickListener {
            var intent = Intent(context, AddTrailerActivity::class.java)
            intent.putExtra(IntentParams.TRAILER_ID , trailerId)
            context.startActivity(intent)
        }
        /*Rental history layout action*/
        rentalHistoryLayout.setOnClickListener {
            if(trailerDetailsReponse.getJSONArray("rentalsList").length()>0) {
                startActivity(Intent(context, RentalHistoryActivity::class.java))
            }
            else{
                showToast("No Rental History, yet!")
            }
        }
        /*Delete trailer button action*/
        deleteTrailerButton.setOnClickListener {
            if(HelperMethods.isOwner()) {
                showDeleteAlert(trailerId)
            }
            else{
                if (HelperMethods.checkACL("TRAILER", "DELETE")) {
                    showDeleteAlert(trailerId)
                } else {
                    showToast("You do not have access to delete.")
                }
            }
        }
        if(Utils.isNetworkAvailable(context)) {
            getRequestedUpsellItemDetails()
        }
        else{
            showToast("No Internet Connectivity!")
        }
    }

    private fun showDeleteAlert(trailerId:String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Delete Trailer")
        //set message for alert dialog
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            deleteTrailer(trailerId)
            // Toast.makeText(context,"clicked yes",Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            //Toast.makeText(context,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        val positiveColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        val negativeColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        alertDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        alertDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }

    fun deleteTrailer(trailerId: String) {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", trailerId)

        CallApi().deleteTrailer(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: CommonResponse) {
                    if (response != null && response.success) {
                        showToast("Trailer deleted successfully!")
                        onBackPressed()
                    } else
                        showToast(response.message)

                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                }
            })

    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    var tempphotos =ArrayList<Photos>()
     fun handlerResponse(trailerObj: JSONObject,upsellItemsListTemp:JSONArray) {
        trailerDetailsReponse = trailerObj
        var photos=ArrayList<String>()
        var jsonArray=trailerObj.getJSONArray("photos")
        for(i in 0 until jsonArray.length()){
            photos.add(jsonArray.getJSONObject(i).getString("data"))
        }

        if (photos != null && photos!!.isNotEmpty()) {
            var mAdapter: GalleryAdapter
            mAdapter = GalleryAdapter(
                this,
                photos
            )
            view_pager_gallery.adapter = mAdapter
            circle_indicator.setViewPager(view_pager_gallery);
        }
         txtRentalStatus.text=if(trailerObj.getBoolean("availability"))
             "ACTIVE"
         else "INACTIVE"

        trailerName.text = "" + trailerObj.getString("name")

        if(trailerObj.getJSONArray("insurance").length()==0) {
            lytBusineeImg.gone()
        }
         else{
            lytBusineeImg.show()
            licenseUrl=trailerObj.getJSONArray("insurance").getJSONObject(0).getJSONObject("document").getString("data")
            //HelperMethods.downloadImage(trailerObj.getJSONArray("insurance").getJSONObject(0).getJSONObject("document").getString("data"), this, businessDocImage)
        }
        if(trailerObj.getJSONArray("servicing").length()==0) {
            lytservicingImg.gone()

        }
         else{
            lytservicingImg.show()
            businessDocUrl=trailerObj.getJSONArray("servicing").getJSONObject(0).getJSONObject("document").getString("data")
            //HelperMethods.downloadImage(trailerObj.getJSONArray("servicing").getJSONObject(0).getJSONObject("document").getString("data"), this, servicingImg)
        }


         if(upsellItemsListTemp.length()>0) {

             upsellItemTitle.show()
             upSellItemRecyclerView.show()
            for(j in 0 until upsellItemsListTemp.length()) {
                var jsonObject=upsellItemsListTemp.get(j) as JSONObject
                var upsellItems=UpsellItems()
                upsellItems._id=jsonObject.get("_id").toString()
                upsellItems.licensee=jsonObject.get("licensee").toString()
                upsellItems.name=jsonObject.get("name").toString()

                var photos=Photos()
                photos.data=jsonObject.getJSONObject("photo").getString("data")
                photos._id=jsonObject.getJSONObject("photo").getString("_id")

                upsellItems.photo=photos
                upsellItems.price=jsonObject.get("price").toString()
                upsellItems.rating=jsonObject.get("rating") as Int
                upsellItemsList.add(upsellItems)
            }
            upSellItemRecyclerView.adapter = UpSellItemAdapter(upsellItemsList, this)

        }
         else{
             upsellItemTitle.gone()
             upSellItemRecyclerView.gone()
         }

         aVinNumberET.text=trailerObj.getString("vin").toEditable()
         aSizeET.text=trailerObj.getString("size").toEditable()
         aCapacityET.text=trailerObj.getString("capacity").toEditable()
         aTareET.text=trailerObj.getString("tare").toEditable()
         aDescriptionET.text=trailerObj.getString("description").toEditable()
         aTypeET.text=trailerObj.getString("type").toEditable()
         aAgeET.text=trailerObj.getString("age").toEditable()
       // TrailerApplication.trailerObj = trailer
        //showToast("Fetched Trailer details")

    }
    var traileObj=JSONObject()
    var url=""
    private fun getRequestedUpsellItemDetails() {
        val mAQuery= AQuery(context)
        val mProgress= TransparentProgressDialog(context, R.drawable.ic_loader)

        url= ConstantApi.LIVE_BASE_URL+"licensee/trailer?"

        val cb = AjaxCallback<JSONObject>()
        cb.header("authorization",HelperMethods.getAuthorizationToken())
        cb.timeout(20000)
        cb.url(url+"id=$trailerId")
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


        if (status != null) {
            if (status.code==200) {
                try {
                    if (url.equals( ConstantApi.LIVE_BASE_URL+"licensee/trailer?"+"id=$trailerId", ignoreCase = true)) {
                        if (`object` != null) {

                            if (`object`.getBoolean("success")) {
                                traileObj=`object`.getJSONObject("trailerObj")
                                handlerResponse(`object`.getJSONObject("trailerObj"),`object`.getJSONArray("upsellItemsList"))

                            } else {
                                showToast(HelperMethods.safeText(`object`.getString("message"), ""))
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
}
