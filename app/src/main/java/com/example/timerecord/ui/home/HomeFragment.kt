package com.example.timerecord.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timerecord.R
import com.example.timerecord.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var inputText: EditText
    private lateinit var recordButton: Button
    private lateinit var addTagButton: Button
    private lateinit var deleteTagButton: Button
    private lateinit var tagRecyclerView: RecyclerView
    private lateinit var textHome: TextView
    private lateinit var tagAdapter: TagAdapter
    private var lastRecordTime: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        inputText = binding.inputText
        recordButton = binding.recordButton
        addTagButton = binding.addTagButton
        deleteTagButton = binding.deleteTagButton
        tagRecyclerView = binding.tagRecyclerView
        textHome = binding.textHome

        tagAdapter = TagAdapter(mutableListOf())
        tagRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tagRecyclerView.adapter = tagAdapter

        // 为 RecyclerView 添加居中对齐的设置
//        val layoutManager = tagRecyclerView.layoutManager as LinearLayoutManager
//        layoutManager.gravity = Gravity.CENTER

        recordButton.setOnClickListener {
            val input = inputText.text.toString().trim()
            if (input.isNotEmpty()) {
                val currentTime = dateFormat.format(Date())
                val log = if (lastRecordTime != null) {
                    "${lastRecordTime}, $currentTime, $input\n"
                } else {
                    "首次记录, $currentTime, $input\n"
                }
                textHome.append(log)
                lastRecordTime = currentTime
                inputText.text.clear()
            }
        }

        addTagButton.setOnClickListener {
            val input = inputText.text.toString().trim()
            if (input.isNotEmpty()) {
                tagAdapter.addTag(input)
                inputText.text.clear()
            }
        }

        deleteTagButton.setOnClickListener {
            tagAdapter.removeSelectedTag()
        }

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}