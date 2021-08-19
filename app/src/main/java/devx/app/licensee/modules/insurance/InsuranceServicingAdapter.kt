package devx.app.licensee.modules.insurance

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.addtrailer.AddTrailerInsuranceActivity
import devx.app.licensee.modules.addtrailer.AddTrailerServicingActivity
import devx.app.licensee.webapi.response.InsuranceResponse
import devx.app.seller.webapi.response.upSellItem.UpsellItems
import kotlinx.android.synthetic.main.activity_verify_otp.view.*

class InsuranceServicingAdapter(var insuranceList: ArrayList<InsuranceResponse>, var context: Context, var flag:String) :
    RecyclerView.Adapter<InsuranceServicingAdapter.ViewHolder>() {

    var issueDate=""
    var expiryDate=""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sgl_item_insurance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = insuranceList[position]
        if(flag=="insurance"){
            holder.txtServiceName.gone()
            holder.txtTrailerName.text=item.trailerName
            holder.txtIssueDate.text = HelperMethods.convertUTCtoNormal(item.issueDate)
            holder.txtExpiryDate.text =HelperMethods.convertUTCtoNormal(item.expiryDate)
            holder.txtLblIssueDate.text ="Issued Date: "
            holder.txtLblExpiryDate.text ="Expiry Date: "
        }
        else{
            holder.txtServiceName.show()
            holder.txtTrailerName.text=item.trailerName
            holder.txtServiceName.text=item.name
            holder.txtIssueDate.text = HelperMethods.convertUTCtoNormal(item.serviceDate)
            holder.txtExpiryDate.text =HelperMethods.convertUTCtoNormal(item.nextDueDate)
            holder.txtLblIssueDate.text ="Serviced Date: "
            holder.txtLblExpiryDate.text ="Next Due Date: "
        }


        /*holder.imgDoc.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.document))
            context.startActivity(browserIntent)
        }*/
        holder.itemView.setOnClickListener {
            if(flag=="insurance"){
                var intent=Intent(context, AddTrailerInsuranceActivity::class.java)
                intent.putExtra("flag","edit")
                intent.putExtra("trailerId",item.itemId)
                intent.putExtra("document",item.document)
                intent.putExtra("trailerName",item.trailerName)
                intent.putExtra("photo",item.photo)
                intent.putExtra("issueDate",holder.txtIssueDate.text.toString())
                intent.putExtra("expiryDate",holder.txtExpiryDate.text.toString())
                intent.putExtra("_id",item._id)
                context.startActivity(intent)
            }
            else{
                var intent= Intent(context, AddTrailerServicingActivity::class.java)
                intent.putExtra("flag","edit")
                intent.putExtra("trailerId",item.itemId)
                intent.putExtra("document",item.document)
                intent.putExtra("trailerName",item.trailerName)
                intent.putExtra("photo",item.photo)
                intent.putExtra("serviceDate",holder.txtIssueDate.text.toString())
                intent.putExtra("nextDueDate",holder.txtExpiryDate.text.toString())
                intent.putExtra("name", holder.txtServiceName.text.toString())
                intent.putExtra("_id",item._id)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount() = insuranceList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTrailerName: TextView = itemView.findViewById(R.id.txtTrailerName)
        var txtIssueDate: TextView = itemView.findViewById(R.id.txtIssueDate)
        var txtExpiryDate: TextView = itemView.findViewById(R.id.txtExpiryDate)
        var txtLblIssueDate: TextView = itemView.findViewById(R.id.txtLbltxtIssueDate)
        var txtLblExpiryDate: TextView = itemView.findViewById(R.id.txtLblExpiryDate)
        var txtServiceName: TextView = itemView.findViewById(R.id.txtServiceName)
        var imgDoc=itemView.findViewById<ImageView>(R.id.imgDocument)
    }
}