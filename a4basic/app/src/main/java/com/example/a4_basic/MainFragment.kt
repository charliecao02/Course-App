package com.example.a4_basic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myVM = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        // add button
        root.findViewById<FloatingActionButton>(R.id.goToAddButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.addFragment)
            }
        }

        // filter by spinner
        val filterArr = resources.getStringArray(R.array.filter_array)
        val filterSpinner = root.findViewById<Spinner>(R.id.spinnerMain_Filter)
        filterSpinner.setSelection(filterArr.indexOf(myVM.filterBy))
        filterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newFilter = filterArr[position]
                    myVM.changeFilter(newFilter)
                    Log.i(null, "New filter by: $newFilter")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // sort by spinner
        val sortArr = resources.getStringArray(R.array.sort_array)
        val sortSpinner = root.findViewById<Spinner>(R.id.spinnerMain_Sort)
        sortSpinner.setSelection(sortArr.indexOf(myVM.sortBy))
        sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newSort = sortArr[position]
                    myVM.changeSort(newSort)
                    Log.i(null, "New sort by: $newSort")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val parentLayout = root.findViewById<LinearLayout>(R.id.linearLayoutMain)
        // create the course entries
        parentLayout.also { parent ->
            myVM.curVisibleList.observe(viewLifecycleOwner){ courseList ->
                parent.removeAllViews()
                courseList.forEach {
                    addCourseToLayout(parent, it, myVM)
                }
            }
        }
        return root
    }
    fun addCourseToLayout(parentLayout: LinearLayout, course: AppViewModel.Course, myVM: AppViewModel) {
        val courseLayout = LayoutInflater.from(context).inflate(R.layout.course_layout, parentLayout, false)
        val nameText = courseLayout.findViewById<TextView>(R.id.course_nameText)
        val gradeText = courseLayout.findViewById<TextView>(R.id.course_gradeText)
        val termText = courseLayout.findViewById<TextView>(R.id.course_termText)
        val descriptionText = courseLayout.findViewById<TextView>(R.id.course_descriptionText)
        val outerLayout = courseLayout.findViewById<ConstraintLayout>(R.id.course_outerLayout)

        // set all the text fields
        nameText.text = course.courseCode
        gradeText.text = course.grade
        termText.text = course.term
        descriptionText.text = course.description
        val gradeNum = course.gradeAsNumber
        // set background color
        outerLayout.setBackgroundColor(
            if (gradeNum == -1) resources.getColor(R.color.course_WD, null)
            else if ((gradeNum >= 0) && (gradeNum < 50)) resources.getColor(R.color.course_0_50, null)
            else if ((gradeNum >= 50) && (gradeNum < 60)) resources.getColor(R.color.course_50_60, null)
            else if ((gradeNum >= 60) && (gradeNum < 91)) resources.getColor(R.color.course_60_91, null)
            else if ((gradeNum >= 91) && (gradeNum < 96)) resources.getColor(R.color.course_91_96, null)
            else resources.getColor(R.color.course_96_100, null)
        )
        parentLayout.addView(courseLayout)

        // delete button behaviour
        outerLayout.findViewById<ImageButton>(R.id.course_deleteButton).apply {
            setOnClickListener {
                myVM.deleteCourse(course)
            }
        }

        // edit button behaviour
        outerLayout.findViewById<ImageButton>(R.id.course_editButton).apply {
            setOnClickListener {
                myVM.editingCourse = course
                findNavController().navigate(R.id.editFragment)
            }
        }
    }
}