package devx.app.seller.modules.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.modules.home.hometab.HomeActivity
import devx.app.seller.modules.login.LoginActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (HelperMethods.isUserLogin() && HelperMethods.getUserId() != "-1") {
                startActivity(Intent(this, HomeActivity::class.java))
            } else
                startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}