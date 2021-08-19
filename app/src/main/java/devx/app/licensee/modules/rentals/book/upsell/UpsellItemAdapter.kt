package devx.app.seller.modules.rentals.book.upsell

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.webapi.response.upSellItem.UpsellItems

class UpsellItemAdapter(
    private val context: Context,
    private val upSellItemList: List<UpsellItems>
) :
    RecyclerView.Adapter<UpsellItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upsellitem_booking, parent, false)
        return ViewHolder(view)
    }

    var sel=0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.trailerTitle.text = upSellItemList[position].name
        holder.trailerPrice.text = upSellItemList[position].price
        HelperMethods.downloadImage(upSellItemList[position].photos!![0].data, context, holder.trailerImage)

//        if(sel==position){
//            holder.addBtn.text = "Added"
//            holder.addBtn.setTextColor(ResourcesUtil.getColor(R.color.themeLight))
//            holder.addBtn.background = ResourcesUtil.getDrawableById(R.drawable.complete_gray_round_background)
//        }else{
//            holder.addBtn.text = "Add"
//            holder.addBtn.setTextColor(ResourcesUtil.getColor(R.color.white))
//            holder.addBtn.background = ResourcesUtil.getDrawableById(R.drawable.complete_lightblue_round_background)
//        }

        holder.addBtn.setOnClickListener {


            sel = holder.adapterPosition
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {
        return upSellItemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val addBtn = itemView.findViewById<TextView>(R.id.addBtn)
        val trailerTitle = itemView.findViewById<TextView>(R.id.trailerTitle)
        val trailerPrice = itemView.findViewById<TextView>(R.id.trailerPrice)
        val trailerImage = itemView.findViewById<ImageView>(R.id.trailerImage)
    }

}