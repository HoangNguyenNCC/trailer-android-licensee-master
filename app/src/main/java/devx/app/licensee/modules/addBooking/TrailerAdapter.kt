package devx.app.licensee.modules.addBooking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods

class TrailerAdapter(
    private val context: Context,
    var trailerList: ArrayList<TrailerItem>
) :
    RecyclerView.Adapter<TrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_up_sell_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailerItem = trailerList[position]

        holder.trailerName.text = trailerItem.name
        HelperMethods.downloadImage(trailerItem.photos[0].data,context,holder.img)
        holder.vin.show()
        holder.vin.text = "VIN: "+trailerItem.vin


        /*  if (trailerItem.isSelected ) {
              holder.itemView.setBackgroundResource(R.drawable.all_square_rounded_yellow_background_with_stroke)
          } else {
              holder.itemView.setBackgroundResource(R.drawable.round_square_white_background)
          }*/

        holder.itemView.setOnClickListener {
            trailerItem.setSelected(!trailerItem.isSelected())
            holder.itemView.setBackgroundResource(if (trailerItem.isSelected()) R.drawable.all_square_rounded_yellow_background_with_stroke else R.drawable.round_square_white_background)
           /* AddBookingActivity().changeSelectionFlag(
                trailerItem,
                holder.adapterPosition,
                if(trailerItem.isSelected) false else true
            )*/

        }

    }

    override fun getItemCount() = trailerList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
        val img: ImageView = itemView.findViewById(R.id.trailerImage)
        val vin:TextView=itemView.findViewById(R.id.trailerVIN)
    }
    fun notifyDataSetChanged(mList:List<TrailerItem>) {

        trailerList= mList as ArrayList<TrailerItem>
        notifyDataSetChanged()

    }
}