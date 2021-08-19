package devx.app.licensee.modules.addtrailer

import com.google.gson.JsonObject
import company.coutloot.common.BasePresenter
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class AddTrailerPresenter : AddTrailerContract.Presenter,
    BasePresenter<AddTrailerContract.View>() {

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

                       /* if (response != null) {
                            view?.handlerResponse(response)
                        } else {
                            view?.showToast(response.message)
                        }*/
                    }
                }
                ))
    }

}