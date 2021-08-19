package devx.app.seller.modules.rentals.pay

import android.os.Bundle
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.common.CommonTopbarView
import kotlinx.android.synthetic.main.activity_payment.*

class PayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        CommonTopbarView().setup(this, "")

        payNow.setOnClickListener {
            showToast("Thanks for payment! Order placed successfully!")
            HelperMethods.closeEverythingOpenHome(this)
        }
    }
}
