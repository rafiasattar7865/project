package com.example.campuslife

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslife.model.Event
import java.util.*

class EventFormActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnPickDate: EditText
    private lateinit var btnPickTime: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_form)

        etTitle = findViewById(R.id.edtEventTitle)
        etLocation = findViewById(R.id.edtEventLocation)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)
        btnSave = findViewById(R.id.btnSaveEvent)

        val existingEvent = intent.getSerializableExtra("eventData") as? Event
        existingEvent?.let {
            etTitle.setText(it.title)
            etLocation.setText(it.location)
            btnPickDate.setText(it.date)
            btnPickTime.setText(it.time)
        }

        btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val date = "$d/${m+1}/$y"
                btnPickDate.setText(date)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnPickTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                val time = String.format("%02d:%02d", h, m)
                btnPickTime.setText(time)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val date = btnPickDate.text.toString().trim()
            val time = btnPickTime.text.toString().trim()

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Title and Date are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(title, location, date, time)
            val resultIntent = Intent()
            resultIntent.putExtra("eventData", event)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}