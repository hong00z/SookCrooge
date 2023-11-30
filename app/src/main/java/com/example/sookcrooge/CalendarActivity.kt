package com.example.sookcrooge



import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.databinding.MyAccountBookBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import java.util.Calendar


class CalendarActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = MyAccountBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialCalendar.setDateTextAppearance(R.style.CustomDateTextAppearance)
        binding.materialCalendar.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        binding.materialCalendar.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)

        binding.materialCalendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        binding.materialCalendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

        var selectedDate: CalendarDay = CalendarDay.today()

        lateinit var calendar: MaterialCalendarView
        calendar = binding.materialCalendar
        calendar.setSelectedDate(CalendarDay.today())

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        val currentDate = startTimeCalendar.get(Calendar.DATE)

        endTimeCalendar.set(Calendar.MONTH, currentMonth+3)//이번 달부터 3달 후까지만 보여지도록 함

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

        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                calendar.removeDecorator(todayDecorator)
                selectedDate = calendar.selectedDate
                Log.d("selectedDate", selectedDate.toString())

            }
        })
        var datas = mutableListOf<String>("사과","라임","레몬","수박")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = AccountAdapter(datas)

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




}

