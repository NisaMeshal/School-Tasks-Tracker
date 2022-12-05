package com.app.scheduler

import java.time.LocalDate

enum class TASK_TYPE ( var taskName : String){
    ASSIGNMENT ("Assignment"), QUIZ("Quiz"), EXAM("Exam")
}

data class Task (
    var name: String = "",
    var dueDate : LocalDate = LocalDate.now(),
    var taskType: TASK_TYPE = TASK_TYPE.ASSIGNMENT
){

    var notes: String? = null
}