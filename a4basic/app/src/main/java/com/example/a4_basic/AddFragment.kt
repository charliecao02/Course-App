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

class AddFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myVM = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        val root = inflater.inflate(R.layout.add_fragment, container, false)
        var courseCode = ""
        var description = ""
        var oldMark = ""
        var mark = ""
        var term = ""
        var isWD = false
        val termsArr = resources.getStringArray(R.array.terms_array)
        // course code entry
        root.findViewById<EditText>(R.id.editTextAdd_CourseCode).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        courseCode = s.toString()
                        Log.i(null, "Course name: $courseCode")
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }
        )
        // course description entry
        root.findViewById<EditText>(R.id.editTextAdd_Description).addTextChangedListener(
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
        root.findViewById<EditText>(R.id.editTextAdd_Mark).addTextChangedListener(
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
        root.findViewById<Switch>(R.id.switchAdd_WD).apply {
            setOnCheckedChangeListener { _, isChecked ->
                isWD = isChecked
                val markField = root.findViewById<EditText>(R.id.editTextAdd_Mark)
                if (isChecked) { // disable mark entry
                    markField.isEnabled = false
                    oldMark = mark
                    markField.setText("")
                    mark = "WD"
                }
                else {
                    markField.isEnabled = true
                    mark = oldMark
                    markField.setText(mark)
                }
                Log.i(null, isWD.toString())
            }
        }
        // term spinner
        root.findViewById<Spinner>(R.id.spinnerAdd_Term).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    term = termsArr[position]
                    Log.i(null, "Term: $term")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        // cancel button
        root.findViewById<Button>(R.id.buttonAdd_Cancel).apply {
            setOnClickListener {
                // go back to main fragment
                findNavController().navigate(R.id.mainFragment)
            }
        }
        // create button
        root.findViewById<Button>(R.id.buttonAdd_Create).apply {
            setOnClickListener {
                Log.i(null, "Creating: Course: $courseCode, Term: $term, Grade: $mark")
                // try to create course
                var success = true
                var invalidInput = false
                val gradeNum = mark.toIntOrNull()
                if (courseCode != "" && (mark == "WD" || gradeNum != null)){
                    if (mark == "WD") success = myVM.addCourse(courseCode, term, mark, description)
                    else if ((gradeNum!! >= 0) && (gradeNum <= 100)){
                        success = myVM.addCourse(courseCode, term, mark, description)
                    }
                    else invalidInput = true
                }
                else invalidInput = true

                if (!success) {
                    Toast.makeText(activity,"Course already exists!",Toast.LENGTH_SHORT).show()
                }
                else if (invalidInput){
                    Toast.makeText(activity,"Invalid course info!",Toast.LENGTH_SHORT).show()
                }
                else {
                    // go back to main fragment
                    findNavController().navigate(R.id.mainFragment)
                }
            }
        }
        return root
    }
}