package com.example.a4_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val myVM = ViewModelProvider(this)[AppViewModel::class.java]
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}