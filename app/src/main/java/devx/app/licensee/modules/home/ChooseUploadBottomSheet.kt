package devx.app.seller.modules.search

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.IntentParams
import devx.app.licensee.modules.addBooking.AddBookingActivity
import devx.app.licensee.modules.addUpSellItem.AddUpSellItemActivity
import devx.app.licensee.modules.addtrailer.AddTrailerActivity
import devx.app.licensee.modules.addtrailer.SelectUpSellItemActivity
import devx.app.licensee.modules.inviteEmployees.InviteEmployeesActivity
import kotlinx.android.synthetic.main.sheet_choose_action.*

class ChooseUploadBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_choose_action, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCancelBtn.setOnClickListener {
            dismiss()
        }

        addUpsellItemBtn.setOnClickListener {
            dismiss()
            if(HelperMethods.isOwner()) {
                var intent = Intent(activity, AddUpSellItemActivity::class.java)
                intent.putExtra("flag" , "add")
                startActivity(intent)
            }
            else {
                if (HelperMethods.checkACL("UPSELL", "ADD")) {
                    var intent = Intent(activity, AddUpSellItemActivity::class.java)
                    intent.putExtra("flag" , "add")
                    startActivity(intent)
                } else {
                    Toast.makeText(context,"You do not have access to add.",Toast.LENGTH_SHORT).show()
                }
            }
            //startActivity(Intent(activity, AddUpSellItemActivity::class.java))
            /*val intent = Intent(activity, SelectUpSellItemActivity::class.java)
            intent.putExtra(IntentParams.IS_ADD_INSURANCE, false)
            intent.putExtra(IntentParams.IS_ADD_SERVICING, false)
            startActivity(intent)*/
        }

        addTrailerBtn.setOnClickListener {
            dismiss()
            if(HelperMethods.isOwner()) {
                startActivity(Intent(activity, AddTrailerActivity::class.java))
            }
            else {
                if (HelperMethods.checkACL("TRAILER", "ADD")) {
                    startActivity(Intent(activity, AddTrailerActivity::class.java))
                } else {
                    Toast.makeText(context,"You do not have access to add.",Toast.LENGTH_SHORT).show()
                }
            }
        }
        addBookingBtn.setOnClickListener {
            dismiss()
            if(HelperMethods.isOwner()) {
                startActivity(Intent(activity, AddBookingActivity
                ::class.java))
            }
            else {
                if (HelperMethods.checkACL("BLOCK", "ADD")) {
                    startActivity(Intent(activity, AddBookingActivity
                    ::class.java))
                } else {
                    Toast.makeText(context,"You do not have access to add.",Toast.LENGTH_SHORT).show()
                }
            }
        }
        /*addServicingmBtn.setOnClickListener {
            dismiss()
            val intent = Intent(activity, SelectUpSellItemActivity::class.java)
            intent.putExtra(IntentParams.IS_ADD_INSURANCE, false)
            intent.putExtra(IntentParams.IS_ADD_SERVICING, true)
            startActivity(intent)
        }*/

        /*addInsuranceBtn.setOnClickListener {
            dismiss()
            val intent = Intent(activity, SelectUpSellItemActivity::class.java)
            intent.putExtra(IntentParams.IS_ADD_INSURANCE, true)
            intent.putExtra(IntentParams.IS_ADD_SERVICING, false)
            startActivity(intent)
        }*/
        inviteEmployeeBtn.setOnClickListener{
            dismiss()
            if(HelperMethods.isOwner()) {
                startActivity(Intent(activity,InviteEmployeesActivity::class.java))
            }
            else {
                if (HelperMethods.checkACL("EMPLOYEES", "ADD")) {
                    startActivity(Intent(activity,InviteEmployeesActivity::class.java))
                } else {
                    Toast.makeText(context,"You do not have access to add.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}