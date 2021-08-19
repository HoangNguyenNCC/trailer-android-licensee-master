package devx.app.licensee.modules.financial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.TrailerFinancialResponse
import devx.app.seller.common.CommonTopbarView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_financial.*

class FinancialActivity : BaseActivity(), OnChartValueSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial)
        CommonTopbarView().setup(this)

        financialTrailerList.layoutManager = LinearLayoutManager(this)
        financialTrailerList.adapter = FinancialTrailerAdapter(this, arrayListOf())

        getTrailerFinancialData()
    }

    private fun setUpLineChart(list:List<Entry>) {
        val lineDataSet = LineDataSet(list, "Earning")
        lineDataSet.color = ResourcesUtil.getColor(R.color.themeDark)
        lineDataSet.lineWidth = 2f

        val dataSet = arrayListOf<ILineDataSet>()
        dataSet.add(lineDataSet)

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun getLineDataValues(): List<Entry> {
        val dataValues = arrayListOf<Entry>()
        dataValues.add(Entry(0f, 20f))
        dataValues.add(Entry(1f, 24f))
        dataValues.add(Entry(2f, 2f))
        dataValues.add(Entry(3f, 10f))
        dataValues.add(Entry(4f, 28f))
        return dataValues
    }

    private fun getTrailerFinancialData() {
        showProgressDialog()
        var temp=""
        if(HelperMethods.isOwner()) {
            temp="licensee"
        }
        else {
            temp="employee"
        }
        CallApi().getTrailerFinancialData(temp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<TrailerFinancialResponse>() {
                override fun onComplete() {
                }

                override fun onNext(response: TrailerFinancialResponse) {
                    if (response != null && response.success && response.finances != null) {
                        if(response.finances.size>0){

                            txtEarnings.show()
                            financialTrailerList.show()
                            txtNoFinancial.show()
                            financialTrailerList.layoutManager =
                                LinearLayoutManager(this@FinancialActivity)
                            financialTrailerList.adapter = FinancialTrailerAdapter(
                                this@FinancialActivity,
                                response.finances
                            )

                            var incomingTotal = 0.0
                            val dataValues = arrayListOf<Entry>()
                            for(i in 0 until response.finances.size) {
                                if(response.finances[i].isLicenseePaid){
                                    incomingTotal = incomingTotal + response.finances[i].adminPayment.transfer.amount
                                    dataValues.add(Entry(0f, incomingTotal.toFloat()))
                                }
                            }
                            txtEarnings.text=String.format("%.2f",incomingTotal)+"AUD"
                            setUpLineChart(dataValues)

                        }
                        else{
                            txtEarnings.gone()
                            financialTrailerList.gone()
                            txtNoFinancial.gone()
                        }

                    } else
                        showToast(HelperMethods.safeText(response.message, "Something went wrong"))
                    dismissProgressDialog()
                }

                override fun onError(e: Throwable) {
                    dismissProgressDialog()
                    showToast(e.message)
                }
            })
    }

    override fun onNothingSelected() {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }
}
