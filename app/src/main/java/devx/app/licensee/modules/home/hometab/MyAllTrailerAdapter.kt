package devx.app.seller.modules.home.hometab

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.TrailerApplication
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.editTrailer.TrailerDetailsActivity
import devx.app.seller.webapi.response.trailerslist.Trailers

class MyAllTrailerAdapter(
    private val context: HomeFragment,
    var trailerList: List<Trailers>,
    private val isHorizontalList: Boolean,
    private val listener: (Trailers) -> Unit
) :
    RecyclerView.Adapter<MyAllTrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(parent.context).inflate(
            R.layout.profile_your_trailer_item_layout,
            parent,
            false
        )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailers = trailerList[position]
        if(trailers.photos!!.size>0) {
            HelperMethods.downloadImage(
                trailers.photos!!.get(0).data,
                context.context,
                holder.trailerImageView
            )
        }
        holder.trailerName.text = trailers.name
        holder.trailerOwnerName.text = "VIN:"+trailers.vin


        holder.itemView.setOnClickListener { listener(trailers) }

        if(trailers.trailerAvailability.ongoing!=null){
            //var startDate=HelperMethods.formatDateTimeToString(trailers.trailerAvailability.ongoing.startDate,"yyyy-MM-dd HH:mm","UTC")
            //var endDate=HelperMethods.formatDateTimeToString(trailers.trailerAvailability.ongoing.endDate,"yyyy-MM-dd HH:mm","UTC")
            //holder.returnTime.text=HelperMethods.getCountOfDays(startDate,endDate)
            holder.returnTime.gone()
            holder.mTxtAvailabilityStatus.setText("Rental expiry")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_red_round_corner_background)
        }
        else if(trailers.trailerAvailability.upcoming!=null){
            //var startDate=HelperMethods.formatDateTimeToString(trailers.trailerAvailability.upcoming.startDate,"yyyy-MM-dd HH:mm","UTC")
            //var endDate=HelperMethods.formatDateTimeToString(trailers.trailerAvailability.upcoming.endDate,"yyyy-MM-dd HH:mm","UTC")
            //holder.returnTime.text=HelperMethods.getCountOfDays(startDate,endDate)

            holder.returnTime.gone()
            holder.mTxtAvailabilityStatus.setText("Upcoming rental")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_blue_round_corner_background)
        }
        else{
           // holder.returnTime.text="Free"

            holder.returnTime.gone()
            holder.mTxtAvailabilityStatus.setText("No bookings")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_green_round_corner_background)
        }


        /* holder.itemView.setOnClickListener {
             if(HelperMethods.isOwner()) {
                 TrailerApplication.trailerObj = trailerList[holder.adapterPosition]
                 var intent = Intent(context, TrailerDetailsActivity::class.java)
                 intent.putExtra(IntentParams.TRAILER_ID, trailerList[holder.adapterPosition]._id)
                 context.startActivity(intent)
             }
             else{
                 if (HelperMethods.checkACL("TRAILER", "UPDATE")) {

                     TrailerApplication.trailerObj = trailerList[holder.adapterPosition]
                     var intent = Intent(context, TrailerDetailsActivity::class.java)
                     intent.putExtra(IntentParams.TRAILER_ID, trailerList[holder.adapterPosition]._id)
                     context.startActivity(intent)
                 } else {
                     Toast.makeText(context,"You do not have access to update.",Toast.LENGTH_SHORT).show()
                 }
             }
         }*/
       /* holder.itemView.setOnLongClickListener {

            true
        }*/

    }



    override fun getItemCount() = trailerList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),AdapterView.OnItemLongClickListener
    {

        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val trailerOwnerName: TextView = itemView.findViewById(R.id.trailerOwnerName)
        val trailerImageView: RoundedImageView = itemView.findViewById(R.id.trailerImage)
        val returnTime: TextView = itemView.findViewById(R.id.returnTime)
        val mTxtAvailabilityStatus: TextView = itemView.findViewById(R.id.mTxtAvailabilityStatus)


        override fun onItemLongClick(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ): Boolean {

            return true
        }
    }

    fun notifyDataSetChanged(mList: List<Trailers>) {

        trailerList = mList
        notifyDataSetChanged()

    }
    interface OnLongClickListener{
        fun onLongClick( position:Int)
    }
}

