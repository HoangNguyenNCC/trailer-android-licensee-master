package devx.app.licensee.modules.home.notiftab

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.webapi.response.requelist.RentalRequest
import devx.app.seller.modules.home.hometab.RequestTrailerFragment
import devx.app.seller.modules.home.notiftab.NotificationFragment
import devx.app.seller.webapi.response.notification.Notifications

class NotificationTrailerListAdapter(
    private val notificationFragment: NotificationFragment,
    private var rentalList: List<Notifications>
) :
    RecyclerView.Adapter<NotificationTrailerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_trailers_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = rentalList[position]
        holder.notificationTitle.text = notification.reminderType

        holder.trailerName.text = notification.itemName

        holder.remainingTime.text = notification.statusText
        holder.remainingTime.setTextColor(Color.parseColor(notification.reminderColor))


        if(notification.invoiceId!=null) {
            HelperMethods.downloadImage(
                notification.rentedItems[0].itemPhoto.data,
                notificationFragment.context,
                holder.trailerImageView
            )
        }
        else{
            HelperMethods.downloadImage(
                notification.itemPhoto.data,
                notificationFragment.context,
                holder.trailerImageView
            )
        }
        holder.itemView.setOnClickListener {
            if(notification.invoiceId!=null) {
                notificationFragment.showInvoiceDetails(notification)
            }

        }
    }

    override fun getItemCount() = rentalList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerImageView: ImageView = itemView.findViewById(R.id.trailerImage)
        val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val remainingTime: TextView = itemView.findViewById(R.id.remainingTime)
    }
    fun notifyDataSetChanged(mList:List<Notifications>) {

        rentalList= mList as ArrayList<Notifications>
        notifyDataSetChanged()

    }
}