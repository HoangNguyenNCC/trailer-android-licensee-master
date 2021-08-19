package devx.app.licensee.modules.home.hometab
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.makeramen.roundedimageview.RoundedImageView
import devx.app.licensee.R
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.BoldTextView
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity
import devx.app.licensee.modules.inviteEmployees.PermissionItem
import devx.app.licensee.webapi.CallApi
import devx.app.licensee.webapi.response.employee.EmployeeDetailsObj
import devx.app.seller.modules.home.hometab.HomeFragment
import devx.app.seller.webapi.response.CommonResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class EmployeeListAdapter(
    private val homeFragment: HomeFragment,
    var empList: ArrayList<EmployeeDetailsObj>
) :
    RecyclerView.Adapter<EmployeeListAdapter.ViewHolder>() {

   // var permissionBottomSheetFragment: devx.app.licensee.modules.home.hometab.PermissionBottomSheetFragment? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sgl_item_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val empItem = empList[position]

        holder.empEmail.text = empItem.name
        holder.empMobile.text=empItem.mobile
        if(empItem.photo!=null) {
            HelperMethods.downloadImage(empItem.photo, homeFragment.context, holder.imgEmp)
        }

        if(empItem.mobileVerified&&empItem.emailVerified){
            holder.mTxtAvailabilityStatus.setText("Verified")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_green_round_corner_background)
        }
        else{

            holder.mTxtAvailabilityStatus.setText("Not Verified")
            holder.mTxtAvailabilityStatus.setBackgroundResource(R.drawable.complete_red_round_corner_background)
        }
        holder.itemView.setOnClickListener {

            for(i in 0 until empList[position].arrACL.size) {
                for (item in empList[position].acl) {
                    if (empList[position].arrACL[i].atribute == item.key) {
                        empList[position].arrACL[i].selectedPermissionArr = item.value
                        break
                    }
                }
            }
            homeFragment.showBottomSheet(empList[position].owner,empList[position].arrACL,position);

            /*if(HelperMethods.isOwner()) {
                if(empList[position].owner){
                    val editProfileIntent = Intent(homeFragment.context, ProfileEditActivity::class.java)
                    editProfileIntent.putExtra("flag","businessInfo")
                    homeFragment.context!!.startActivity(editProfileIntent)
                    //Toast.makeText(homeFragment.context,"You cannot edit owner ACL",Toast.LENGTH_SHORT).show()
                }
                else{
                    showPermissionDialog(empList[position].arrACL,position)
                }
            }
            else{
                if (HelperMethods.checkACL("EMPLOYEES", "UPDATE")) {

                    showPermissionDialog(empList[position].arrACL,position)
                } else {
                    Toast.makeText(homeFragment.context,"You do not have access to update.",Toast.LENGTH_SHORT).show()
                }
            }*/

        }
        holder.itemView.setOnLongClickListener {
            if(HelperMethods.isOwner()) {
                if(empList[position].owner){
                    Toast.makeText(homeFragment.context,"You cannot delete owner account",Toast.LENGTH_SHORT).show()
                }
                else{
                    showDeleteAlert(position)
                }
                true
            }
            else{
                if (HelperMethods.checkACL("EMPLOYEES", "DELETE")) {
                    showDeleteAlert(position)
                    true
                } else {
                    Toast.makeText(homeFragment.context,"You do not have access to delete.",Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }

    }



    private fun showDeleteAlert(position: Int) {
        val builder = android.app.AlertDialog.Builder(homeFragment.context)
        //set title for alert dialog
        builder.setTitle("Delete Employee")
        //set message for alert dialog
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            homeFragment.deleteEmployee( empList[position]._id)
            //Toast.makeText(context,"clicked yes", Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
            //Toast.makeText(context,"clicked No", Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: android.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

        val positiveColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        val negativeColor: Int = ResourcesUtil.getColor(R.color.light_orange)
        alertDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        alertDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
    }




    companion object{
        var tempSelectedpermissionList=
            mutableMapOf<String,ArrayList<String>>()//arrayListOf<PermissionItem>()
        var permissionAdapter: InvitePermissionAdapter? = null
    }




    override fun getItemCount() = empList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val empEmail: TextView = itemView.findViewById(R.id.txtEmpEmail)
        val empMobile: TextView = itemView.findViewById(R.id.txtEmpMobile)
        val imgEmp: RoundedImageView =itemView.findViewById(R.id.imgEmployee)
        val mTxtAvailabilityStatus:TextView=itemView.findViewById(R.id.mTxtAvailabilityStatus)
    }
    fun notifyDataSetChanged(mList:List<EmployeeDetailsObj>) {

        empList= mList as ArrayList<EmployeeDetailsObj>
        notifyDataSetChanged()

    }



}