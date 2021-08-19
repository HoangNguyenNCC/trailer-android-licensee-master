package devx.app.licensee.modules.home.hometab

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.BoldTextView
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.addUpSellItem.AddUpSellItemActivity
import devx.app.licensee.webapi.response.addUpSell.UpsellItems
import devx.app.seller.modules.home.hometab.HomeFragment

class UpsellItemAdapter(
    private val context: HomeFragment,
    var upsellList: List<UpsellItems>
) :
    RecyclerView.Adapter<UpsellItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sgl_item_upsell_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upsellItem = upsellList[position]

        holder.trailerName.text = upsellItem.name
        holder.imgUpsell.show()
        holder.trailerName.textSize=14.0f
        if(upsellItem.photos!!.size>0) {
            HelperMethods.downloadImage(upsellItem.photos!![0].data,context.context, holder.imgUpsell)
        }
        holder.mTxtAvailabilityStatus.show()
        holder.txtUpsellItemAvailabilityStatus.gone()
        if(!upsellItem.availability){
            //holder.txtUpsellItemAvailabilityStatus.gone()
            holder.mTxtAvailabilityStatus.setText("Unavailable")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_red_round_corner_background)
        }
        else{
            //holder.txtUpsellItemAvailabilityStatus.show()
            //holder.txtUpsellItemAvailabilityStatus.setText("Free")
            holder.mTxtAvailabilityStatus.setText("Available")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_green_round_corner_background)
        }

        holder.itemView.setOnClickListener {
            if(HelperMethods.isOwner()) {
                var intent = Intent(context.context, AddUpSellItemActivity::class.java)
                intent.putExtra(IntentParams.UPSELL_ID , upsellItem._id)
                intent.putExtra("flag" , "edit")
                context.startActivity(intent)
            }
            else{
                if (HelperMethods.checkACL("UPSELL", "UPDATE")) {
                    var intent = Intent(context.context, AddUpSellItemActivity::class.java)
                    intent.putExtra(IntentParams.UPSELL_ID , upsellItem._id)
                    intent.putExtra("flag" , "edit")
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context.context,"You do not have access to update.", Toast.LENGTH_SHORT).show()
                }
            }

        }
        holder.itemView.setOnLongClickListener {
            if(HelperMethods.isOwner()) {
                showDeleteAlert(position)
            }
            else{
                if (HelperMethods.checkACL("UPSELL", "DELETE")) {
                    showDeleteAlert(position)
                } else {
                    Toast.makeText(context.context,"You do not have access to delete.",Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun showDeleteAlert(position: Int) {
        val builder = AlertDialog.Builder(context.context)
        //set title for alert dialog
        builder.setTitle("Delete Upsell")
        //set message for alert dialog
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            context.deleteUpsell( upsellList[position]._id)
            //Toast.makeText(context,"clicked yes", Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            //Toast.makeText(context,"clicked No", Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        val positiveColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        val negativeColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        alertDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        alertDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }

    override fun getItemCount() = upsellList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerName: BoldTextView= itemView.findViewById(R.id.txtUpsellItem)
        val imgUpsell: RoundedImageView =itemView.findViewById(R.id.imgUpsellItem)
        val mTxtAvailabilityStatus: BoldTextView =itemView.findViewById(R.id.mTxtAvailabilityStatus)
        val txtUpsellItemAvailabilityStatus: BoldTextView=itemView.findViewById(R.id.txtUpsellItemAvailabilityStatus)
    }
    fun notifyDataSetChanged(mList:List<UpsellItems>) {

        upsellList= mList as ArrayList<UpsellItems>
        notifyDataSetChanged()

    }
}