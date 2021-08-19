package devx.app.licensee.modules.home.profiletab

import android.os.Bundle
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.licensee.common.*
import devx.app.licensee.common.utils.*
import devx.app.licensee.common.utils.Utils.Companion.isValidPassword
import devx.app.licensee.webapi.CallApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import devx.app.seller.webapi.response.CommonResponse
class ChangePasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        commonBack.setOnClickListener {
        finish()
        }

        savePassword.setOnClickListener {
            val password = oldPasswordEditText.text.toString()
            if (password.length < 8) {
                oldPasswordEditText.requestFocus()
                showToast("Please enter old password")
            }
            else if (newPasswordEditText.text.toString().length < 8) {
                oldPasswordEditText.requestFocus()
                showToast("Please enter new password")
            }
            else if (!isValidPassword(newPasswordEditText.text.toString().trim())) {
                newPasswordEditText.requestFocus()
                openKeyBoard(newPasswordEditText)
                showToast("Password must have at least 1 capital, 1 symbol, 1 number");
            }
            else {
                if (Utils.isNetworkAvailable(context)) {
                    updatePassword()
                } else {
                    showToast("No Internet Connection!")
                }
            }
        }


    }
    private fun updatePassword() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("oldPassword", oldPasswordEditText.text.toString())
        jsonObject.addProperty("newPassword", newPasswordEditText.text.toString())

        CallApi().changePwd(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<CommonResponse>() {
                override fun onComplete() {
                }

                override fun onNext(t: CommonResponse) {
                    showToast(t.message)
                    finish()
                }

                override fun onError(e: Throwable) {
                    showToast(e.toString()
                    )
                }
            })

    }

}
