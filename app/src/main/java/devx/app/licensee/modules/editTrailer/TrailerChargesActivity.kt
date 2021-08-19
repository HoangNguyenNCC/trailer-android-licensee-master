package devx.app.licensee.modules.editTrailer

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TimePicker
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import devx.app.licensee.R
import devx.app.licensee.common.BaseActivity
import devx.app.licensee.common.ResourcesUtil
import devx.app.licensee.common.custumViews.BoldTextView
import devx.app.licensee.common.utils.HelperMethods
import devx.app.licensee.webapi.APIError
import devx.app.licensee.webapi.Api
import devx.app.licensee.webapi.ErrorUtils
import devx.app.licensee.webapi.ServiceGenerator
import devx.app.seller.common.CommonTopbarView
import devx.app.seller.webapi.response.BookingChargesResponse
import kotlinx.android.synthetic.main.activity_trailer_charges.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TrailerChargesActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private var price = 100

    var trailer = TrailerDetailsActivity.trailerDetailsReponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer_charges)
        CommonTopbarView().setup(this)

        txtSelectDateTime.setOnClickListener {
            showCalendarBottomSheet();
        }

        /*var doorToDoorList = trailer.getJSONObject("rentalCharges").getJSONArray("door2Door")
        var pickUp = trailer.getJSONObject("rentalCharges").getJSONArray("pickUp")
        trailerName.setText(""+ trailer.get("name").toString())
        try {
            seekBar.progress =  0
            seekBar.max = doorToDoorList.length()
        } catch (e: Exception) {
            showToast("E:" + e.message)
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<=0 || progress> doorToDoorList.length())return

                daysCount.text = "$progress Hours"

                trailerIncomeDelivery.text = ""+doorToDoorList.getJSONObject(progress-1).get("charges").toString()+ " AUD"
                trailerIncomePickup.text = ""+pickUp.getJSONObject(progress-1).get("charges").toString()+ " AUD"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })*/

    }

    /*Show employee action bottom sheet layout*/
    var bottomSheetDialog: BottomSheetDialog? = null
    var bottomSheetView: View? = null
    var imgClose: ImageView?=null
    var calenderView: com.applandeo.materialcalendarview.CalendarView?=null

    var startDate: BoldTextView?=null
    var selectStartTime: BoldTextView?=null
    var endDate: BoldTextView?=null
    var selectEndTime: BoldTextView?=null

    var nextButton: BoldTextView?=null


    private var isStartTimePicker = -1
    private var isStartTimeSet = false
    private var isEndTimeSet = false
    private var selectedStartDate = ""
    private var selectedEndDate = ""
    var index = 1
    fun showCalendarBottomSheet(
    ) {
        bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val behaviorField: BottomSheetBehavior<*> = bottomSheetDialog!!.getBehavior()
        behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
        behaviorField.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behaviorField.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        })
        bottomSheetView = layoutInflater
            .inflate(
                R.layout.bottom_sheet_calendar,null)
        calenderView=bottomSheetView!!.findViewById(R.id.calenderView)
        startDate=bottomSheetView!!.findViewById(R.id.startDate)
        selectStartTime=bottomSheetView!!.findViewById(R.id.selectStartTime)
        endDate=bottomSheetView!!.findViewById(R.id.endDate)
        selectEndTime=bottomSheetView!!.findViewById(R.id.selectEndTime)
        nextButton=bottomSheetView!!.findViewById(R.id.nextButton)


        selectStartTime!!.setOnClickListener{
            isStartTimePicker = 1
            openTimeSelectionDialogView()
        }
        selectEndTime!!.setOnClickListener{
            isStartTimePicker = 2
            openTimeSelectionDialogView()

        }
        nextButton!!.setOnClickListener {

            val selectedDates = calenderView!!.selectedDates
            if (selectedDates.isEmpty()) {
                showToast("Please select dates")
                return@setOnClickListener
            } else {
                val dateStart = Date(selectedDates[0].timeInMillis)
                val dateEnd = Date(selectedDates[selectedDates.size.minus(1)].timeInMillis)

                val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                val simpleDateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                startDate!!.text = simpleDateFormat.format(dateStart)
                endDate!!.text = simpleDateFormat.format(dateEnd)

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
                    getRequestedScheduleCharges()
                }
            }
        }
        calenderView!!.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                if ((index % 2) != 0) {
                    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    startDate!!.text = simpleDateFormat.format(clickedDayCalendar.timeInMillis)
                    index++
                } else {
                    val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    endDate!!.text = simpleDateFormat.format(clickedDayCalendar.timeInMillis)
                    index++
                }
            }
        })
        bottomSheetDialog!!.setContentView(bottomSheetView!!)

        bottomSheetDialog!!.show()
    }

    /**
     * get web services api call
     */
    var webService: Api = ServiceGenerator.getApi()
    private fun getRequestedScheduleCharges() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "startDate",
            HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedStartDate + " " + selectStartTime!!.text.toString()),"yyyy-MM-dd HH:mm","GMT")
        )
        jsonObject.addProperty(
            "endDate",
            HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedEndDate + " " + selectEndTime!!.text.toString()),"yyyy-MM-dd HH:mm","GMT")
        )
        jsonObject.addProperty("trailerId",trailer.getString("_id"))
        jsonObject.addProperty("isPickUp",false)
        val jsonArray=JsonArray()
        jsonObject.add("upsellItems",jsonArray)
        val getBookingChargesCall: Call<BookingChargesResponse> = webService.getBookingCharges(jsonObject)
        showProgressDialog()
        getBookingChargesCall.enqueue(object : Callback<BookingChargesResponse> {
            override fun onResponse(
                call: Call<BookingChargesResponse>,
                response: Response<BookingChargesResponse>
            ) {
                if (response.isSuccessful()) {

                    dismissProgressDialog()
                    bottomSheetDialog!!.cancel()
                    txtrentalCharges.text=response.body()!!.trailerCharges.rentalCharges+" AUD"
                    txtTaxes.text=response.body()!!.trailerCharges.taxes+" AUD"
                    txtdamageWaiver.text=response.body()!!.trailerCharges.lateFees+" AUD"
                    txttotalCharges.text=response.body()!!.totalPayableAmount+" AUD"

                    var revisedStartDate=HelperMethods.changeDateFormat(HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedStartDate + " " + selectStartTime!!.text.toString()),"yyyy-MM-dd HH:mm","GMT"))
                    var revisedEndDate=HelperMethods.changeDateFormat(HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedEndDate + " " + selectEndTime!!.text.toString()),"yyyy-MM-dd HH:mm","GMT"))

                    txtStartDate.text=revisedStartDate
                    txtEndDate.text=revisedEndDate
                    //txtSelectDateTime.text=HelperMethods.formatDateTimeToString(HelperMethods.convert12hrto24Hr(selectedStartDate + " " + selectStartTime!!.text.toString()),"yyyy-MM-dd HH:mm","GMT") +" To "+

                } else {
                    ///handling bad request 400 response
                    val apiError: APIError = ErrorUtils.parseError(response)
                    showToast(apiError.message())
                }
                dismissProgressDialog()
            }

            override fun onFailure(
                call: Call<BookingChargesResponse>,
                t: Throwable
            ) {
                dismissProgressDialog()
            }
        })
    }
    private fun changeDateFormat(date: String?):String {
        var temp="";
        val titleDate: Date
        try {
            titleDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date)
            temp=SimpleDateFormat("MMM dd\nHH:mm").format(titleDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return temp
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
            selectStartTime!!.text = selectedTime
            isStartTimeSet = true
            Log.e("dateFormat", selectedTime)
        } else if (isStartTimePicker == 2) {
            isStartTimePicker = -1
            selectEndTime!!.text = selectedTime
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
            selectStartTime!!.text = selectedTime
            isStartTimeSet = true
            Log.e("dateFormat", selectedTime)
        } else if (isStartTimePicker == 2) {
            isStartTimePicker = -1
            selectEndTime!!.text = selectedTime
            isEndTimeSet = true
            Log.e("dateFormat", selectedTime)
        }
    }
}
