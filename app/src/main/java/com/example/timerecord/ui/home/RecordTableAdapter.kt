package com.example.timerecord.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.timerecord.R
import com.example.timerecord.data.Record
import java.text.SimpleDateFormat
import java.util.*

class RecordTableAdapter(
    private val context: Context,
    private val tableLayout: TableLayout,
    private val onRecordUpdate: ((Record) -> Unit)? = null
) {
    private val records = mutableListOf<Record>()
    private var editingRow: TableRow? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun addRecord(record: Record) {
        records.add(record)
        addRowToTable(record)
    }

    private fun addRowToTable(record: Record) {
        val row = TableRow(context).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // 开始时间
        val startTimeView = TextView(context).apply {
            text = formatTime(record.startTime)
            setPadding(8, 8, 8, 8)
        }
        row.addView(startTimeView)

        // 结束时间
        val endTimeView = TextView(context).apply {
            text = formatTime(record.endTime)
            setPadding(8, 8, 8, 8)
        }
        row.addView(endTimeView)

        // 任务
        val taskView = TextView(context).apply {
            text = record.task
            setPadding(8, 8, 8, 8)
        }
        row.addView(taskView)

        // 设置双击事件
        row.setOnClickListener { view ->
            if (view is TableRow) {
                if (editingRow == view) {
                    // 如果已经在编辑状态，则保存更改
                    saveRowChanges(view, record)
                    editingRow = null
                } else {
                    // 如果不在编辑状态，则进入编辑状态
                    if (editingRow != null) {
                        // 保存之前正在编辑的行
                        val index = tableLayout.indexOfChild(editingRow!!) - 1
                        if (index >= 0 && index < records.size) {
                            saveRowChanges(editingRow!!, records[index])
                        }
                    }
                    makeRowEditable(view, record)
                    editingRow = view
                }
            }
        }

        tableLayout.addView(row)
    }

    private fun makeRowEditable(row: TableRow, record: Record) {
        // 清除行中的所有视图
        row.removeAllViews()

        // 添加可编辑的开始时间
        val startTimeEdit = EditText(context).apply {
            setText(formatTime(record.startTime))
            setPadding(8, 8, 8, 8)
            hint = "yyyy-MM-dd HH:mm:ss"
        }
        row.addView(startTimeEdit)

        // 添加可编辑的结束时间
        val endTimeEdit = EditText(context).apply {
            setText(formatTime(record.endTime))
            setPadding(8, 8, 8, 8)
            hint = "yyyy-MM-dd HH:mm:ss"
        }
        row.addView(endTimeEdit)

        // 添加可编辑的任务
        val taskEdit = EditText(context).apply {
            setText(record.task)
            setPadding(8, 8, 8, 8)
        }
        row.addView(taskEdit)
    }

    private fun saveRowChanges(row: TableRow, record: Record) {
        // 清除行中的所有视图
        row.removeAllViews()

        // 更新记录数据
        val startTimeText = (row.getChildAt(0) as EditText).text.toString()
        val endTimeText = (row.getChildAt(1) as EditText).text.toString()
        record.startTime = parseTime(startTimeText)
        record.endTime = parseTime(endTimeText)
        record.task = (row.getChildAt(2) as EditText).text.toString()

        // 通知更新
        onRecordUpdate?.invoke(record)

        // 重新添加不可编辑的视图
        val startTimeView = TextView(context).apply {
            text = formatTime(record.startTime)
            setPadding(8, 8, 8, 8)
        }
        row.addView(startTimeView)

        val endTimeView = TextView(context).apply {
            text = formatTime(record.endTime)
            setPadding(8, 8, 8, 8)
        }
        row.addView(endTimeView)

        val taskView = TextView(context).apply {
            text = record.task
            setPadding(8, 8, 8, 8)
        }
        row.addView(taskView)
    }

    private fun formatTime(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    private fun parseTime(timeText: String): Long {
        return try {
            dateFormat.parse(timeText)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    fun clearRecords() {
        records.clear()
        // 保留表头
        while (tableLayout.childCount > 1) {
            tableLayout.removeViewAt(1)
        }
    }
} 