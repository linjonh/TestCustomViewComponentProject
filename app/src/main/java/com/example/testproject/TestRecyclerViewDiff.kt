package com.example.testproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testproject.databinding.ActivityTestRecyclerViewDiffBinding

class TestRecyclerViewDiff : AppCompatActivity() {
    lateinit var binding: ActivityTestRecyclerViewDiffBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestRecyclerViewDiffBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}