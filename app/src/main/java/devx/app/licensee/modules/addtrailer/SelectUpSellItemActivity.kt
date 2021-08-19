package devx.app.licensee.modules.addtrailer

import android.os.Bundle
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.PlaceHolderAdapter
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse
import devx.app.seller.common.CommonTopbarView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_up_sell_item.*

class SelectUpSellItemActivity : BaseActivity() {

    private var isAddInsurance = false
    private var isAddServicing= false
    private var trailerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_up_sell_item)
        CommonTopbarView().setup(this)

        trailerId = if (intent.hasExtra(IntentParams.TRAILER_ID))
            intent.getStringExtra(IntentParams.TRAILER_ID)!!
        else
            ""

        if (intent != null && intent.hasExtra(IntentParams.IS_ADD_INSURANCE)) {
            isAddInsurance = intent.getBooleanExtra(
                IntentParams.IS_ADD_INSURANCE,
                false
            )

            isAddServicing = intent.getBooleanExtra(
                IntentParams.IS_ADD_SERVICING,
                false
            )

            if(isAddInsurance)
            topHeader.text = "Add Insurance"
            else
            topHeader.text = "Add Servicing"

            bottomHeader.text = "Select item"
            requestTypeLayout.show()
            getRequestedList(Constants.TRAILER)

            trailersOption.setOnClickListener {
                resetOptionLayoutUI()
                trailersOption.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
                trailersOption.setTextColor(ResourcesUtil.getColor(R.color.black))
                getRequestedList(Constants.TRAILER)
            }

            upSellItemsOption.setOnClickListener {
                resetOptionLayoutUI()
                upSellItemsOption.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
                upSellItemsOption.setTextColor(ResourcesUtil.getColor(R.color.black))
                getRequestedList(Constants.UP_SELL_ITEM)
            }
        } else
            getRequestedList(Constants.UP_SELL_ITEM)
    }

    private fun getRequestedList(requestType: String) {
        showProgressDialog()
        upSellItemRecyclerView.adapter = PlaceHolderAdapter(context, R.layout.trailer1_placeholder)
        val endPoint = if (requestType == Constants.TRAILER) {
            "licensee/trailers?"
        } else
            "licensee/upsellitems?"

        val requestMap = HashMap<String, String>()
        requestMap["trailerId"] = trailerId

        CallApi().getUpSellOrTrailerItems(ConstantApi.LIVE_BASE_URL + endPoint, requestMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<AddUpSellItemResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: AddUpSellItemResponse) {
                    if (response != null && response.success ) {
                        if (requestType == Constants.TRAILER) {
                            upSellItemRecyclerView.adapter = SelectUpSellItemAdapter(
                                this@SelectUpSellItemActivity,
                                requestType,
                                response.trailersList,
                                isAddInsurance,
                                isAddServicing
                            )
                        } else {
                            upSellItemRecyclerView.adapter = SelectUpSellItemAdapter(
                                this@SelectUpSellItemActivity,
                                requestType,
                                response.upsellItemsList,
                                isAddInsurance,
                                isAddServicing
                            )
                        }
                        dismissProgressDialog()
                    } else
                        showToast(safeText(response.message))
                }

                override fun onError(e: Throwable) {
                    showToast(e.message)
                    dismissProgressDialog()
                }
            })
    }

    private fun resetOptionLayoutUI() {
        trailersOption.setBackgroundResource(0)
        trailersOption.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))
        upSellItemsOption.setBackgroundResource(0)
        upSellItemsOption.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))
    }
}
