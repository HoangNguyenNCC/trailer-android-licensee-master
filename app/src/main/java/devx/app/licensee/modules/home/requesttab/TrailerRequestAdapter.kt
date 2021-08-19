package devx.app.licensee.modules.home.requesttab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.SharedPrefsUtils
import devx.app.licensee.webapi.response.requelist.RentalRequest
import devx.app.seller.modules.home.hometab.RequestTrailerFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrailerRequestAdapter(
        val context : Context,
    private val requestTrailerFragment: RequestTrailerFragment,
    private var rentalList: List<RentalRequest>
    //private val requestType: String
) :
    RecyclerView.Adapter<TrailerRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trailer_request_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = rentalList[position];
        holder.trailerName.text = notification.rentedItems.get(0).name
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())

        holder.returnTime.text = HelperMethods.getCountOfDays(currentDateandTime,notification.rentalPeriodStart)
        holder.trailerOwnerName.text = notification.customerName
        if(notification.rentedItems[0].photos.data!=null) {
            HelperMethods.downloadImage(
                notification.rentedItems[0].photos.data,
                requestTrailerFragment.context,
                holder.trailerImageView
            )
        }

        when (notification.requestType) {
            "rental" -> {
                when(notification.isApproved){
                    1->{
                        holder.rightSideButton.text="Rental Approved"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_blue_round_background)
                    }
                    2->{
                        holder.rightSideButton.text="Rental Rejected"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_red_round_background)
                    }
                    else -> {
                        holder.rightSideButton.text = "Rental Request"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_orange_round_background)
                    }
                    }

                }


            "cancellation" -> {
                when(notification.isApproved){
                    1->{
                        holder.rightSideButton.text="Cancelled"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_blue_round_background)
                    }
                    2->{
                        holder.rightSideButton.text="Cancelled"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_red_round_background)
                    }
                    else -> {
                        holder.rightSideButton.text = "Cancellation Request"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_orange_round_background)
                    }

                }

            }
            "extension" -> {
                when(notification.isApproved){

                    1->{
                        holder.rightSideButton.text="Extension Approved"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_blue_round_background)
                    }
                    2->{
                        holder.rightSideButton.text="Extension Rejected"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_red_round_background)
                    }
                    else -> {
                        holder.rightSideButton.text = "Extension Request"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_orange_round_background)
                    }

                }

            }
            "reschedule" -> {
                when(notification.isApproved){


                    1->{
                        holder.rightSideButton.text="Reschedule Approved"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_blue_round_background)
                    }
                    2->{
                        holder.rightSideButton.text="Reschedule Rejected"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_red_round_background)
                    }
                    else -> {
                        holder.rightSideButton.text = "Reschedule Request"
                        holder.rightSideButton.setBackgroundResource(R.drawable.complete_orange_round_background)
                    }


                }
            }
        }


        holder.itemView.setOnClickListener {
            if(notification.isTodayDropOff){
                SharedPrefsUtils.setStringPreference(context, "Dropoff", "true");
            }
            else
                SharedPrefsUtils.setStringPreference(context, "Dropoff", "false");
            requestTrailerFragment.showInvoiceDetails(notification)
        }
    }

    override fun getItemCount(): Int {
        return rentalList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val trailerOwnerName: TextView = itemView.findViewById(R.id.trailerOwnerName)
        val trailerImageView: RoundedImageView = itemView.findViewById(R.id.trailerImage)
        val rightSideButton: TextView = itemView.findViewById(R.id.rightSideButton)
        val returnTime: TextView = itemView.findViewById(R.id.returnTime)
        val returnComment: TextView = itemView.findViewById(R.id.returnComment)
    }
    fun notifyDataSetChanged(mList:List<RentalRequest>) {

        rentalList= mList as ArrayList<RentalRequest>
        notifyDataSetChanged()

    }
}