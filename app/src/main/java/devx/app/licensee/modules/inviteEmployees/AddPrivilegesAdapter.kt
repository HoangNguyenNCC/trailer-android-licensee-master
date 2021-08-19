package devx.app.licensee.modules.inviteEmployees

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devx.app.licensee.R
import devx.app.licensee.common.ResourcesUtil

class AddPrivilegesAdapter : RecyclerView.Adapter<AddPrivilegesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_privileges_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.privilegeTitle.setOnClickListener {
            holder.selectionView.background =
                ResourcesUtil.getDrawableById(R.drawable.all_square_rounded_yellow_background)
            holder.privilegeTitle.setTextColor(ResourcesUtil.getColor(R.color.light_black))
        }

    }

    override fun getItemCount(): Int {
        return 6
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val privilegeTitle: TextView = itemView.findViewById(R.id.privilegeTitle)
        val selectionView: View = itemView.findViewById(R.id.selectionView)
    }
}