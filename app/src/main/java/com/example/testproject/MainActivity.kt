package com.example.testproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.testproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<View>(R.id.button).setOnClickListener {
            startActivity(Intent(this, TestViewActivity::class.java))
        }
        binding.RCVBtn.setOnClickListener {
            startActivity(Intent(this, TestRecyclerViewDiff::class.java))
        }
    }
}