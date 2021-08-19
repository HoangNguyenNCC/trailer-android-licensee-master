package devx.app.licensee.modules.addBooking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.webapi.response.trailerslist.Trailers

class UpsellItemAdapter(
    private val context: Context,
    var upsellList: ArrayList<UpsellItem>
) :
    RecyclerView.Adapter<UpsellItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_up_sell_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upsellItem = upsellList[position]

        holder.trailerName.text = upsellItem.name

        HelperMethods.downloadImage(upsellItem.photos[0].data,context,holder.img)
      /*  if (upsellItem.isSelected ) {
            holder.itemView.setBackgroundResource(R.drawable.all_square_rounded_yellow_background_with_stroke)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.round_square_white_background)
        }*/

        holder.itemView.setOnClickListener {
            upsellItem.setSelected(!upsellItem.isSelected())
            holder.itemView.setBackgroundResource(if (upsellItem.isSelected()) R.drawable.all_square_rounded_yellow_background_with_stroke else R.drawable.round_square_white_background)
           /* AddBookingActivity().changeUpsellItemSelectionFlag(
                upsellItem,
                holder.adapterPosition,
                if(upsellItem.isSelected) false else true
            )*/

        }

    }

    override fun getItemCount() = upsellList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val img: ImageView = itemView.findViewById(R.id.trailerImage)
    }
    fun notifyDataSetChanged(mList:List<UpsellItem>) {

        upsellList= mList as ArrayList<UpsellItem>
        notifyDataSetChanged()

    }
}