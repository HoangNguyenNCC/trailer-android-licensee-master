package devx.app.seller.modules.rentals.details

import com.google.gson.JsonObject
import company.coutloot.common.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


class TrailerDetailsPresenter : TrailerDetailsContract.Presenter,
    BasePresenter<TrailerDetailsContract.View>() {

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
    }

    override fun loadTrailerDetails(id: String) {
        view?.showProgressDialog()
        disposable.add(
            callApi!!
                .loadTrailerDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {
                        view?.dismissProgressDialog()
                        view?.showToast("Something went wrong")
                    }

                    override fun onNext(response: JSONObject) {
                        view?.dismissProgressDialog()
                        val jsonObject: JsonObject = response.get("trailerObj") as JsonObject
                        //val jsonString: String = response.body().toString()
                       /* if (response != null ) {
                            view?.handlerResponse(jsonObject.getAsJsonObject("trailerObj"),jsonObject.getAsJsonArray("upsellItemsList"))
                        } else {
                            view?.showToast(jsonObject.get("message").toString())
                        }*/
                    }
                }
                ))
    }

}