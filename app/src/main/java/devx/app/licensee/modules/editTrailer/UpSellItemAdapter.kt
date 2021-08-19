package devx.app.licensee.modules.editTrailer

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

class UpSellItemAdapter(var upSellItemsList: ArrayList<UpsellItems>, var context: Context) :
    RecyclerView.Adapter<UpSellItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.up_sell_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upSellItems = upSellItemsList[position]
        HelperMethods.downloadImage(upSellItems.photo!!.data, context, holder.upSellItemImage)
        holder.upSellItemName.text = upSellItems.name
        holder.upSellItemPrice.text = upSellItems.price
    }

    override fun getItemCount() = upSellItemsList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var upSellItemImage: ImageView = itemView.findViewById(R.id.upSellItemImage)
        var upSellItemName: TextView = itemView.findViewById(R.id.upSellItemName)
        var upSellItemPrice: TextView = itemView.findViewById(R.id.upSellItemPrice)
    }
}