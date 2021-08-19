package devx.app.licensee.modules.signup

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import devx.app.licensee.R
import devx.app.licensee.modules.home.profiletab.ProfileEditActivity
import devx.app.seller.modules.signup.SignUpActivity

class WeekDaysAdapter() : RecyclerView.Adapter<WeekDaysAdapter.ViewHolder>() {

    private var context: Context? = null
    private var weekDaysList = arrayListOf<WeekDay>()
    private var itemWidth = 0
    private var displayMetrics = DisplayMetrics()
    private var flag:String=""

    constructor(context: Context, daysList: List<WeekDay>,flag:String) : this() {
        this.context = context
        weekDaysList = daysList as ArrayList<WeekDay>
        displayMetrics = DisplayMetrics()
        this.flag=flag
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        itemWidth = (displayMetrics.widthPixels -75) / 8
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.week_days_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemWidth > 0) holder.rootLayout.layoutParams.width = itemWidth
        if (itemWidth > 0) holder.rootLayout.layoutParams.height = itemWidth
        val weekDay = weekDaysList[position]

        if (weekDay.isSelected) {
            holder.rootLayout.setBackgroundResource(R.drawable.complete_yellow_round_background)
        } else {
            holder.rootLayout.setBackgroundResource(R.drawable.circle_complete_white_round_background)
        }

        holder.weekName.text = weekDaysList[position].weekDay
        holder.itemView.setOnClickListener {
            if (weekDay.isSelected) {
                weekDay.isSelected = false
                holder.rootLayout.setBackgroundResource(R.drawable.circle_complete_white_round_background)

                if (flag=="signup"){
                    (context as SignUpActivity).onDaySelected(
                        false,
                        weekDaysList[holder.adapterPosition].weekValue
                    )
                }
                else{
                    (context as ProfileEditActivity).onDaySelected(
                        false,
                        weekDaysList[holder.adapterPosition].weekValue
                    )
                }
            } else {
                weekDay.isSelected = true
                holder.rootLayout.setBackgroundResource(R.drawable.complete_yellow_round_background)
                if (flag=="signup") {
                    (context as SignUpActivity).onDaySelected(
                        true,
                        weekDaysList[holder.adapterPosition].weekValue
                    )
                }
                else{
                    (context as ProfileEditActivity).onDaySelected(
                        true,
                        weekDaysList[holder.adapterPosition].weekValue
                    )
                }
            }
        }
    }

    fun isNothingSelected(): Boolean {

        for (i in weekDaysList) {
            if (i.isSelected)
                return false
        }
        return true
    }

    fun getSelectedWeekdays(): JsonArray{

        var jsonArray = JsonArray()
        for (i in weekDaysList) {
            if (i.isSelected)
                jsonArray.add(i.weekValue)
        }
        return jsonArray
    }


    override fun getItemCount() = weekDaysList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootLayout: RelativeLayout = itemView.findViewById(R.id.rootLayout)
        val weekName: TextView = itemView.findViewById(R.id.weekName)
    }
}