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
import company.coutloot.common.xtensions.show
import devx.app.licensee.R
import devx.app.licensee.common.TrailerApplication.trailerObj
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.modules.addtrailer.AddTrailerInsuranceActivity
import devx.app.licensee.modules.addtrailer.AddTrailerServicingActivity
import devx.app.licensee.modules.insurance.InsuranceListActivity
import devx.app.licensee.modules.inviteEmployees.InviteEmployeesActivity
import devx.app.seller.modules.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.*
import kotlinx.android.synthetic.main.sheet_choose_action_insurance.*
import kotlinx.android.synthetic.main.sheet_choose_action_insurance.btnCancel
import kotlinx.android.synthetic.main.sheet_update_document.*
import org.json.JSONObject

class UpdateDocumentBottomSheet(flag: String) : BottomSheetDialogFragment() {

    val tempStr=flag
    val tempTrailerObj=trailerObj
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_update_document, container, false)
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


        btnCancel.setOnClickListener {
            dismiss()
        }

        btnUpdateScan.setOnClickListener {
            dismiss()
            if(tempStr=="INSURANCE"){
                (activity as AddTrailerInsuranceActivity).chooseDocument()
            }
            else{
                (activity as AddTrailerServicingActivity).chooseDocument()
            }

        }

        btnViewScan.setOnClickListener {
            dismiss()

            if(tempStr=="INSURANCE"){
                (activity as AddTrailerInsuranceActivity).showDocument()
            }
            else{
                (activity as AddTrailerServicingActivity).showDocument()
            }
        }

    }
}