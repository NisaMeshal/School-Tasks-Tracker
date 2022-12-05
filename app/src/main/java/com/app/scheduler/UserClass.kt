package com.app.scheduler

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
Parcelizable object. Once this object is instantiated, it can be placed in a bundle due to its
parcel nature. This class is used for the classes that the user enters at the main landing page.
 */

@Parcelize
public class UserClass(val name : String = "", val classId : Int) : Parcelable {
    var taskList : ArrayList<Task> = ArrayList()
}