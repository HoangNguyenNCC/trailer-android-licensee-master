package devx.app.licensee.modules.inviteEmployees

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.seller.webapi.response.trailerslist.Trailers

class InvitePermissionAdapter(
    private val bottomSheet: PermissionBottomSheetFragment,
    var permissionList: ArrayList<PermissionItem>
) :
    RecyclerView.Adapter<InvitePermissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.invite_permission_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permissionItem = permissionList[position]

        holder.permissionName.text = permissionItem.atribute

        if (permissionItem.isPermissionSelected == 1) {
            holder.itemView.setBackgroundResource(R.drawable.all_square_rounded_yellow_background_with_stroke)
            holder.cancelLayout.show()
        } else {
            holder.itemView.setBackgroundResource(R.drawable.round_square_white_background)
            holder.cancelLayout.gone()
        }

        holder.itemView.setOnClickListener {
            bottomSheet.showAddPrivilegeDialog(
                permissionItem,
                holder.adapterPosition
            )
        }

        holder.cancelLayout.setOnClickListener {
            bottomSheet.removeSelectedPermission(
                permissionItem.atribute,
                holder.adapterPosition
            )
        }
    }

    override fun getItemCount() = permissionList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val permissionName: TextView = itemView.findViewById(R.id.permissionName)
        val cancelLayout: LinearLayout = itemView.findViewById(R.id.cancelLayout)
    }
    fun notifyDataSetChanged(mList:List<PermissionItem>) {

        permissionList= mList as ArrayList<PermissionItem>
        notifyDataSetChanged()

    }
}