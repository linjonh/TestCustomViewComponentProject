package com.example.testproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.testproject.databinding.ActivityTestRecyclerViewDiffBinding
import com.example.testproject.databinding.ItemLayoutBinding

class TestRecyclerViewDiff : AppCompatActivity() {
    lateinit var binding: ActivityTestRecyclerViewDiffBinding
    var incrementNumber: Int = 0
    private val rcvAdapter = RCVAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestRecyclerViewDiffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RECV.adapter = rcvAdapter
        binding.addBtn.setOnClickListener {
            val toMutableList = rcvAdapter.currentList.toMutableList()
//            if (toMutableList.size > 1)
//                return@setOnClickListener
            val generateData = generateData()

//            toMutableList.addAll(generateData)
            rcvAdapter.submitList(generateData)
        }
        binding.reverseBtn.setOnClickListener {
            val toMutableList = rcvAdapter.currentList.toMutableList()
            toMutableList.reverse()
            rcvAdapter.submitList(toMutableList)
        }
    }

    fun generateData(): MutableList<ItemData> {
        val inc = 1
        Log.e("inc:", "$inc")
        val mutableListOf = mutableListOf<ItemData>()
        for (i in 1..10) {
            val itemData = ItemData(i * inc, "example ${i * inc} ")
            Log.e("data:", itemData.toString())
            mutableListOf.add(itemData)
        }
        return mutableListOf
    }
}

class RCVAdapter : ListAdapter<ItemData, RCVH>(object : DiffUtil.ItemCallback<ItemData?>() {
    override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
        val b = oldItem.id == newItem.id
        Log.e("areItemsTheSame", "$b  oldItem:${oldItem.id} newItem ${newItem.id}")

        return b
    }

    override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
        val b = oldItem.id == newItem.id && oldItem.name == newItem.name && System.identityHashCode(oldItem) == System.identityHashCode(newItem)
        Log.e("areContentsTheSame", "$b  oldItem:${System.identityHashCode(oldItem).toString()} newItem ${System.identityHashCode(newItem).toString()}")
        return b
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVH {
        val inflate = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RCVH(inflate)
    }

    override fun onBindViewHolder(holder: RCVH, position: Int) {
        val item = getItem(position)
        val s = "pos: $position id: ${item.id}  name:${item.name}"
        holder.binding.textView.text = s
    }

}

data class ItemData(
    val id: Int, val name: String
)

class RCVH(val binding: ItemLayoutBinding) : ViewHolder(binding.root) {
}