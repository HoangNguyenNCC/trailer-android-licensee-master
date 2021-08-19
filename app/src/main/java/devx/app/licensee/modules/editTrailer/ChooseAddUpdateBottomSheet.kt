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
import kotlinx.android.synthetic.main.activity_add_trailer_insurance.*
import kotlinx.android.synthetic.main.sheet_choose_action_insurance.*
import org.json.JSONObject

class ChooseAddUpdateBottomSheet(flag: String, trailerObj: JSONObject) : BottomSheetDialogFragment() {

    val tempStr=flag
    val tempTrailerObj=trailerObj
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_choose_action_insurance, container, false)
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

        if(tempStr=="INSURANCE"){
            btnUpdate.text="Add Insurance"
            btnView.text="View Insurance"
        }
        else{
            btnUpdate.text="Add Servicing"
            btnView.text="View Servicing"
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnUpdate.setOnClickListener {
            dismiss()
            if(tempStr=="INSURANCE") {
                if (HelperMethods.isOwner()) {

                    var intent=Intent(activity, AddTrailerInsuranceActivity::class.java)
                    intent.putExtra("flag","add")

                    var jsonArray=tempTrailerObj.getJSONArray("photos")
                    intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                    if(jsonArray.length()>0) {
                        intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                    }
                    else{
                        intent.putExtra("photo", "")
                    }

                    intent.putExtra("trailerId",tempTrailerObj.getString("_id"))
                    startActivity(intent)
                } else {
                    if (HelperMethods.checkACL("INSURANCE", "ADD")) {
                        var intent=Intent(activity, AddTrailerInsuranceActivity::class.java)
                        intent.putExtra("flag","add")
                        var jsonArray=tempTrailerObj.getJSONArray("photos")
                        intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                        if(jsonArray.length()>0) {
                            intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                        }
                        else{
                            intent.putExtra("photo", "")
                        }
                        intent.putExtra("trailerId",tempTrailerObj.getString("_id"))
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "You do not have access to add insurance.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else{
                if (HelperMethods.isOwner()) {
                    var intent=Intent(activity, AddTrailerServicingActivity::class.java)
                    intent.putExtra("trailerId",tempTrailerObj.getString("_id"))
                    var jsonArray=tempTrailerObj.getJSONArray("photos")
                    intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                    if(jsonArray.length()>0) {
                        intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                    }
                    else{
                        intent.putExtra("photo", "")
                    }
                    intent.putExtra("flag","add")
                    startActivity(intent)

                } else {
                    if (HelperMethods.checkACL("SERVICING", "ADD")) {
                        var intent=Intent(activity, AddTrailerServicingActivity::class.java)
                        intent.putExtra("trailerId",tempTrailerObj.getString("_id"))
                        var jsonArray=tempTrailerObj.getJSONArray("photos")
                        intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                        if(jsonArray.length()>0) {
                            intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                        }
                        else{
                            intent.putExtra("photo", "")
                        }
                        intent.putExtra("flag","add")
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            context,
                            "You do not have access to add servicing.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

        btnView.setOnClickListener {
            dismiss()

            if(tempStr=="INSURANCE") {
                if (HelperMethods.isOwner()) {
                    val intent=Intent(activity, InsuranceListActivity::class.java)
                    intent.putExtra("itemId",tempTrailerObj.getString("_id"))

                    var jsonArray=tempTrailerObj.getJSONArray("photos")
                    intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                    if(jsonArray.length()>0) {
                        intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                    }
                    else{
                        intent.putExtra("photo", "")
                    }
                    intent.putExtra("flag","insurance")
                    startActivity(intent)
                } else {
                    if (HelperMethods.checkACL("INSURANCE", "VIEW")) {
                        val intent=Intent(activity, InsuranceListActivity::class.java)
                        intent.putExtra("itemId",tempTrailerObj.getString("_id"))

                        var jsonArray=tempTrailerObj.getJSONArray("photos")
                        intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                        if(jsonArray.length()>0) {
                            intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                        }
                        else{
                            intent.putExtra("photo", "")
                        }
                        intent.putExtra("flag","insurance")
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "You do not have access to view.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else{
                if (HelperMethods.isOwner()) {
                    val intent=Intent(activity, InsuranceListActivity::class.java)
                    intent.putExtra("itemId",tempTrailerObj.getString("_id"))

                    var jsonArray=tempTrailerObj.getJSONArray("photos")
                    intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                    if(jsonArray.length()>0) {
                        intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                    }
                    else{
                        intent.putExtra("photo", "")
                    }
                    intent.putExtra("flag","servicing")
                    startActivity(intent)
                } else {
                    if (HelperMethods.checkACL("INSURANCE", "VIEW")) {
                        val intent=Intent(activity, InsuranceListActivity::class.java)
                        intent.putExtra("itemId",tempTrailerObj.getString("_id"))

                        var jsonArray=tempTrailerObj.getJSONArray("photos")
                        intent.putExtra("trailerName", tempTrailerObj.getString("name"))
                        if(jsonArray.length()>0) {
                            intent.putExtra("photo", jsonArray.getJSONObject(0).getString("data"))
                        }
                        else{
                            intent.putExtra("photo", "")
                        }
                        intent.putExtra("flag","servicing")
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "You do not have access to add.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }
}