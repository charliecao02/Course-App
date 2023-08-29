package com.example.a4_basic

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class EditFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myVM = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        val root = inflater.inflate(R.layout.edit_fragment, container, false)
        val editingCourse = myVM.editingCourse!!
        val courseCode = editingCourse.courseCode
        var description = editingCourse.description
        var oldMark = ""
        var mark = editingCourse.grade
        var term = editingCourse.term
        var isWD = mark == "WD"
        val termsArr = resources.getStringArray(R.array.terms_array)

        root.findViewById<Switch>(R.id.switchEdit_WD).isChecked = isWD

        // course title
        root.findViewById<TextView>(R.id.textViewEdit_CourseCode).apply {
            text = courseCode
        }

        // course description entry
        val descriptionEntry = root.findViewById<EditText>(R.id.editTextEdit_Description)
        descriptionEntry.setText(description)
        descriptionEntry.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        description = s.toString()
                        Log.i(null, "Description: $description")
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }
        )
        // mark entry
        val markEntry = root.findViewById<EditText>(R.id.editTextEdit_Mark)
        if (mark != "WD") markEntry.setText(mark)
        else markEntry.isEnabled = false
        markEntry.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        mark = s.toString()
                        Log.i(null, "Mark: $mark")
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }
        )
        // wd switch
        root.findViewById<Switch>(R.id.switchEdit_WD).apply {
            setOnCheckedChangeListener { _, isChecked ->
                isWD = isChecked
                if (isChecked) { // disable mark entry
                    markEntry.isEnabled = false
                    oldMark = mark
                    markEntry.setText("")
                    mark = "WD"
                }
                else {
                    markEntry.isEnabled = true
                    mark = oldMark
                    markEntry.setText(mark)
                }
                Log.i(null, isWD.toString())
            }
        }
        // term spinner
        val termSpinner = root.findViewById<Spinner>(R.id.spinnerEdit_Term)
        termSpinner.setSelection(termsArr.indexOf(term))
        termSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    term = termsArr[position]
                    Log.i(null, "Term: $term")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        // cancel button
        root.findViewById<Button>(R.id.buttonEdit_Cancel).apply {
            setOnClickListener {
                // go back to main fragment
                myVM.editingCourse = null
                findNavController().navigate(R.id.mainFragment)
            }
        }
        // submit button
        root.findViewById<Button>(R.id.buttonEdit_Submit).apply {
            setOnClickListener {
                Log.i(null, "Updating: Course: $courseCode, Term: $term, Grade: $mark")
                // try to create course
                var invalidInput = false
                val gradeNum = mark.toIntOrNull()
                if (courseCode != "" && (mark == "WD" || gradeNum != null)){
                    if (mark == "WD") myVM.updateCourse(editingCourse, courseCode, term, mark, description)
                    else if ((gradeNum!! >= 0) && (gradeNum <= 100)){
                        myVM.updateCourse(editingCourse, courseCode, term, mark, description)
                    }
                    else invalidInput = true
                }
                else invalidInput = true

                if (invalidInput){
                    Toast.makeText(activity,"Invalid course info!", Toast.LENGTH_SHORT).show()
                }
                else {
                    // go back to main fragment
                    myVM.editingCourse = null
                    findNavController().navigate(R.id.mainFragment)
                }
            }
        }

        return root
    }
}