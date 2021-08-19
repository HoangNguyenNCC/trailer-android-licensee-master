package devx.app.licensee.modules.search
//
//package devx.app.seller.modules.search
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import androidx.recyclerview.widget.LinearLayoutManager
//import devx.app.licensee.R
//import devx.app.licensee.common.BaseActivity
//import devx.app.licensee.common.RxSearch
//import devx.app.licensee.webapi.CallApi
//import devx.app.seller.modules.home.hometab.MyAllTrailerAdapter
//import devx.app.seller.webapi.response.trailerslist.TrailerListResp
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.observers.DisposableObserver
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_seach_trailer.*
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//class SearchTrailerActivity : BaseActivity() {
//
//    private var linearLayoutManager: LinearLayoutManager? = null
//    var searchText=""
//
//    @SuppressLint("CheckResult")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_seach_trailer)
//
////        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
////        searchTrailerRecyclerView.layoutManager = linearLayoutManager
////        searchTrailerRecyclerView.isNestedScrollingEnabled = true
////        searchTrailerRecyclerView.adapter = FeaturedTrailerAdapter(this, false)
//
//        advanceSearchTV.setOnClickListener {
//            val bottomSheet = AdvanceSearchBottomSheet()
//            bottomSheet.show(supportFragmentManager, "")
//        }
//
//        RxSearch.fromView(txtSearchTrailer)
//            .debounce(40, TimeUnit.MILLISECONDS)
//            .distinctUntilChanged()
//            .switchMap {
//                searchText = it.trim()
//
//                val requestMap = HashMap<String, String>()
//                requestMap["name"] = searchText
//                    CallApi().getFeaturedTrailers(requestMap)
//
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableObserver<TrailerListResp>() {
//                override fun onComplete() {
//                }
//
//                override fun onNext(t: TrailerListResp) {
//
//                    if (t.success != 1) return
//                    if( t.trailersList==null) return
//                    if( t.trailersList.isEmpty()) return
//
//
//                    var adapter = MyAllTrailerAdapter(context!!, t.trailersList, false)
//                    linearLayoutManager =
//                        LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
//                    searchTrailerRecyclerView.layoutManager = linearLayoutManager
//                    searchTrailerRecyclerView.isNestedScrollingEnabled = true
//                    searchTrailerRecyclerView.adapter = adapter
//
//                }
//
//                override fun onError(e: Throwable) {
//                    runOnUiThread {
//
//                    }
//                }
//            })
//    }
//
//    public fun getFeaturedTrailer(size:String) {
//
//        showProgressDialog()
//        val requestMap = HashMap<String, String>()
//        requestMap["name"] = searchText
//        requestMap["size"] = size
//
//
//        CallApi().getFeaturedTrailers(requestMap)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : DisposableObserver<TrailerListResp>() {
//                override fun onComplete() {
//                }
//
//                override fun onNext(t: TrailerListResp) {
//                    if(!isAllOk()){return}
//                    dismissProgressDialog()
//                    if (t.success == 1) {
//
//                        var adapter = MyAllTrailerAdapter(context!!, t.trailersList, false)
//                        linearLayoutManager =
//                            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
//                        searchTrailerRecyclerView.layoutManager = linearLayoutManager
//                        searchTrailerRecyclerView.isNestedScrollingEnabled = true
//                        searchTrailerRecyclerView.adapter = adapter
//                    }
//
//
//                }
//
//                override fun onError(e: Throwable) {
//                    if(isAllOk())
//                    dismissProgressDialog()
//                }
//
//            })
//    }
//}
