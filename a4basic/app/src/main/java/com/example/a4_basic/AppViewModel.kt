package com.example.a4_basic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    enum class Term {W21, S21, F21, W22, S22, F22, W23, S23, F23, W24}
    class Course(val courseCode: String, var term: String, var grade: String, var description: String){
        var termClass: Term = updateTermClass()
        var gradeAsNumber = when(grade){
            "WD" -> -1
            else -> grade.toInt()
        }
        fun updateTermClass(): Term{
            return when(term){
                "W21" -> Term.W21; "S21" -> Term.S21; "F21" -> Term.F21
                "W22" -> Term.W22; "S22" -> Term.S22; "F22" -> Term.F22
                "W23" -> Term.W23; "S23" -> Term.S23; "F23" -> Term.F23
                "W24" -> Term.W24
                else -> Term.W23
            }
        }
    }
    private val courses = mutableListOf<Course>()
    private val visibleList = mutableListOf<Course>()
    var sortBy = "Course Code"
    var filterBy = "All courses"

    var editingCourse: Course? = null

    private val _visibleList = MutableLiveData<MutableList<Course>>()
    val curVisibleList : LiveData<MutableList<Course>>
        get() {
            return _visibleList
        }

    // returns whether adding was successful
    fun addCourse(courseCode: String, term: String, grade: String, description: String): Boolean{
        // add new course, update visible courses and notify view
        var exists = false
        courses.forEach{ if (it.courseCode == courseCode) exists = true }
        if (!exists) {
            courses.add(Course(courseCode, term, grade, description))
            updateVisible()
            return true
        }
        else return false
    }
    fun updateCourse(course: Course, newName: String, newTerm: String, newGrade: String, newDescription: String){
        course.term = newTerm
        course.termClass = course.updateTermClass()
        course.grade = newGrade
        course.description = newDescription
        course.gradeAsNumber = when(newGrade){
            "WD" -> -1
            else -> newGrade.toInt()
        }
        updateVisible()
    }
    fun deleteCourse(course: Course){
        courses.remove(course)
        updateVisible()
    }
    private fun sortVisible(){
        // sort visible list according to sorting criterion
        when (sortBy){
            "By Course Code" -> visibleList.sortBy { it.courseCode }
            "By Term" -> visibleList.sortBy { it.termClass }
            "By Mark" -> visibleList.sortByDescending { it.gradeAsNumber }
        }
    }
    private fun updateVisible(){
        visibleList.clear()
        // if filter is all courses just show course
        if (filterBy == "All Courses"){
            courses.forEach {
                visibleList.add(it)
            }
        }
        // if filter is cs courses only check if code starts with cs
        else if (filterBy == "CS Only"){
            courses.forEach {
                val code = it.courseCode
                if (code.length >= 2 && code.substring(0, 2).lowercase() == "cs") {
                    visibleList.add(it)
                }
            }
        }
        // if filter is math courses only check if code starts with math/stat/co
        else if (filterBy == "Math Only"){
            courses.forEach {
                val code = it.courseCode
                if (code.length >= 4 && code.substring(0, 4).lowercase() == "math" ||
                    code.length >= 4 && code.substring(0, 4).lowercase() == "stat" ||
                    code.length >= 2 && code.substring(0, 2).lowercase() == "co") {
                    visibleList.add(it)
                }
            }
        }
        // if filter is other courses only check code doesn't start with cs/math/stat/co
        else if (filterBy == "Other Only"){
            courses.forEach {
                val code = it.courseCode
                if (code.length >= 2 && code.substring(0, 2).lowercase() == "cs" ||
                    code.length >= 4 && code.substring(0, 4).lowercase() == "math" ||
                    code.length >= 4 && code.substring(0, 4).lowercase() == "stat" ||
                    code.length >= 2 && code.substring(0, 2).lowercase() == "co") { }
                else visibleList.add(it)
            }
        }
        sortVisible()
        _visibleList.value = visibleList
    }
    fun changeSort(newSortBy: String){
        sortBy = newSortBy
        updateVisible()
    }
    fun changeFilter(newFilterBy: String){
        filterBy = newFilterBy
        updateVisible()
    }
    init {
        addCourse("CS123", "W23", "90", "Some kind of CS course")
        addCourse("MATH239", "F21", "50", "Some kind of MATH course")
        addCourse("SCI239", "S22", "49", "Some kind of SCI course")
        addCourse("AAA111", "W22", "WD", "Some kind of AAA course")
    }
}