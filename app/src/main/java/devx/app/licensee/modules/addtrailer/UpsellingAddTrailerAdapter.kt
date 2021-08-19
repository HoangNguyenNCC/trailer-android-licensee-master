package devx.app.licensee.modules.addtrailer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.webapi.response.addUpSell.UpsellItems
import devx.app.seller.webapi.response.trailerslist.Trailers

class UpsellingAddTrailerAdapter(
    private val context: Context,
    var upsellList: List<Trailers>
) :
    RecyclerView.Adapter<UpsellingAddTrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sgl_item_upsell_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailerItem = upsellList[position]

        holder.trailerName.text = trailerItem.name
        holder.imgUpsell.show()
        if(trailerItem.photos!=null) {
            HelperMethods.downloadImage(trailerItem.photos!![0].data, context, holder.imgUpsell)
        }

        if (trailerItem.flag ) {
            holder.itemView.setBackgroundResource(R.drawable.all_square_rounded_yellow_background_with_stroke)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.round_square_white_background)
        }

        holder.itemView.setOnClickListener {

            /*trailerItem.flag=!trailerItem.flag
            holder.itemView.setBackgroundResource(if (trailerItem.flag) R.drawable.all_square_rounded_yellow_background_with_stroke else R.drawable.round_square_white_background)*/
            for(item in 0  until upsellList.size){
                if(item==position){
                    upsellList[item].flag=true
                    holder.itemView.setBackgroundResource(R.drawable.all_square_rounded_yellow_background_with_stroke)
                }
                else{
                    upsellList[item].flag=false
                    holder.itemView.setBackgroundResource(R.drawable.round_square_white_background)
                }
            }
            notifyDataSetChanged(upsellList)
        }

    }

    override fun getItemCount() = upsellList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerName: TextView = itemView.findViewById(R.id.txtUpsellItem)
        val imgUpsell: RoundedImageView =itemView.findViewById(R.id.imgUpsellItem)
    }
    fun notifyDataSetChanged(mList:List<Trailers>) {

        upsellList= mList as ArrayList<Trailers>
        notifyDataSetChanged()

    }
}