package devx.app.licensee.modules.editTrailer

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.TrailerApplication
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.rentals.details.TrailerDetailsContract
import devx.app.seller.modules.rentals.details.TrailerDetailsPresenter
import kotlinx.android.synthetic.main.activity_details_trailer.*
import org.json.JSONArray
import org.json.JSONObject

class EditTrailerActivity : BaseActivity(), TrailerDetailsContract.View {


    lateinit var presenter: TrailerDetailsPresenter
    var trailerId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trailer)
        CommonTopbarView().setup(this)

        if (intent.hasExtra(IntentParams.TRAILER_ID))
            trailerId = intent.getStringExtra(IntentParams.TRAILER_ID).toString()

        presenter = TrailerDetailsPresenter()
        presenter.attachView(this, lifecycle)

        presenter.loadTrailerDetails(if (trailerId.isEmpty()) "5e4545280dcf075d55515c2d" else trailerId)


    }



    override fun handlerResponse(trailerObj: JSONObject, t1: JSONArray) {
//var trailer = t.trailerObj
        var photos=ArrayList<String>()
        for(i in 0 until trailerObj.getJSONArray("photos").length()){
            photos.add(trailerObj.getJSONArray("photos").get(i).toString())
        }
        trailerName.text = "" +  trailerObj.get("name").toString()
        if (photos != null &&photos!!.isNotEmpty()) {
            HelperMethods.downloadImage(photos!!.get(0), context, trailerImage)
        }
        //TrailerApplication.trailerObj = trailer
        showToast("Fetched Trailer details")

        /*if(t.upsellItemsList!=null && t.upsellItemsList.isNotEmpty()) {
            upSellItemRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            upSellItemRecyclerView.adapter = UpSellItemAdapter(t.upsellItemsList, context)
        }*/

        viewTrailerCharges.setOnClickListener {
            //startActivity(Intent(this, TrailerChargesActivity::class.java))
        }

        editTrailerButton.setOnClickListener {

        }    }
}
