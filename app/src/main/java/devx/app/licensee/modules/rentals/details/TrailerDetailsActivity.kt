package devx.app.seller.modules.rentals.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.BaseFragment
import devx.app.licensee.common.TrailerApplication
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.rentals.book.RentalBookingActivity
import kotlinx.android.synthetic.main.activity_trailer_details.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class TrailerDetailsActivity : BaseActivity(){//, TrailerDetailsContract.View {

    lateinit var presenter: TrailerDetailsPresenter
    var trailerId = ""

    fun openTrailer(context: Context, trailerId: String) {
        var intent = Intent(context, this::class.java)
        intent.putExtra(IntentParams.TRAILER_ID, trailerId)
        context.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer_details)

        CommonTopbarView().setup(this, "")
        if (intent.hasExtra(IntentParams.TRAILER_ID))
            trailerId = intent.getStringExtra(IntentParams.TRAILER_ID).toString()

       // presenter = TrailerDetailsPresenter()
        //presenter.attachView(this, lifecycle)

        //presenter.loadTrailerDetails(if (trailerId.isEmpty()) "5e4545280dcf075d55515c2d" else trailerId)

       // getTrailerDetails()

        rRentButton.setOnClickListener {
            gotoActivity(RentalBookingActivity::class.java)
        }

        val min: Calendar = Calendar.getInstance()
        calenderView.setMinimumDate(min)
        val max: Calendar = Calendar.getInstance()
//        calenderView.setMaximumDate(max)

        val todayDate = Calendar.getInstance()
        calenderView.setDate(todayDate)

        val selectedDates: MutableList<Calendar> = ArrayList()
        val calender1 = Calendar.getInstance()
        calender1.set(2020, 1, 26)
        selectedDates.add(calender1)

        val calender2 = Calendar.getInstance()
        calender2.set(2020, 1, 27)
        selectedDates.add(calender2)

        val calender3 = Calendar.getInstance()
        calender3.set(2020, 1, 28)
        selectedDates.add(calender3)

        calenderView.selectedDates = selectedDates
    }




    private fun setUpPriceDetails(orderSummary: ArrayList<String>?) {
        if (orderSummary == null) return

        val rTrailerPreviewImagesLL = findViewById<LinearLayout>(R.id.rTrailerPreviewImagesLL)
        rTrailerPreviewImagesLL.removeAllViews()

        for (i in orderSummary.indices) {
            val view = layoutInflater.inflate(R.layout.item_image_layout, null, false)
            val rTrailerPreviewImages = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
            HelperMethods.downloadImage(orderSummary.get(i), context, rTrailerPreviewImages)
            rTrailerPreviewImages.setOnClickListener {
                HelperMethods.downloadImage(orderSummary.get(i), context, rTrailerPreview)
            }

            rTrailerPreviewImagesLL.addView(view)
        }
    }

   /* override fun handlerResponse(trailerObj: JSONObject, t1: JSONArray) {
        rTopT2.text = trailerObj.get("name").toString()
        rTrailerDetails.text =trailerObj.get("description").toString()
        rPrice.text = "" + trailerObj.get("price").toString() + " / HR"
        rRating.text = "" +trailerObj.get("rating").toString() + " / 5"
        rRatingBar.rating =trailerObj.get("rating") as Float

        var photos=ArrayList<String>()
        for(i in 0 until trailerObj.getJSONArray("photos").length()){
            photos.add(trailerObj.getJSONArray("photos").get(i).toString())
        }
        if (photos != null && photos!!.isNotEmpty())
            HelperMethods.downloadImage(photos!!.get(0), context, rTrailerPreview)

        setUpPriceDetails(photos as ArrayList<String>?)    }*/
}
