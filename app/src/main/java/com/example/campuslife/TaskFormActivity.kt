package com.example.campuslife

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslife.adapters.SpinnerAdapter
import com.example.campuslife.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnCategorySelector: TextView
    private lateinit var switchImportant: androidx.appcompat.widget.SwitchCompat
    private lateinit var btnSave: Button

    private var calendar = Calendar.getInstance()
    private val categories = arrayListOf("Study 📚", "Work 💼", "Personal ✨", "Fitness 🍎", "+ Add New")
    private var selectedCategory = "Study 📚"

    private var existingTaskId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        etTitle = findViewById(R.id.edtTaskTitle)
        etDescription = findViewById(R.id.edtTaskDescription)
        etDate = findViewById(R.id.edtTaskDate)
        etTime = findViewById(R.id.edtTaskTime)
        btnCategorySelector = findViewById(R.id.btnCategorySelector)
        switchImportant = findViewById(R.id.switchImportant)
        btnSave = findViewById(R.id.btnSaveTask)

        // READ Mode: Check if we are editing an existing task
        val existingTask = intent.getSerializableExtra("taskData") as? Task
        if (existingTask != null) {
            existingTaskId = existingTask.id
            etTitle.setText(existingTask.title)
            etDescription.setText(existingTask.description)
            etDate.setText(existingTask.date)
            etTime.setText(existingTask.time)
            btnCategorySelector.text = existingTask.category
            selectedCategory = existingTask.category
            switchImportant.isChecked = existingTask.isImportant
            btnSave.text = "Update Task ✨"
        }

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.formRootLayout).setOnClickListener { finish() }

        etDate.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText("$d/${m + 1}/$y")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                val ampm = if (h < 12) "AM" else "PM"
                val hour = if (h % 12 == 0) 12 else h % 12
                etTime.setText(String.format("%02d:%02d %s", hour, m, ampm))
            }, 12, 0, false).show()
        }

        btnCategorySelector.setOnClickListener { showCutesyMenu(it) }
        btnSave.setOnClickListener { validateAndSave() }
    }

    private fun showCutesyMenu(anchor: View) {
        val popup = ListPopupWindow(this)
        popup.setAdapter(SpinnerAdapter(this, categories))
        popup.anchorView = anchor
        popup.setBackgroundDrawable(getDrawable(R.drawable.bg_dropdown_menu))
        popup.setOnItemClickListener { _, _, pos, _ ->
            if (categories[pos] == "+ Add New") showAddNewCategoryDialog()
            else {
                selectedCategory = categories[pos]
                btnCategorySelector.text = selectedCategory
            }
            popup.dismiss()
        }
        popup.show()
    }

    private fun showAddNewCategoryDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_new_category)
        val et = dialog.findViewById<EditText>(R.id.edtNewCategoryName)
        dialog.findViewById<Button>(R.id.btnDialogAdd).setOnClickListener {
            if (et.text.isNotEmpty()) {
                categories.add(0, et.text.toString())
                selectedCategory = et.text.toString()
                btnCategorySelector.text = selectedCategory
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun validateAndSave() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) { etTitle.error = "Required!"; return }

        val task = Task(title, selectedCategory, etDate.text.toString(), etTime.text.toString(), etDescription.text.toString(), switchImportant.isChecked, existingTaskId)
        val intent = Intent().apply { putExtra("taskData", task) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}