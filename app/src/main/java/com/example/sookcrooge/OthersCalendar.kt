package com.example.sookcrooge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.databinding.ActivityOthersAccountBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.text.SimpleDateFormat
import java.util.Calendar


class OthersCalendar : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityOthersAccountBookBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth= Firebase.auth

        val decoratorList= mutableListOf<calendarDates>()
        val sdf=SimpleDateFormat("yyyy-MM-dd")
        val allDatas = mutableListOf<accountItem>()
        val currentMonthDatas=mutableListOf<accountItem>()
        var selectedMonthSpend=0
        db.collection("JROKAAj8GRc49JZxLvXRpsUYZqH3")
            .orderBy("date")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var timeStamp = document["date"] as com.google.firebase.Timestamp
                    val timeStampLong=timeStamp.seconds*1000+32400
                    val year = SimpleDateFormat("YYYY").format(timeStampLong)
                    val month=SimpleDateFormat("MM").format(timeStampLong)
                    val day=SimpleDateFormat("dd").format(timeStampLong)
                    val calendarDayListTemp = ArrayList<CalendarDay>()
                    val calendarDay = CalendarDay.from(year.toInt(), month.toInt()-1, day.toInt())
                    calendarDayListTemp.add(CalendarDay.from(year.toInt(), month.toInt()-1, day.toInt()))

                    val hasAlreadyDate = decoratorList.find{it.date.equals(calendarDay)}
                    if (hasAlreadyDate==null)
                    {
                        Log.d("jhs", "중복 날짜 없음")
                        if (document["type"]=="save")
                        {
                            var decorator = planDotDecorator(calendarDayListTemp, document["cost"].toString().toInt(), 0,this)
                            val data=calendarDates(calendarDay, document["cost"].toString().toInt(), 2000)
                            decoratorList.add(data)
                            binding.othersMaterialCalendar.addDecorator(decorator)
                        }
                        else
                        {
                            var decorator = planDotDecorator(calendarDayListTemp, 0, document["cost"].toString().toInt(),this)
                            val data=calendarDates(calendarDay,0 , document["cost"].toString().toInt())
                            decoratorList.add(data)
                            binding.othersMaterialCalendar.addDecorator(decorator)
                        }


                    }
                    else
                    {
                        Log.d("jhs", hasAlreadyDate.toString())

                        //decorator 전체 지우고 다시 그리기
                        decoratorList.remove(hasAlreadyDate)
                        binding.othersMaterialCalendar.removeDecorators()
                        decoratorList.forEach{
                            val tempCalendarDayList = ArrayList<CalendarDay>()
                            calendarDayListTemp.add(it.date)
                            val decorator=planDotDecorator(tempCalendarDayList, it.saving, it.spend, this)
                            binding.othersMaterialCalendar.addDecorator(decorator)
                        }

                        //decoratorList에 추가한 후 캘린더 뷰에 보이도록 추가
                        if (document["type"]=="save")
                        {
                            val data=calendarDates(calendarDay, hasAlreadyDate.saving+document["cost"].toString().toInt(), hasAlreadyDate.spend)
                            decoratorList.add(data)
                            var decorator = planDotDecorator(calendarDayListTemp, hasAlreadyDate.saving+document["cost"].toString().toInt(), hasAlreadyDate.spend,this)
                            binding.othersMaterialCalendar.addDecorator(decorator)
                        }
                        else
                        {
                            val data=calendarDates(calendarDay, hasAlreadyDate.saving, hasAlreadyDate.spend+document["cost"].toString().toInt())
                            decoratorList.add(data)
                            var decorator = planDotDecorator(calendarDayListTemp, hasAlreadyDate.saving, hasAlreadyDate.spend+document["cost"].toString().toInt(),this)
                            binding.othersMaterialCalendar.addDecorator(decorator)
                        }


                    }
                    val newItem = accountItem(document["name"].toString(), document["cost"].toString().toInt(),
                        (month.toInt()-1).toString(), day.toString(), document["type"].toString(),
                        document["angry"].toString().toInt(), document["smile"].toString().toInt())
                    allDatas.add(newItem)
                    if (month.toInt()-1==CalendarDay.today().month)
                    {
                        currentMonthDatas.add(newItem)
                        if (document["type"]=="spend")
                        {
                            selectedMonthSpend+=document["cost"].toString().toInt()
                        }
                    }
                }
                binding.recyclerView.adapter = OthersAccountAdapter(currentMonthDatas)
                binding.totalMonthSpend.text=selectedMonthSpend.toString()
            }

        //calendar 설정
        binding.othersMaterialCalendar.setDateTextAppearance(R.style.CustomDateTextAppearance)
        binding.othersMaterialCalendar.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        binding.othersMaterialCalendar.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)

        binding.othersMaterialCalendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        binding.othersMaterialCalendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

        var selectedDate: CalendarDay = CalendarDay.today()

        lateinit var calendar: MaterialCalendarView
        calendar = binding.othersMaterialCalendar
        calendar.setSelectedDate(CalendarDay.today())

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)


        val stCalendarDay = CalendarDay.from(currentYear, currentMonth, currentDate)
        val enCalendarDay = CalendarDay.from(endTimeCalendar.get(Calendar.YEAR), endTimeCalendar.get(Calendar.MONTH), endTimeCalendar.get(Calendar.DATE))

        val todayDecorator = TodayDecorator(this)	//디폴트로 오늘 날짜가 처음 selected된 상태가 될텐데 오늘 날짜를 꾸며주는 decorator


        calendar.setDateTextAppearance(R.style.CustomDateTextAppearance)
        calendar.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        calendar.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)

        calendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        calendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

        calendar.state().edit()
            .setMinimumDate(CalendarDay.from(currentYear, currentMonth-6, 1))
            .setMaximumDate(CalendarDay.from(currentYear, currentMonth, endTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()


        calendar.setOnMonthChangedListener(object: OnMonthChangedListener{
            override fun onMonthChanged(widget: MaterialCalendarView?, date: CalendarDay?) {
                if (date != null) {
                    val selectedMonthDatas = mutableListOf<accountItem>()
                    selectedMonthSpend=0
                    binding.totalMonth.text = (date.month+1).toString()
                    allDatas.forEach{
                        if (it.month.toInt()==date.month) {
                            if (it.type=="spend")
                            {
                                selectedMonthSpend+=it.cost
                            }
                            selectedMonthDatas.add(it)
                        }
                    }
                    binding.recyclerView.adapter = OthersAccountAdapter(selectedMonthDatas)
                    binding.totalMonthSpend.text=selectedMonthSpend.toString()
                }
            }
        })
        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                calendar.removeDecorator(todayDecorator)
                selectedDate = calendar.selectedDate
                Log.d("selectedDate", selectedDate.toString())

            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }


    class TodayDecorator(context: Context): DayViewDecorator {
        private var date = CalendarDay.today()
        // val drawable = context.resources.getDrawable(R.drawable.style_only_radius_10)
        val drawable = context.getDrawable(androidx.appcompat.R.drawable.abc_action_bar_item_background_material)
        override fun shouldDecorate(day: CalendarDay?): Boolean{
            return day?.equals(date)!!
        }
        override fun decorate(view: DayViewFacade?) {
//view?.addSpan(ForegroundColorSpan(Color.parseColor("#34A94B")))
            if(drawable != null) {
                view?.setSelectionDrawable(drawable)
            }
            else{
                Log.d("today_decorator","is null")
            }
        }
    }
    class MinMaxDecorator(min: CalendarDay, max:CalendarDay): DayViewDecorator {
        val maxDay = max
        val minDay = min

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return (day?.month == maxDay.month && day.day > maxDay.day)
                    || (day?.month == minDay.month && day.day < minDay.day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object: ForegroundColorSpan(Color.parseColor("#ADDEAC")){})
            view?.setDaysDisabled(true)
        }
    }

    public class CalendarViewSpan(saving: String, spend: String): LineBackgroundSpan {

        private val saving : String
        private val spend: String
        init {
            this.saving=saving
            this.spend=spend
        }


        override fun drawBackground(
            canvas: Canvas, paint: Paint, left: Int, right: Int, top: Int, baseline: Int,
            bottom: Int, text: CharSequence, start: Int, end: Int, lineNumber: Int) {

            if (saving !="0")
            {
                paint.textSize=29f
                paint.color=Color.parseColor("#0083E2")
                canvas.drawText(
                    "+"+this.saving,
                    ((start+end)/2).toFloat(), (bottom+15).toFloat(), paint)
            }
            if (spend !="0")
            {
                paint.textSize=29f
                paint.color=Color.parseColor("#EC1F1F")
                canvas.drawText(
                    "- "+this.spend,
                    ((start+end)/2).toFloat(), (bottom+40).toFloat(), paint)
            }
            paint.textSize=40f
            paint.color=Color.BLACK
        }
    }

     class planDotDecorator(dates: Collection<CalendarDay>?, saving: Int, spend:Int, context: Context) : DayViewDecorator{
        public val dates: HashSet<CalendarDay>
        private val saving:Int
        private val spend:Int
        init {
            this.dates = HashSet(dates)
            this.saving=saving
            this.spend=spend
        }
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(CalendarViewSpan(saving.toString(), spend.toString()))
        }


    }
}


data class calendarDates(val date: CalendarDay, val saving: Int, val spend: Int)

class accountItem(name:String, cost:Int, month:String, date: String, type: String, angry:Int, smile: Int)
{
    val name: String
    val cost: Int
    val month: String
    val date: String
    val type: String
    var angry: Int
    var smile: Int
    init {
        this.name=name
        this.cost=cost
        this.month=month
        this.date=date
        this.type=type
        this.angry=angry
        this.smile=smile
    }
}
