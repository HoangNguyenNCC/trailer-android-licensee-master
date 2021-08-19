package company.coutloot.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import devx.app.licensee.common.LogUtil
import devx.app.licensee.webapi.CallApi

abstract class BasePresenter<MvpView> : LifecycleObserver {

    var view: MvpView? = null
    private var viewLifecycle: Lifecycle? = null

    fun attachView(view: MvpView, viewLifecycle: Lifecycle) {
        this.view = view
        this.viewLifecycle = viewLifecycle

        viewLifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onViewDestroyed() {
        LogUtil.logD("onViewDestroyed::")
        view = null
        viewLifecycle = null
        onCleared()
    }

    abstract fun onCleared();

    private var callApiObj: CallApi = CallApi()

    val callApi: CallApi
        get() {
            if (callApiObj == null) {
                callApiObj = CallApi()
            }
            return callApiObj
        }
}

