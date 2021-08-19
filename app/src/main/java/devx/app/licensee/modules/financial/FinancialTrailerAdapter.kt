package devx.app.licensee.modules.financial

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.home.requesttab.RequestInvoiceDetailsActivity
import devx.app.licensee.modules.trailerTracking.TrailerTrackingActivity
import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.FinancialsObj
import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.TotalByType

class FinancialTrailerAdapter(
    private val context: Context,
    private val financialTrailerList: List<FinancialsObj>
) : RecyclerView.Adapter<FinancialTrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.financial_trailers_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailerData = financialTrailerList[position]
        holder.trailerName.text = trailerData.trailerDetails.name
        HelperMethods.downloadImage(trailerData.trailerDetails.photos[0].data, context, holder.trailerImage)

        if(trailerData.isLicenseePaid){

            holder.earningAmount.text = String.format("%.2f", trailerData.adminPayment.transfer.amount)+ " AUD"
            holder.earningAmount.setTextColor(context.resources.getColor(R.color.green))

        }
        else{

            var incomingTotal=0.0
            var outgoingTotal=0.0
            for(i in 0 until trailerData.incoming.size){
                incomingTotal=incomingTotal+trailerData.incoming[i].amount
            }
            for(i in 0 until trailerData.outgoing.size){
                outgoingTotal=outgoingTotal+trailerData.outgoing[i].amount
            }
            var difference=incomingTotal-outgoingTotal
            holder.earningAmount.text =String.format("%.2f", difference) + " AUD"
            holder.earningAmount.setTextColor(context.resources.getColor(R.color.red))

        }
        holder.itemView.setOnClickListener {

            val intent= Intent(context, RequestInvoiceDetailsActivity::class.java )
            intent.putExtra("id",trailerData.rentalId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = financialTrailerList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerImage: RoundedImageView = itemView.findViewById(R.id.trailerImage)
        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val earningAmount: TextView = itemView.findViewById(R.id.earningAmount)

    }
}