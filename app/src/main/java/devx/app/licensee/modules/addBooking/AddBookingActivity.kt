package devx.app.licensee.modules.addBooking

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import company.coutloot.common.xtensions.gone
import company.coutloot.common.xtensions.show
import company.coutloot.common.xtensions.visible
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.TransparentProgressDialog
import devx.app.licensee.common.utils.ConstantApi
import devx.app.licensee.common.utils.Constants
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.common.utils.Utils
import devx.app.licensee.webapi.APIError
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ErrorUtils
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.licensee.webapi.response.addUpSell.AddUpSellItemResponse
import devx.app.licensee.webapi.response.home.HomeResponse
import devx.app.seller.webapi.response.CommonResponse
import devx.app.seller.webapi.response.upSellItem.UpSellItemResponse
import devx.app.seller.webapi.response.upSellItem.UpsellItems
import kotlinx.android.synthetic.main.activity_add_booking.*
import kotlinx.android.synthetic.main.activity_add_booking.upSellItemRecyclerView
import kotlinx.android.synthetic.main.common_topbar_notitle_transparent.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class AddBookingActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private var isStartTimePicker = -1
    private var isStartTimeSet = false
    private var isEndTimeSet = false
    private var selectedStartDate = ""
    private var selectedEndDate = ""
    private var isForResult = false
    private var isDateSelection = false

    private var isFirstDate = true
    var index = 1


    var trailersList = arrayListOf<TrailerItem>()
    var upSellItemList = arrayListOf<UpsellItem>()

    private lateinit var trailerAdapter: TrailerAdapter
    private lateinit var upsellAdapter: UpsellItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking)


        val min: Calendar = Calendar.getInstance()
        calenderView.setMinimumDate(min)

        calendarLayout.show()
        trailerUpsellLayout.gone()

        displayTitle.text="Add Dates"
        displayText.text="Book Trailers"

        commonBack.setOnClickListener {
            onBackPressed()
        }

        /*Next button click*/
        nextButton.setOnClickListener {

            val selectedDates = calenderView.selectedDates
            if (selectedDates.isEmpty()) {
                showToast("Please select dates")
                return@setOnClickListener
            } else {
                val dateStart = Date(selectedDates[0].timeInMillis)
                val dateEnd = Date(selectedDates[selectedDates.size.minus(1)].timeInMillis)

                val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                val simpleDateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                startDate.text = simpleDateFormat.format(dateStart)
                endDate.text = simpleDateFormat.format(dateEnd)

                selectedStartDate = simpleDateFormat1.format(dateStart)
                selectedEndDate = simpleDateFormat1.format(dateEnd)

                if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {

                    showToast("Select date range from calendar")
                } else if (!HelperMethods.compareTwoDate(selectedStartDate, selectedEndDate)) {
                    showToast("Start date should be less than end date")
                } else if (!isStartTimeSet) {
                    showToast("Select Start time")
                } else if (!isEndTimeSet) {
                    showToast("Select End time")
                } else {

                    calendarLayout.gone()
                    trailerUpsellLayout.show()

                    displayTitle.text="Select Unavailable Items"
                    displayText.text="Select Trailers / Upsell"


                    resetOptionLayoutUI()
                    trailersOption.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
                    trailersOption.setTextColor(ResourcesUtil.getColor(R.color.black))

                    trailersRecyclerView.show()
                    upSellItemRecyclerView.gone()
                    txtNoDataAdded.gone()
                    getRequestedList(Constants.TRAILER)
                }

            }


        }


        addScheduleButton.setOnClickListener {
            if (getSelectedArr().size() > 0) {
                if (Utils.isNetworkAvailable(context)) {
                    addSchedule();
                } else {
                    showToast("No Internet Connectivity!")
                }
            } else {
                showToast("Please add atleast item ")
            }
        }

        selectStartTime.setOnClickListener {
            isStartTimePicker = 1
            openTimeSelectionDialogView()
        }

        selectEndTime.setOnClickListener {
            isStartTimePicker = 2
            openTimeSelectionDialogView()
        }
        calenderView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                if ((index % 2) != 0) {
                    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    startDate.text = simpleDateFormat.format(clickedDayCalendar.timeInMillis)
                    index++
                } else {
                    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    endDate.text = simpleDateFormat.format(clickedDayCalendar.timeInMillis)
                    index++
                }
            }
        })


        trailersOption.setOnClickListener {
            resetOptionLayoutUI()
            trailersOption.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
            trailersOption.setTextColor(ResourcesUtil.getColor(R.color.black))

            trailersRecyclerView.show()
            upSellItemRecyclerView.gone()
            txtNoDataAdded.gone()
            getRequestedList(Constants.TRAILER)
        }

        upSellItemsOption.setOnClickListener {
            resetOptionLayoutUI()
            upSellItemsOption.setBackgroundResource(R.drawable.all_square_rounded_yellow_background)
            upSellItemsOption.setTextColor(ResourcesUtil.getColor(R.color.black))

            trailersRecyclerView.gone()
            upSellItemRecyclerView.show()
            txtNoDataAdded.gone()
            getRequestedList(Constants.UP_SELL_ITEM)
        }
    }

    override fun onResume() {
        super.onResume()
        trailerAdapter = TrailerAdapter(
            context!!,
            trailersList
        )
        trailersRecyclerView.adapter = trailerAdapter

        upsellAdapter = UpsellItemAdapter(
            context!!,
            upSellItemList
        )
        upSellItemRecyclerView.adapter = upsellAdapter
    }

    /*GET trailer and upsell items to set unavailability*/
    var url: String = ""
    private fun getRequestedList(flag: String) {
        if (flag == "trailer") {
            getFeaturedTrailer()
        } else{
            getUpsellItemAPI()
        }
    }

    private fun getFeaturedTrailer() {

        showProgressDialog()
        val requestMap = HashMap<String, String>()
        requestMap["count"] = "15"
        requestMap["skip"] = "0"

        val getAllTrailerCall: Call<UpSellItemResponse> =webService.getAllTrailersUpsellsForBooking(ConstantApi.LIVE_BASE_URL + "licensee/trailers?",requestMap)

        getAllTrailerCall.enqueue(object : Callback<UpSellItemResponse> {
            override fun onResponse(
                call: Call<UpSellItemResponse>,
                response: Response<UpSellItemResponse>
            ) {
                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.trailersList != null && response.body()!!.trailersList.isNotEmpty()) {
                            if(response.body()!!.trailersList.size>0) {
                                trailersRecyclerView.show()
                                txtNoDataAdded.gone()
                                trailersList.clear()
                                trailersList=response.body()!!.trailersList
                                trailerAdapter?.notifyDataSetChanged(trailersList)

                            }
                            else{
                                trailersRecyclerView.gone()
                                txtNoDataAdded.show()
                            }
                        }
                        else{
                            trailersRecyclerView.gone()
                            txtNoDataAdded.show()
                        }
                    } else {
                        showToast("Something went wrong ...")
                    }
                } else {
                    ///handling bad request 400 response
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    HelperMethods.closeEverythingOpenSplash(this@AddBookingActivity)
                }
            }

            override fun onFailure(
                call: Call<UpSellItemResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }
    private fun getUpsellItemAPI() {

        val endPoint = "licensee/upsellitems?"
        val requestMap = HashMap<String, String>()
        requestMap["count"] = "15"
        requestMap["skip"] = "0"

        val getUpSellOrTrailerItemsCall: Call<UpSellItemResponse> =webService.getAllTrailersUpsellsForBooking(ConstantApi.LIVE_BASE_URL + endPoint, requestMap)

        showProgressDialog()
        getUpSellOrTrailerItemsCall.enqueue(object : Callback<UpSellItemResponse> {
            override fun onResponse(
                call: Call<UpSellItemResponse>,
                response: Response<UpSellItemResponse>
            ) {
                dismissProgressDialog()
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        if (response.body()!!.upsellItemsList.size > 0) {
                            upSellItemRecyclerView.show()
                            txtNoDataAdded.gone()
                            upSellItemList.clear()
                            upSellItemList= response.body()!!.upsellItemsList
                            upsellAdapter?.notifyDataSetChanged(upSellItemList)

                        }
                        else{
                            upSellItemRecyclerView.gone()
                            txtNoDataAdded.show()
                        }
                    } else {
                        upSellItemRecyclerView.gone()
                        txtNoDataAdded.show()
                        showToast("Something went wrong ...")
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    HelperMethods.setIsUserLogin(false)
                    HelperMethods.setUserId("-1")
                    showToast("Session expired! Please login again.")
                    HelperMethods.closeEverythingOpenSplash(this@AddBookingActivity)
                }
            }
            override fun onFailure(
                call: Call<UpSellItemResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })

    }


    /*Add schedule API call*/

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun addSchedule() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "startDate",
            HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedStartDate + " " + selectStartTime.text.toString()),"yyyy-MM-dd HH:mm","GMT")
        )
        jsonObject.addProperty(
            "endDate",
            HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedEndDate + " " + selectEndTime.text.toString()),"yyyy-MM-dd HH:mm","GMT")
        )
        jsonObject.add("items", getSelectedArr())

        val addScheduleCall: Call<CommonResponse> = webService.addScheduleCallAPI(jsonObject)

        addScheduleCall.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful()) {
                    if (response.body()!!.success) {
                        showToast(
                            HelperMethods.safeText(
                                "Schedule added successfully!",
                                ""
                            )
                        )
                        HelperMethods.closeEverythingOpenHome(context)

                    } else {
                        showToast("Something went wrong ...")
                        //return
                    }
                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
                dismissProgressDialog()
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
    }

    private fun getSelectedArr(): JsonArray {
        var jsonArray: JsonArray = JsonArray()

        for (i in trailersList) {
            if (i.isSelected) {
                var jsonObject = JsonObject()
                jsonObject.addProperty("itemType", i.type)
                jsonObject.addProperty("itemId", i.id)
                jsonArray.add(jsonObject)
            }
        }
        for (i in upSellItemList) {
            if (i.isSelected) {
                var jsonObject = JsonObject()
                jsonObject.addProperty("itemType", i.type)
                jsonObject.addProperty("itemId", i.id)
                jsonObject.addProperty("units", i.units)
                jsonArray.add(jsonObject)
            }
        }
        return jsonArray
    }


    private fun resetOptionLayoutUI() {
        trailersOption.setBackgroundResource(0)
        trailersOption.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))
        upSellItemsOption.setBackgroundResource(0)
        upSellItemsOption.setTextColor(ResourcesUtil.getColor(R.color.dark_gray))
    }

    override fun onBackPressed() {
        if (calendarLayout.visibility == View.VISIBLE)
            super.onBackPressed()
        else if (trailerUpsellLayout.visibility == View.VISIBLE) {
            trailerUpsellLayout.gone()
            calendarLayout.show()
            displayTitle.text="Add Dates"
            displayText.text="Book Trailers"
        }
    }

    private fun openTimeSelectionDialogView() {
        val cal = Calendar.getInstance()
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val minute: Int = cal.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(context, this, hour, minute, false)

        val aa = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(this, false)
        timePickerDialog.setOnShowListener {
            val positiveColor: Int = ResourcesUtil.getColor(R.color.themeDark)
            val negativeColor: Int = ResourcesUtil.getColor(R.color.themeDark)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        aa.show(supportFragmentManager, aa.tag)
    }

    override fun onTimeSet(
        view: com.wdullaer.materialdatetimepicker.time.TimePickerDialog?,
        hourOfDay: Int,
        minute: Int,
        second: Int
    ) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        currentTime[Calendar.HOUR_OF_DAY] = hourOfDay
        currentTime[Calendar.MINUTE] = minute
        currentTime.clear(Calendar.SECOND)
        val selectedTime = timeFormat.format(currentTime.timeInMillis)

        if (selectedTime!!.contains("am", true))
            selectedTime.replace("am", "AM")
        else if (selectedTime.contains("pm", true))
            selectedTime.replace("pm", "PM")

        if (isStartTimePicker == 1) {
            isStartTimePicker = -1
            selectStartTime.text = selectedTime
            isStartTimeSet = true
            Log.e("dateFormat", selectedTime)
        } else if (isStartTimePicker == 2) {
            isStartTimePicker = -1
            selectEndTime.text = selectedTime
            isEndTimeSet = true
            Log.e("dateFormat", selectedTime)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = Calendar.getInstance()
        currentTime[Calendar.HOUR_OF_DAY] = hourOfDay
        currentTime[Calendar.MINUTE] = minute
        currentTime.clear(Calendar.SECOND)
        val selectedTime = timeFormat.format(currentTime.timeInMillis)

        if (selectedTime!!.contains("am", true))
            selectedTime.replace("am", "AM")
        else if (selectedTime.contains("pm", true))
            selectedTime.replace("pm", "PM")

        if (isStartTimePicker == 1) {
            isStartTimePicker = -1
            selectStartTime.text = selectedTime
            isStartTimeSet = true
            Log.e("dateFormat", selectedTime)
        } else if (isStartTimePicker == 2) {
            isStartTimePicker = -1
            selectEndTime.text = selectedTime
            isEndTimeSet = true
            Log.e("dateFormat", selectedTime)
        }
    }


}
