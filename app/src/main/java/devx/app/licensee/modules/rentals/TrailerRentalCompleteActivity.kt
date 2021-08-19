package devx.app.seller.modules.rentals

import android.os.Bundle
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.webapi.CallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_trailer_rental_complete.*
import devx.app.seller.webapi.response.CommonResponse
class TrailerRentalCompleteActivity : BaseActivity() {

    private var trailerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer_rental_complete)

        if (intent != null && intent.hasExtra(IntentParams.TRAILER_ID))
            trailerId = intent.getStringExtra(IntentParams.TRAILER_ID).toString()

        rentalRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            showToast(rating.toString())
        }

        skipRatingButton.setOnClickListener {
            finish()
        }

        sendRenalFeedback.setOnClickListener {
            CallApi().rateTrailer(
                "trailer",
                trailerId,
                HelperMethods.getUserId(),
                rentalRatingBar.rating.toString()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<CommonResponse>() {
                    override fun onComplete() {
                    }

                    override fun onNext(response: CommonResponse) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
        }
    }
}
