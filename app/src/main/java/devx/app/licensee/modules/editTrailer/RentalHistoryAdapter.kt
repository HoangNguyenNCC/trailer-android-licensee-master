package devx.app.licensee.modules.editTrailer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import de.hdodenhof.circleimageview.CircleImageView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.home.requesttab.RequestInvoiceDetailsActivity
import devx.app.licensee.webapi.response.RentalHistory
import devx.app.seller.webapi.response.upSellItem.UpsellItems
import kotlinx.android.synthetic.main.activity_verify_otp.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RentalHistoryAdapter(var rentalHistory: ArrayList<RentalHistory>, var context: Context, var flag:String) :
    RecyclerView.Adapter<RentalHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sgl_item_rental_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rental = rentalHistory[position]
        holder.txtIssueDate.text = changeDateFormat(rental.start)
        holder.txtServiceName.text=rental.businessName
        holder.txtExpiryDate.text =changeDateFormat(rental.end)
        HelperMethods.downloadImage(rental.photo,context,holder.img)

        holder.itemView.setOnClickListener{
            val intent= Intent(context, RequestInvoiceDetailsActivity::class.java )
            intent.putExtra("id",rental.invoiceId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = rentalHistory.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: CircleImageView = itemView.findViewById(R.id.imgItem)
        var txtIssueDate: TextView = itemView.findViewById(R.id.txtRentalStartDate)
        var txtExpiryDate: TextView = itemView.findViewById(R.id.txtRentalEndDate)
        var txtServiceName: TextView = itemView.findViewById(R.id.txtItemName)
    }
    private fun changeDateFormat(date: String?):String {
        var temp="";
        val titleDate: Date
        try {
            titleDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date)
            temp= SimpleDateFormat("dd MMM").format(titleDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return temp
    }
}