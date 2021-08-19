package devx.app.seller.modules.home.hometab

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.modules.home.profiletab.ProfileFragment
import devx.app.seller.modules.home.notiftab.NotificationFragment
import devx.app.seller.modules.search.ChooseUploadBottomSheet
import kotlinx.android.synthetic.main.activity_home.*

class
HomeActivity : BaseActivity() {

    var profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val adapter = HomeViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment())
        adapter.addFragment(RequestTrailerFragment())
        adapter.addFragment(NotificationFragment())
        adapter.addFragment(profileFragment)
        homeViewPager.adapter = adapter

        homeRL.setOnClickListener {
            homeViewPager.currentItem = 0
        }

        requestIV.setOnClickListener {
            homeViewPager.currentItem = 1
        }

        notificationRL.setOnClickListener {
            homeViewPager.currentItem = 2
        }

        accountRL.setOnClickListener {
            homeViewPager.currentItem = 3
        }

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                activateTabIcon(position)
            }
        })

        addNewTrailer.setOnClickListener {
            ChooseUploadBottomSheet().show(supportFragmentManager, "")
        }


    }

    private fun activateTabIcon(position: Int) {
        when (position) {
            0 -> {
                homeIV.setImageResource(R.drawable.home_yellow)
                requestIV.setImageResource(R.drawable.grey_notification_icon)
                accountIV.setImageResource(R.drawable.grey_account_icon)
                notificationIV.setImageResource(R.drawable.reminder_grey)
            }
            1 -> {
                requestIV.setImageResource(R.drawable.notification_yellow)
                homeIV.setImageResource(R.drawable.grey_home_icon)
                notificationIV.setImageResource(R.drawable.reminder_grey)
                accountIV.setImageResource(R.drawable.grey_account_icon)
            }
            2 -> {
                notificationIV.setImageResource(R.drawable.reminder_yellow)
                homeIV.setImageResource(R.drawable.grey_home_icon)
                requestIV.setImageResource(R.drawable.grey_notification_icon)
                accountIV.setImageResource(R.drawable.grey_account_icon)
            }
            3 -> {
                accountIV.setImageResource(R.drawable.yellow_profile_icon)
                homeIV.setImageResource(R.drawable.grey_home_icon)
                requestIV.setImageResource(R.drawable.grey_notification_icon)
                notificationIV.setImageResource(R.drawable.reminder_grey)
            }
        }
    }
}