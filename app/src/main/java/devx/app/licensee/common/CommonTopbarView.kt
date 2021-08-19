package devx.app.seller.common

import android.widget.ImageView
import android.widget.TextView
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity

class CommonTopbarView {

    var commonBack: ImageView? = null
    var commonTitle: TextView? = null
    fun setup(activity: BaseActivity, title: String) {
        commonBack = activity.findViewById(R.id.commonBack)
        //commonTitle = activity.findViewById(R.id.commonTitle)
        commonBack?.setOnClickListener {
            activity.finish()
        }
        //commonTitle?.text = title
    }

    fun setup(activity: BaseActivity) {
        commonBack = activity.findViewById(R.id.commonBack)
       // commonTitle = activity.findViewById(R.id.commonTitle)
        commonBack?.setOnClickListener {
            activity.finish()
        }
    }

    fun setup(activity: BaseActivity, title: String, onBackPress: OnBackPress) {
        //setup(activity, title)
        commonBack?.setOnClickListener {
            onBackPress.onBack()
        }
    }

    interface OnBackPress {
        fun onBack()
    }
}