package devx.app.seller.modules.rentals.book.cancel

import company.coutloot.common.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import devx.app.seller.webapi.response.CommonResponse
class CancelTrailerPresenter : CancelTrailerContract.Presenter, BasePresenter<CancelTrailerContract.View>() {

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
    }

    override fun cancelTrailer(id: String, canceledBy: String) {
        view?.showProgressDialog()
        disposable.add(
            callApi!!
                .cancelTrailer(id, "", "user", "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<CommonResponse>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {
                        view?.dismissProgressDialog()
                        view?.showToast("Something went wrong")
                        view?.exit()
                    }

                    override fun onNext(response: CommonResponse) {
                        view?.dismissProgressDialog()

                        if (response != null && response.success) {
                            view?.handlerResponse(response)
                        } else {
                            view?.showToast(response.message)
                        }
                    }
                }
                ))
    }

}