package com.app.scheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationCompat.getExtras
import androidx.fragment.app.Fragment
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate


class ClassesFragment : Fragment() {
    var classesList : ArrayList<UserClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            classesList = savedInstanceState.getParcelableArrayList<UserClass>("classes_list") as ArrayList<UserClass>
        }

        //initial data population. Would not be here if the app was deployed.
        classesList.add(UserClass("App Dev", 0))
        classesList.add(UserClass("Database Management", 1))
        classesList.add(UserClass("ODE", 2))

        classesList[0].taskList.add(Task("Quiz 1", LocalDate.of(2022, 11, 1), TASK_TYPE.QUIZ))
        classesList[0].taskList.add(Task("Homework 3", LocalDate.of(2022, 11, 3), TASK_TYPE.ASSIGNMENT))
        classesList[0].taskList.add(Task("Unit 5 Exam", LocalDate.of(2022, 11, 25), TASK_TYPE.EXAM))
    }

    override fun onPause() {
        super.onPause()

        val args = Bundle()
        args.putParcelableArrayList("classes_list", classesList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflates the view for the fragment
        val rootView = inflater.inflate(R.layout.fragment_classes, container, false)
        val recyclerView : RecyclerView = rootView.findViewById(R.id.class_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        var adapter = ClassAdapter(classesList)
        recyclerView.adapter = adapter

        //listens for add button click and notifies the adapter that the list has been updated.
        //refreshes the list on update
        rootView.findViewById<Button>(R.id.add_class_button).setOnClickListener {
            val newClassName = rootView.findViewById<EditText>(R.id.add_class).text.toString()
            classesList.add(UserClass(newClassName, classesList.size))
            adapter.notifyItemInserted(classesList.size - 1)
        }

        return rootView
    }

    //Recycler view adapter. Takes the arraylist and binds the items onto the recycler view to be displayed
    private class ClassAdapter(private val classes: ArrayList<UserClass>) :
        RecyclerView.Adapter<ClassAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) :RecyclerView.ViewHolder(view) {
            var itemTextView : TextView = view.findViewById(R.id.class_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.class_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = classes[position].name
            holder.itemTextView.text = item
            holder.itemView.setOnClickListener{ itemView : View ->
                val args = Bundle()
                args.putString(ARG_CLASS_ID, item)
                args.putParcelableArrayList("classes_list", classes)
                Navigation.findNavController(itemView).navigate(R.id.tasks_fragment, args)
            }
        }

        override fun getItemCount(): Int {
            return classes.size
        }
    }
}