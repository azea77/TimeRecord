package com.example.timerecord.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.timerecord.data.AppDatabase
import com.example.timerecord.data.Record
import com.example.timerecord.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var importButton: Button
    private lateinit var exportButton: Button
    private lateinit var notificationsViewModel: NotificationsViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val importFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importData(uri)
            }
        }
    }

    private val exportFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportData(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        importButton = binding.importButton
        exportButton = binding.exportButton

        importButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            importFileLauncher.launch(intent)
        }

        exportButton.setOnClickListener {
            saveFile()
        }

        binding.importTextButton.setOnClickListener {
            importText()
        }

        return root
    }

    private fun importText() {
        val text = binding.importText.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(context, "请输入要导入的文本", Toast.LENGTH_SHORT).show()
            return
        }

        // 预检查所有行的格式
        val lines = text.lineSequence().toList()
        var hasError = false
        var errorLine = 0

        for ((index, line) in lines.withIndex()) {
            try {
                val parts = line.split(",")
                if (parts.size != 3) {
                    hasError = true
                    errorLine = index + 1
                    break
                }

                val startTime = dateFormat.parse(parts[0].trim())?.time
                val endTime = dateFormat.parse(parts[1].trim())?.time

                if (startTime == null || endTime == null) {
                    hasError = true
                    errorLine = index + 1
                    break
                }
            } catch (e: Exception) {
                hasError = true
                errorLine = index + 1
                break
            }
        }

        if (hasError) {
            Toast.makeText(context, "第 $errorLine 行格式错误，请检查后重试", Toast.LENGTH_SHORT).show()
            return
        }

        // 所有行格式正确，执行导入
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                var successCount = 0

                lines.forEach { line ->
                    val parts = line.split(",")
                    val startTime = dateFormat.parse(parts[0].trim())!!.time
                    val endTime = dateFormat.parse(parts[1].trim())!!.time
                    val task = parts[2].trim()

                    val record = Record(
                        startTime = startTime,
                        endTime = endTime,
                        task = task
                    )
                    database.recordDao().insert(record)
                    successCount++
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "成功导入 $successCount 条记录", Toast.LENGTH_SHORT).show()
                    binding.importText.text.clear()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "导入失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "timerecord_${getCurrentTimeString()}.csv")
        }
        exportFileLauncher.launch(intent)
    }

    private fun importData(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val database = AppDatabase.getDatabase(requireContext())
                    var successCount = 0
                    var errorCount = 0

                    reader.lineSequence().forEach { line ->
                        try {
                            val parts = line.split(",")
                            if (parts.size == 3) {
                                val startTime = dateFormat.parse(parts[0].trim())?.time
                                val endTime = dateFormat.parse(parts[1].trim())?.time
                                val task = parts[2].trim()

                                if (startTime != null && endTime != null) {
                                    val record = Record(
                                        startTime = startTime,
                                        endTime = endTime,
                                        task = task
                                    )
                                    database.recordDao().insert(record)
                                    successCount++
                                } else {
                                    errorCount++
                                }
                            } else {
                                errorCount++
                            }
                        } catch (e: Exception) {
                            errorCount++
                        }
                    }

                    withContext(Dispatchers.Main) {
                        val message = if (errorCount > 0) {
                            "导入完成：成功 $successCount 条，失败 $errorCount 条"
                        } else {
                            "成功导入 $successCount 条记录"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "导入失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exportData(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                val records = database.recordDao().getAllRecordsSync()
                
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    // 写入CSV头
                    outputStream.write("开始时间,结束时间,任务\n".toByteArray())
                    
                    // 写入数据
                    records.forEach { record ->
                        val line = "${dateFormat.format(Date(record.startTime))}," +
                                "${dateFormat.format(Date(record.endTime))}," +
                                "${record.task}\n"
                        outputStream.write(line.toByteArray())
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "成功导出 ${records.size} 条记录", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "导出失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentTimeString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}