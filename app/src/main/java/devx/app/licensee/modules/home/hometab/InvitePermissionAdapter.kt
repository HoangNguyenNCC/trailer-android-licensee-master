package devx.app.licensee.modules.home.hometab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.home.hometab.EmployeeListAdapter.Companion.tempSelectedpermissionList
import devx.app.licensee.modules.inviteEmployees.PermissionItem

class InvitePermissionAdapter(
    private val context: Context,
    var permissionList: ArrayList<PermissionItem>?,
    var acl: MutableMap<String, java.util.ArrayList<String>>
) :
    RecyclerView.Adapter<InvitePermissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.invite_permission_item_with_color_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permissionItem = permissionList?.get(position)
        holder.permissionName.text = permissionItem!!.atribute

        if (permissionItem.isPermissionSelected == 1) {

                    if(permissionItem.selectedPermissionArr.contains("ADD")){
                        holder.viewAdd.show()
                    }
                    else{
                        holder.viewAdd.gone()
                    }
                    if(permissionItem.selectedPermissionArr.contains("VIEW")){
                        holder.viewView.show()
                    } else{
                        holder.viewView.gone()
                    }
                    if(permissionItem.selectedPermissionArr.contains("UPDATE")){
                        holder.viewUpdate.show()
                    }else{
                        holder.viewUpdate.gone()
                    }
                    if(permissionItem.selectedPermissionArr.contains("DELETE")){
                        holder.viewDelete.show()
                    }else{
                        holder.viewDelete.gone()
                    }


        } else {
            holder.viewAdd.gone()
            holder.viewView.gone()
            holder.viewUpdate.gone()
            holder.viewDelete.gone()
        }


        holder.itemView.setOnClickListener {
            showAddPrivilegeDialog(
                permissionItem,
                holder.adapterPosition
            )
        }

    }
    fun showAddPrivilegeDialog(permissionitem: PermissionItem, position: Int) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.add_privilege_dialog_item_layout, null, false)
        val dialog =
            AlertDialog.Builder(context)
                .setView(view)
                .create()

        var isAnyPermissionSelected = false
        val closeView = view.findViewById<ImageView>(R.id.closeView)
        val addPermission = view.findViewById<TextView>(R.id.addPermission)
        val viewPermission = view.findViewById<TextView>(R.id.viewPermission)
        val updatePermission = view.findViewById<TextView>(R.id.updatePermission)
        val deletePermission = view.findViewById<TextView>(R.id.deletePermission)
        val addPrivilegeButton = view.findViewById<TextView>(R.id.addPrivilegeButton)
        val privilegeName = view.findViewById<TextView>(R.id.privilegeName)
        privilegeName.text = permissionitem.atribute

        var tempPermissionArr= arrayListOf<String>()
        //tempSelectedpermissionList.clear()

        /*for(item in acl){
           tempSelectedpermissionList.put(item.key,item.value)
        }*/

       // if(tempSelectedpermissionList.size>0){
           // for(selectedPermission in tempSelectedpermissionList){
                for(i in 0 until permissionitem.selectedPermissionArr.size) {
                    //if (permissionitem.atribute == selectedPermission.key) {
                        if(permissionitem.selectedPermissionArr[i]=="ADD"){
                            isAnyPermissionSelected = true
                            addPermission.setBackgroundResource(R.drawable.complete_green_round_background)
                            addPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            tempPermissionArr.add("ADD")
                        }
                        if(permissionitem.selectedPermissionArr[i]=="VIEW"){
                            isAnyPermissionSelected = true
                            viewPermission.setBackgroundResource(R.drawable.complete_blue_round_background)
                            viewPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            tempPermissionArr.add("VIEW")
                        }
                        if(permissionitem.selectedPermissionArr[i]=="UPDATE"){
                            isAnyPermissionSelected = true
                            updatePermission.setBackgroundResource(R.drawable.complete_orange_round_background)
                            updatePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            tempPermissionArr.add("UPDATE")
                        }
                        if(permissionitem.selectedPermissionArr[i]=="DELETE"){
                            isAnyPermissionSelected = true
                            deletePermission.setBackgroundResource(R.drawable.complete_red_round_background)
                            deletePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            tempPermissionArr.add("DELETE")
                        }
                    //}
                }
            //}

        //}

        addPermission.setOnClickListener {
            if(tempPermissionArr.contains("ADD")){
                isAnyPermissionSelected = false
                addPermission.setBackgroundResource(R.drawable.complete_gray_round_background)
                addPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.remove("ADD")
            }
            else{
                isAnyPermissionSelected = true
                addPermission.setBackgroundResource(R.drawable.complete_green_round_background)
                addPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.add("ADD")
            }
        }

        viewPermission.setOnClickListener {
            if(tempPermissionArr.contains("VIEW")) {
                isAnyPermissionSelected = false
                viewPermission.setBackgroundResource(R.drawable.complete_gray_round_background)
                viewPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.remove("VIEW")
            }
            else{

                isAnyPermissionSelected = true
                viewPermission.setBackgroundResource(R.drawable.complete_blue_round_background)
                viewPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.add("VIEW")
            }
        }

        updatePermission.setOnClickListener {
            if(tempPermissionArr.contains("UPDATE")) {
                isAnyPermissionSelected = false
                updatePermission.setBackgroundResource(R.drawable.complete_gray_round_background)
                updatePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.remove("UPDATE")}
            else{

                isAnyPermissionSelected = true
                updatePermission.setBackgroundResource(R.drawable.complete_orange_round_background)
                updatePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.add("UPDATE")
            }
        }

        deletePermission.setOnClickListener {
            if(tempPermissionArr.contains("DELETE")) {
                isAnyPermissionSelected = false
                deletePermission.setBackgroundResource(R.drawable.complete_gray_round_background)
                deletePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.remove("DELETE")
            }
            else{

                isAnyPermissionSelected = true
                deletePermission.setBackgroundResource(R.drawable.complete_red_round_background)
                deletePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                tempPermissionArr.add("DELETE")
            }
        }

        closeView.setOnClickListener {
            dialog.dismiss()
           // EmployeeListAdapter.tempSelectedpermissionList.clear()
        }

        addPrivilegeButton.setOnClickListener {
            if (tempPermissionArr.size>0) {
                //LogUtil.info("selectedPermission", selectedPermissionList.toString())
                if (EmployeeListAdapter.permissionAdapter != null && EmployeeListAdapter.permissionAdapter!!.permissionList != null) {
                    EmployeeListAdapter.permissionAdapter!!.permissionList!![position].isPermissionSelected = 1
                    EmployeeListAdapter.permissionAdapter!!.permissionList!![position].selectedPermissionArr=tempPermissionArr
                    //acl.replace(EmployeeListAdapter.permissionAdapter!!.permissionList!![position].atribute,tempPermissionArr)
                    EmployeeListAdapter.permissionAdapter!!.notifyItemChanged(position)
                    dialog.dismiss()
                }

                EmployeeListAdapter.permissionAdapter!!.notifyDataSetChanged(permissionList!!)
            } else
                HelperMethods.showToastbar(context, "Select at least 1 permission")
        }

        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.show()
    }
    override fun getItemCount() = permissionList!!.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val permissionName: TextView = itemView.findViewById(R.id.permissionName)
        val lytColor: LinearLayout = itemView.findViewById(R.id.lytColor)
        val viewAdd:View=itemView.findViewById(R.id.viewAdd);
        val viewView:View=itemView.findViewById(R.id.viewView);
        val viewUpdate:View=itemView.findViewById(R.id.viewUpdate);
        val viewDelete:View=itemView.findViewById(R.id.viewDelete);
    }
    fun notifyDataSetChanged(mList:ArrayList<PermissionItem>) {

        permissionList= mList
        notifyDataSetChanged()

    }



}