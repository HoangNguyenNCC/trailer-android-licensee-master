package devx.app.seller.modules.rentals.book.upsell

import company.coutloot.common.BasePresenter
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class UpsellPresenter : UpsellContract.Presenter, BasePresenter<UpsellContract.View>() {

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
    }

    override fun load(id: String) {
        view?.showProgressDialog()
        disposable.add(
            callApi!!
                .loadUpsellItems(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<UpSellItemResponse>() {
                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        view?.dismissProgressDialog()
                        view?.showToast("Something went wrong")
                        view?.exit()
                    }

                    override fun onNext(response: UpSellItemResponse) {
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