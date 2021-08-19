package devx.app.seller.modules.rentals.book

import android.os.Bundle
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.TrailerApplication
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.modules.rentals.book.upsell.RentalUpsellItemActivity
import kotlinx.android.synthetic.main.activity_rental_booking.*

class RentalBookingActivity : BaseActivity() {

    var trailer = TrailerApplication.trailerObj

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental_booking)
        CommonTopbarView().setup(this,"")

        bTrailerCapacity.text = trailer.capacity
        bTrailerSize.text = trailer.size
        bPrice.text = ""+trailer.price
        HelperMethods.downloadImage(trailer.photo, context, bTrailerPreview)

        bRentButton.setOnClickListener {
            RentalUpsellItemActivity().open(this)
        }
    }
}
