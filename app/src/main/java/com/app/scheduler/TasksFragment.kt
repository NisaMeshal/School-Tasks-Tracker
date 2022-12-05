package com.app.scheduler

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
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
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

const val ARG_CLASS_ID = "class_name"

class TasksFragment : Fragment(){

    private var userClass: UserClass? = null
    var className = "App Dev"
    var classesList : ArrayList<UserClass> = ArrayList()
    lateinit var newTaskName : EditText
    lateinit var newTaskNotes : EditText
    lateinit var newDueDate : TextView
    lateinit var taskType : TASK_TYPE
    lateinit var taskSpinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {className = it.getString(ARG_CLASS_ID).toString()}
        arguments?.let { classesList = it.getParcelableArrayList("classes_list")!! }

        for(item in classesList) {
            if(item.name == className) {
                userClass = item
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflates the view for the fragment
        val rootView = inflater.inflate(R.layout.fragment_tasks, container, false)
        val recyclerView : RecyclerView = rootView.findViewById(R.id.tasks_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if (userClass != null) {
            var adapter = TaskAdapter(userClass!!.taskList)
            recyclerView.adapter = adapter
        }

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

        userClass?.taskList?.add(Task(newTaskName.text.toString(), LocalDate.parse(newDueDate.toString()), taskType))
        userClass?.taskList?.get(userClass!!.taskList.size - 1)?.notes = newTaskNotes.toString()
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
                newDueDate.text = (year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
            },
            year, month, day
        )
        }
        datePickerDialog?.show()
    }

    private class TaskAdapter(private val tasks : ArrayList<Task>) :
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