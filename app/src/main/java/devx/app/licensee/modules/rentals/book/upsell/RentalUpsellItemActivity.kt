package devx.app.seller.modules.rentals.book.upsell

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.TrailerApplication
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.rentals.pay.PayActivity
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse
import kotlinx.android.synthetic.main.activity_rental_upsell_item.*

class RentalUpsellItemActivity : BaseActivity(), UpsellContract.View {

    fun open(context: Context) {
        var intent = Intent(context, this::class.java)
        context.startActivity(intent)
    }

    lateinit var presenter: UpsellPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_upsell_item)

        CommonTopbarView().setup(this,"")

        presenter = UpsellPresenter()
        presenter.attachView(this, lifecycle)
        presenter.load(TrailerApplication.trailerObj._id)

        payNow.setOnClickListener {
            gotoActivity(PayActivity::class.java)
        }
    }

    override fun handlerResponse(response: UpSellItemResponse) {
       /* var adapter = UpsellItemAdapter(this, response.upsellItemsList)
        val manager = GridLayoutManager(this, 2)
        upsellRecycler.layoutManager = manager
        upsellRecycler.adapter = adapter*/
    }
}
