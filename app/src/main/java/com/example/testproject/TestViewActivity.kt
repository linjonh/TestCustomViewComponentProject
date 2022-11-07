package com.example.testproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testproject.databinding.ActivityTestViewBinding

class TestViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.testViewGroup.textStrings = mutableListOf(
            "1",
            "2",
            "3",
            "testViewGroup",
            "3",
            "好的哈",
            "3",
            "好的哈",
            "dddddd",
            "好的哈",
            "dddddd",
            "yoau de",
            "你好",
            " AppCompatActivity",
            " testViewGroup",
            " ActivityTestViewBinding",
            " AppCompatActivity",
        )
    }
}