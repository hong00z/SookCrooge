package com.example.sookcrooge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.sookcrooge.OthersAccountAdapter
import com.example.sookcrooge.R

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.SyncStateContract.Helpers.update
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.ActivityOthersAccountBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth= Firebase.auth

        val smileText = findViewById<TextView>(R.id.smileText)
        val angryText = findViewById<TextView>(R.id.angryText)
        val intent= intent
        val otherUserUID=intent.getStringExtra("uid")
        binding.toolbar.title=intent.getStringExtra("nickname")+"의 가계부"

        val decoratorList= mutableListOf<calendarDates>()
        val allDatas = mutableListOf<accountItem>()
        val selectedMonthDatas=mutableListOf<accountItem>()
        var adapter= OthersAccountAdapter(selectedMonthDatas)
        var selectedMonthSpend=0
        var currentSelectedMonth=CalendarDay.today().month

        val dataQuery = db.collection("users").document(otherUserUID.toString()).collection("accountBook").orderBy("date")
        dataQuery.addSnapshotListener { snapshots, e ->
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        var timeStamp = dc.document.data["date"] as com.google.firebase.Timestamp
                        val timeStampLong=timeStamp.seconds*1000+32400000
                        val year = SimpleDateFormat("YYYY").format(timeStampLong)
                        val month=SimpleDateFormat("MM").format(timeStampLong)
                        val day=SimpleDateFormat("dd").format(timeStampLong)
                        val calendarDayListTemp = ArrayList<CalendarDay>()
                        val calendarDay = CalendarDay.from(year.toInt(), month.toInt()-1, day.toInt())
                        calendarDayListTemp.add(CalendarDay.from(year.toInt(), month.toInt()-1, day.toInt()))
                        val hasAlreadyDate = decoratorList.find{it.date.equals(calendarDay)}
                        if (hasAlreadyDate==null)
                        {
                            if (dc.document.data["type"]=="save")
                            {
                                var decorator = planDotDecorator(calendarDayListTemp, dc.document.data["cost"].toString().toInt(), 0,this)
                                val data=calendarDates(calendarDay, dc.document.data["cost"].toString().toInt(), 0)
                                decoratorList.add(data)
                                binding.othersMaterialCalendar.addDecorator(decorator)
                            }
                            else
                            {
                                var decorator = planDotDecorator(calendarDayListTemp, 0, dc.document.data["cost"].toString().toInt(),this)
                                val data=calendarDates(calendarDay,0 , dc.document.data["cost"].toString().toInt())
                                decoratorList.add(data)
                                binding.othersMaterialCalendar.addDecorator(decorator)
                            }
                        }
                        else
                        {

                            //decorator 전체 지우고 다시 그리기
                            decoratorList.remove(hasAlreadyDate)
                            binding.othersMaterialCalendar.removeDecorators()

                            for (i in 0..decoratorList.size-1)
                            {
                                val tempCalendarDayList = ArrayList<CalendarDay>()
                                tempCalendarDayList.add(decoratorList[i].date)
                                var currentDecorator=planDotDecorator( tempCalendarDayList, decoratorList[i].saving, decoratorList[i].spend, this)
                                binding.othersMaterialCalendar.addDecorator(currentDecorator)
                            }

                            //decoratorList에 추가한 후 캘린더 뷰에 보이도록 추가
                            if (dc.document.data["type"]=="save")
                            {
                                val data=calendarDates(calendarDay, hasAlreadyDate.saving+dc.document.data["cost"].toString().toInt(), hasAlreadyDate.spend)
                                decoratorList.add(data)
                                var decorator = planDotDecorator(calendarDayListTemp, hasAlreadyDate.saving+dc.document.data["cost"].toString().toInt(), hasAlreadyDate.spend,this)
                                binding.othersMaterialCalendar.addDecorator(decorator)
                            }
                            else
                            {
                                val data=calendarDates(calendarDay, hasAlreadyDate.saving, hasAlreadyDate.spend+dc.document.data["cost"].toString().toInt())
                                decoratorList.add(data)
                                var decorator = planDotDecorator(calendarDayListTemp, hasAlreadyDate.saving, hasAlreadyDate.spend+dc.document.data["cost"].toString().toInt(),this)
                                binding.othersMaterialCalendar.addDecorator(decorator)
                            }


                        }
                        val newItem = accountItem(dc.document.id, dc.document.data["name"].toString(), dc.document.data["cost"].toString().toInt(),
                            (month.toInt()-1).toString(), day.toString(), dc.document.data["type"].toString(),
                            dc.document.data["angry"].toString().toInt(), dc.document.data["smile"].toString().toInt(), dc.document.data["tag"].toString())
                        allDatas.add(newItem)
                        if (month.toInt()-1== currentSelectedMonth)
                        {
                            selectedMonthDatas.add(newItem)
                            if (dc.document.data["type"]=="spend")
                            {
                                selectedMonthSpend+=dc.document.data["cost"].toString().toInt()
                            }
                        }
                        selectedMonthDatas.sortWith(compareBy { it.date.toInt() })
                        runOnUiThread{
                            binding.totalMonthSpend.text=selectedMonthSpend.toString()
                            binding.recyclerView.adapter = adapter
                            OthersAccountAdapter(selectedMonthDatas).notifyDataSetChanged()
                        }

                        (binding.recyclerView.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                            override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                                smileText.text= (data.smile+1).toString()
                                db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("smile", (data.smile+1))
                            }
                            override fun onAngryClick(binding: AccountListBinding, data: accountItem) {
                                angryText.text= (data.angry+1).toString()
                                db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("angry", (data.angry+1))
                            }
                        })
                    }
                    DocumentChange.Type.MODIFIED -> {
                        for (i in 0..<allDatas.size) {
                            if (dc.document.id == allDatas[i].documentID)
                            {
                                allDatas[i].smile = dc.document.data["smile"].toString().toInt()
                                allDatas[i].angry = dc.document.data["angry"].toString().toInt()
                                break
                            }
                        }
                        for (i in 0..<selectedMonthDatas.size)
                        {
                            if (dc.document.id == selectedMonthDatas[i].documentID)
                            {
                                selectedMonthDatas[i].smile = dc.document.data["smile"].toString().toInt()
                                selectedMonthDatas[i].angry = dc.document.data["angry"].toString().toInt()
                                runOnUiThread{
                                    binding.recyclerView.adapter = adapter
                                    OthersAccountAdapter(selectedMonthDatas).notifyDataSetChanged()
                                }
                                break
                            }
                        }
                        (binding.recyclerView.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                            override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                                smileText.text= (data.smile+1).toString()
                                db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("smile", (data.smile+1))
                            }
                            override fun onAngryClick(binding: AccountListBinding, data: accountItem) {
                                angryText.text= (data.angry+1).toString()
                                db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("angry", (data.angry+1))
                            }
                        })
                    }
                    else -> {}
                }
            }
        }


        //calendar 설정
        binding.othersMaterialCalendar.setDateTextAppearance(R.style.CustomDateTextAppearance)
        binding.othersMaterialCalendar.setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        binding.othersMaterialCalendar.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)

        binding.othersMaterialCalendar.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(
            R.array.custom_months)))
        binding.othersMaterialCalendar.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(
            R.array.custom_weekdays)))

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
                    selectedMonthDatas.clear()
                    selectedMonthSpend=0
                    binding.totalMonth.text = (date.month+1).toString()
                    currentSelectedMonth=date.month
                    allDatas.forEach{
                        if (it.month.toInt()==date.month) {
                            if (it.type=="spend")
                            {
                                selectedMonthSpend+=it.cost
                            }
                            selectedMonthDatas.add(it)
                        }
                    }
                    selectedMonthDatas.sortWith(compareBy { it.date.toInt() })
                    binding.recyclerView.adapter = OthersAccountAdapter(selectedMonthDatas)
                    adapter.notifyDataSetChanged()
                    binding.totalMonthSpend.text=selectedMonthSpend.toString()
                    (binding.recyclerView.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                        override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                            smileText.text= (data.smile+1).toString()
                            db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("smile", (data.smile+1))
                        }
                        override fun onAngryClick(binding: AccountListBinding, data: accountItem) {
                            angryText.text= (data.angry+1).toString()
                            db.collection("users").document(otherUserUID.toString()).collection("accountBook").document(data.documentID).update("angry", (data.angry+1))
                        }
                    })

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
            view?.addSpan(ForegroundColorSpan(Color.parseColor("#34A94B")))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


data class calendarDates(val date: CalendarDay, val saving: Int, val spend: Int)

class accountItem(documentID: String, name:String, cost:Int, month:String, date: String, type: String, angry:Int, smile: Int, tag:String)
{
    val documentID:String
    val name: String
    val cost: Int
    val month: String
    val date: String
    val type: String
    var angry: Int
    var smile: Int
    val tag:String
    init {
        this.documentID=documentID
        this.name=name
        this.cost=cost
        this.month=month
        this.date=date
        this.type=type
        this.angry=angry
        this.smile=smile
        this.tag=tag
    }
}
