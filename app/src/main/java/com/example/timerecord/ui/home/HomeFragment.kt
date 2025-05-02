package com.example.timerecord.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timerecord.R
import com.example.timerecord.data.Record
import com.example.timerecord.data.Tag
import com.example.timerecord.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private lateinit var recordTable: TableLayout
    private lateinit var tagAdapter: TagAdapter
    private lateinit var recordTableAdapter: RecordTableAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var lastRecordTime: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        inputText = binding.inputText
        recordButton = binding.recordButton
        addTagButton = binding.addTagButton
        deleteTagButton = binding.deleteTagButton
        tagRecyclerView = binding.tagRecyclerView
        recordTable = binding.recordTable

        tagAdapter = TagAdapter(mutableListOf())
        tagRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tagRecyclerView.adapter = tagAdapter

        recordTableAdapter = RecordTableAdapter(requireContext(), recordTable) { record ->
            homeViewModel.updateRecord(record)
        }

        // 观察记录数据
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.allRecords.collectLatest { records ->
                recordTableAdapter.clearRecords()
                records.forEach { record ->
                    recordTableAdapter.addRecord(record)
                }
            }
        }

        // 观察标签数据
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.allTags.collectLatest { tags ->
                tagAdapter.updateTags(tags.map { it.name })
            }
        }

        recordButton.setOnClickListener {
            val input = inputText.text.toString().trim()
            if (input.isNotEmpty()) {
                val currentTime = dateFormat.format(Date())
                val record = if (lastRecordTime != null) {
                    Record(startTime = lastRecordTime!!, endTime = currentTime, task = input)
                } else {
                    Record(startTime = "首次记录", endTime = currentTime, task = input)
                }
                homeViewModel.insertRecord(record)
                lastRecordTime = currentTime
                inputText.text.clear()
            }
        }

        addTagButton.setOnClickListener {
            val input = inputText.text.toString().trim()
            if (input.isNotEmpty()) {
                homeViewModel.insertTag(Tag(name = input))
                tagAdapter.addTag(input)
                inputText.text.clear()
            }
        }

        deleteTagButton.setOnClickListener {
            val selectedTag = tagAdapter.getSelectedTag()
            if (selectedTag != null) {
                homeViewModel.deleteTag(Tag(name = selectedTag))
                tagAdapter.removeSelectedTag()
                inputText.text.clear()
            }
        }

        // 设置 TagAdapter 的点击事件监听器
        tagAdapter.setOnTagClickListener { tag ->
            inputText.setText(tag)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}