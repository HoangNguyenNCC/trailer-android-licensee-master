package devx.app.licensee.modules.home.hometab

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import devx.app.licensee.R
import devx.app.licensee.R.drawable.complete_red_round_background
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.inviteEmployees.PermissionItem
import devx.app.seller.modules.home.hometab.HomeFragment
import kotlinx.android.synthetic.main.fragment_permission_bottom_sheet.*

class PermissionBottomSheetFragment : BottomSheetDialogFragment() {

    private var permissionList = arrayListOf<PermissionItem>()
    private var permissionAdapter: InvitePermissionAdapter? = null

    companion object{
        var tempSelectedpermissionList=
            mutableMapOf<String,ArrayList<String>>()//arrayListOf<PermissionItem>()

        var selectedPermissionList = arrayListOf<String>()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionMap=arguments?.getSerializable("permissionList") as MutableMap<String,ArrayList<String>>
        //val list = arguments?.getStringArrayList("permissionList") as ArrayList<String>
       // tempSelectedpermissionList=(activity as InviteEmployeesActivity).tempSelectedpermissionList

        permissionList.clear()

        /*for (permissionitem in permissionMap) {
            permissionList.add(PermissionItem(permissionitem.key, permissionitem.value, 0))
        }*/
       /* if(tempSelectedpermissionList.size>0) {
            for(i in 0 until permissionList.size){
                for(selectedPermission in tempSelectedpermissionList){
                    if (permissionList.get(i).atribute == selectedPermission.key) {
                        permissionList.get(i).isPermissionSelected=1
                        break

                    }
                }
            }

        }*/


       /* permissionAdapter = InvitePermissionAdapter(
            context!!,
            permissionList,
            null
        )
        invitePermissionsRecyclerView.adapter = permissionAdapter
        permissionAdapter!!.notifyDataSetChanged(permissionList)*/

        commonBack.setOnClickListener {
            (activity as HomeFragment).dismissPermissionBottomSheet()
        }

        doneButton.setOnClickListener {
           /* if (selectedPermissionList.isEmpty()) {
                HelperMethods.showToastbar(context, "Select Something to proceed")
                return@setOnClickListener
            }*/
           /* if ((activity as HomeFragment).tempSelectedpermissionList.isEmpty()) {
            HelperMethods.showToastbar(context, "Select Something to proceed")
            return@setOnClickListener
            }
                (activity as HomeFragment).selectedPermissions = selectedPermissionList
                (activity as HomeFragment).tempSelectedpermissionList = tempSelectedpermissionList
                (activity as HomeFragment).dismissPermissionBottomSheet()*/

        }
    }

   /* fun removeSelectedPermission(permissionName: String, position: Int) {
        if (permissionAdapter != null && permissionAdapter!!.permissionList != null) {
            permissionAdapter!!.permissionList.get(position).isPermissionSelected = 0

            val removeList = arrayListOf<String>()
            var removeItem:String=""
            for (selectedPermission in tempSelectedpermissionList) {
                if (selectedPermission.key==permissionName) {
                    //removeList.add(selectedPermission)
                    removeItem=selectedPermission.key
                }
            }

            selectedPermissionList.removeAll(removeList)
            tempSelectedpermissionList.remove(removeItem)
            //LogUtil.info("selectedPermission", selectedPermissionList.toString())
            permissionAdapter!!.notifyItemChanged(position)
        }
    }*/

    fun showAddPrivilegeDialog(permissionitem: PermissionItem, position: Int) {
        val view: View =
            layoutInflater.inflate(R.layout.add_privilege_dialog_item_layout, null, false)
        val dialog =
            AlertDialog.Builder(activity!!)
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
        if(tempSelectedpermissionList.size>0){
            for(selectedPermission in tempSelectedpermissionList){
                for(i in 0 until permissionitem.permissionArr.size) {
                    if (permissionitem.atribute == selectedPermission.key) {
                        if(selectedPermission.value.contains("ADD")){
                            isAnyPermissionSelected = true
                            addPermission.setBackgroundResource(R.drawable.complete_green_round_background)
                            addPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            selectedPermissionList.add("ADD")
                            tempPermissionArr.add("ADD")
                        }
                         if(selectedPermission.value.contains("VIEW")){
                            isAnyPermissionSelected = true
                            viewPermission.setBackgroundResource(R.drawable.complete_blue_round_background)
                            viewPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            selectedPermissionList.add("VIEW")
                            tempPermissionArr.add("VIEW")
                        }
                         if(selectedPermission.value.contains("UPDATE")){
                            isAnyPermissionSelected = true
                            updatePermission.setBackgroundResource(R.drawable.complete_orange_round_background)
                            updatePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            selectedPermissionList.add("UPDATE")
                            tempPermissionArr.add("UPDATE")
                        }
                         if(selectedPermission.value.contains("DELETE")){
                            isAnyPermissionSelected = true
                            deletePermission.setBackgroundResource(complete_red_round_background)
                            deletePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
                            selectedPermissionList.add("DELETE")
                            tempPermissionArr.add("DELETE")
                        }
                    }
                }
            }

        }
        addPermission.setOnClickListener {
            isAnyPermissionSelected = true
            addPermission.setBackgroundResource(R.drawable.complete_green_round_background)
            addPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
            selectedPermissionList.add("ADD")
            tempPermissionArr.add("ADD")

        }

        viewPermission.setOnClickListener {
            isAnyPermissionSelected = true
            viewPermission.setBackgroundResource(R.drawable.complete_blue_round_background)
            viewPermission.setTextColor(ResourcesUtil.getColor(R.color.white))
            selectedPermissionList.add("VIEW")
            tempPermissionArr.add("VIEW")
        }

        updatePermission.setOnClickListener {
            isAnyPermissionSelected = true
            updatePermission.setBackgroundResource(R.drawable.complete_orange_round_background)
            updatePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
            selectedPermissionList.add("UPDATE")
            tempPermissionArr.add("UPDATE")
        }

        deletePermission.setOnClickListener {
            isAnyPermissionSelected = true
            deletePermission.setBackgroundResource(R.drawable.complete_red_round_background)
            deletePermission.setTextColor(ResourcesUtil.getColor(R.color.white))
            selectedPermissionList.add("DELETE")
            tempPermissionArr.add("DELETE")
        }

        closeView.setOnClickListener {
            dialog.dismiss()
        }

        addPrivilegeButton.setOnClickListener {
            if (isAnyPermissionSelected) {
                //LogUtil.info("selectedPermission", selectedPermissionList.toString())
                if (permissionAdapter != null && permissionAdapter!!.permissionList != null) {
                    permissionAdapter!!.permissionList!![position].isPermissionSelected = 1
                    permissionAdapter!!.permissionList!![position].permissionArr=tempPermissionArr
                    permissionAdapter!!.notifyItemChanged(position)
                    dialog.dismiss()
                }
                tempSelectedpermissionList.put(permissionitem.atribute,tempPermissionArr)
                permissionAdapter!!.notifyDataSetChanged(permissionList)
            } else
                HelperMethods.showToastbar(context, "Select at least 1 permission")
        }

        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.show()
    }

    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
}
