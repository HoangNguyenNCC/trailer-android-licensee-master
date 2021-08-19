package devx.app.licensee.modules.addtrailer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.addUpSellItem.AddUpSellItemActivity
import devx.app.licensee.webapi.response.addUpSell.UpsellItems

class SelectUpSellItemAdapter(
    private val context: Context,
    private val requestType: String,
    private val itemList: List<UpsellItems>,
    private val isAddInsurance: Boolean,
    private val isAddServicing: Boolean
) : RecyclerView.Adapter<SelectUpSellItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_up_sell_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.trailerName.text = itemList[position].name
        if(itemList[position].photos!=null) {
            HelperMethods.downloadImage(
                itemList[position].photos!![0].data,
                context,
                holder.trailerImage
            )
        }

        holder.itemView.setOnClickListener {
            val intent: Intent?
            if (isAddInsurance) {
                intent = Intent(context, AddTrailerInsuranceActivity::class.java)
                intent.putExtra(IntentParams.ITEM_TYPE, requestType)
            } else if(isAddServicing){
                intent = Intent(context, AddTrailerServicingActivity::class.java)
                intent.putExtra(IntentParams.ITEM_TYPE, requestType)
            }else {
                intent = Intent(context, AddUpSellItemActivity::class.java)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerImage: ImageView = itemView.findViewById(R.id.trailerImage)
        val trailerName: TextView = itemView.findViewById(R.id.trailerName)
    }
}