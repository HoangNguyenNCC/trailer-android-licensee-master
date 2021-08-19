package devx.app.seller.modules.rentals.book.cancel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.rentals.details.TrailerDetailsContract
import devx.app.seller.modules.rentals.details.TrailerDetailsPresenter
import kotlinx.android.synthetic.main.activity_cancel_trailer.*
import org.json.JSONArray
import org.json.JSONObject
import devx.app.seller.webapi.response.CommonResponse
class CancelTrailerActivity : BaseActivity(), TrailerDetailsContract.View,
    CancelTrailerContract.View {

    lateinit var presenter: TrailerDetailsPresenter
    lateinit var cancelPresenter: CancelTrailerPresenter
    var trailerId = ""

    fun openTrailer(context: Context, trailerId: String) {
        var intent = Intent(context, this::class.java)
        intent.putExtra(IntentParams.TRAILER_ID, trailerId)
        context.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_trailer)
        CommonTopbarView().setup(this)

        presenter = TrailerDetailsPresenter()
        cancelPresenter = CancelTrailerPresenter()
        presenter.attachView(this, lifecycle)
        cancelPresenter.attachView(this, lifecycle)
        presenter.loadTrailerDetails("5e58914c9d61b40017de39f8")

        cancelTrailerReqButton.setOnClickListener {
            cancelPresenter.cancelTrailer(trailerId, licenseeId)
        }
    }

    private var licenseeId = ""



    private fun setTrailerSmallImage(imageList: List<String>?) {
        val rTrailerPreviewImagesLL = findViewById<LinearLayout>(R.id.rTrailerPreviewImagesLL)
        rTrailerPreviewImagesLL.removeAllViews()

        for (i in imageList?.indices!!) {
            val view = layoutInflater.inflate(R.layout.item_image_layout, null, false)
            val rTrailerPreviewImages = view.findViewById<ImageView>(R.id.rTrailerPreviewImages)
            HelperMethods.downloadImage(imageList[i], context, rTrailerPreviewImages)

            rTrailerPreviewImagesLL.addView(view)
        }
    }


    override fun handlerResponse(t: JSONObject, t1: JSONArray) {
        /* val trailer = response.trailerObj
               licenseeId = trailer.licenseeId
               TrailerApplication.trailerObj = trailer
               rTopT2.text = trailer.name
               rTrailerDetails.text = trailer.description
               rPrice.text = "" + trailer.dlrCharges + " / HR"
               setTrailerSmallImage(response.trailerObj.photos)*/
    }

    override fun handlerResponse(t: CommonResponse) {
        TODO("Not yet implemented")
    }


}
