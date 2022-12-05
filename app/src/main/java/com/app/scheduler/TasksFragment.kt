package com.app.scheduler

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

const val ARG_CLASS_ID = "class_name"

class TasksFragment : Fragment(){

    private var selectedIndex : Int = 0
    var className = "App Dev"
    var classesList : ArrayList<UserClass> = ArrayList()
    lateinit var newTaskName : EditText
    lateinit var newTaskNotes : EditText
    lateinit var newDueDate : TextView
    lateinit var taskType : TASK_TYPE
    lateinit var taskSpinner : Spinner
    lateinit var rvAdapter : TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {className = it.getString(ARG_CLASS_ID).toString()}
        arguments?.let { classesList = it.getParcelableArrayList("classes_list")!! }

        for(item in classesList) {
            if(item.name == className) {
                selectedIndex = classesList.indexOf(item)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflates the view for the fragment
        val rootView = inflater.inflate(R.layout.fragment_tasks, container, false)
        val recyclerView : RecyclerView = rootView.findViewById(R.id.tasks_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        var adapter = TaskAdapter(classesList[selectedIndex].taskList)
        recyclerView.adapter = adapter
        rvAdapter = adapter

        val calendarButton = rootView.findViewById<Button>(R.id.calendar_button).setOnClickListener { calendarButtonClick() }
        val addTaskButton = rootView.findViewById<Button>(R.id.add_new_task).setOnClickListener{ addTaskButtonClick() }
        newTaskName = rootView.findViewById(R.id.new_task_name)
        newTaskNotes = rootView.findViewById(R.id.new_task_notes)
        newDueDate = rootView.findViewById(R.id.new_task_due)
        taskSpinner = rootView.findViewById(R.id.task_spinner)

        return rootView
    }

    private fun addTaskButtonClick() {
        taskType = when(taskSpinner.selectedItem.toString()) {
            "Exam" -> TASK_TYPE.EXAM
            "Quiz" -> TASK_TYPE.QUIZ
            else -> TASK_TYPE.ASSIGNMENT
        }

        classesList[selectedIndex].taskList.add(Task(newTaskName.text.toString(), LocalDate.parse(newDueDate.text.toString()) , taskType))
        classesList[selectedIndex].taskList[classesList[selectedIndex].taskList.size - 1].notes = newTaskNotes.text.toString()

        rvAdapter.notifyItemInserted(classesList[selectedIndex].taskList.size - 1)

        val startTime = newDueDate.text.toString() + "T00:00:00"
        val endTime = newDueDate.text.toString() + "T01:00:00"

        // Parsing the date and time
        val SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formattedStartTime = SimpleDateFormat.parse(startTime)
        val formattedEndTime = SimpleDateFormat.parse(endTime)

        val mIntent = Intent(Intent.ACTION_EDIT)
        mIntent.type = "vnd.android.cursor.item/event"
        mIntent.putExtra(CalendarContract.Events.DTSTART, formattedStartTime)
        mIntent.putExtra("time", true)
        mIntent.putExtra("rule", "FREQ=YEARLY")
        mIntent.putExtra(CalendarContract.Events.DTEND, formattedEndTime)
        mIntent.putExtra(CalendarContract.Events.TITLE, newTaskName.text.toString())
        startActivity(mIntent)
    }

    /*
        Listener for the calendar button that will help in setting the due date
        Online reference : https://www.geeksforgeeks.org/datepicker-in-android/
     */
    private fun calendarButtonClick() {
        //get instance of the calendar
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = this.context?.let { DatePickerDialog( it,
            {view, year, monthOfYear, dayOfMonth ->
                var month : Int = monthOfYear + 1
                var monthFormat : String = ""+month
                var dayFormat : String = ""+dayOfMonth
                if(month < 10) {
                    monthFormat = "0"+month
                }
                if(dayOfMonth < 10) {
                    dayFormat = "0"+dayOfMonth
                }

                newDueDate.text = (year.toString() + "-" + monthFormat + "-" + dayFormat)
            },
            year, month, day
        )
        }
        datePickerDialog?.show()
    }

    class TaskAdapter(private val tasks : ArrayList<Task>) :
        RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

        //place all the fields of the card that you want to variables to in here.
        inner class  MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var itemName : TextView = view.findViewById(R.id.task_title)
            var itemDate : TextView = view.findViewById(R.id.due_date)
            var itemNotes : TextView = view.findViewById(R.id.notes)
            var deleteButton : Button = view.findViewById(R.id.delete_item)
            var itemBackground = view
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)

            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = tasks[position]
            holder.itemName.text = item.name
            holder.itemDate.text = item.dueDate.toString()

            if (item.notes != null) {
                holder.itemNotes.text = item.notes.toString()
            }

            holder.deleteButton.setOnClickListener { itemView : View ->
                tasks.remove(item)
                notifyItemRemoved(position)
            }

            when (item.taskType) {
                TASK_TYPE.EXAM -> holder.itemBackground.setBackgroundColor(Color.parseColor("#fccaca"))
                TASK_TYPE.QUIZ -> holder.itemBackground.setBackgroundColor(Color.parseColor("#faf9b4"))
                else -> holder.itemBackground.setBackgroundColor(Color.parseColor("#bfffbd"))
            }
        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }
}