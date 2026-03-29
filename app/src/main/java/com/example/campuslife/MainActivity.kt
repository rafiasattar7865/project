package com.example.campuslife

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslife.adapters.TaskAdapter
import com.example.campuslife.adapters.NoteAdapter
import com.example.campuslife.adapters.EventAdapter
import com.example.campuslife.database.AppDatabase
import com.example.campuslife.model.Task
import com.example.campuslife.model.Note
import com.example.campuslife.model.Event
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity(),
    TaskAdapter.OnTaskClickListener,
    NoteAdapter.OnNoteClickListener,
    EventAdapter.OnEventClickListener {

    private lateinit var btnAdd: ImageView
    private lateinit var btnTaskWrapper: ConstraintLayout
    private lateinit var btnNoteWrapper: ConstraintLayout
    private lateinit var btnEventWrapper: ConstraintLayout
    private lateinit var btnTaskMini: ImageView
    private lateinit var btnNoteMini: ImageView
    private lateinit var btnEventMini: ImageView
    private lateinit var txtEmptyPlaceholder: TextView

    private var isExpanded = false

    private lateinit var rvMainToday: RecyclerView
    private lateinit var rvTasksTab: RecyclerView
    private lateinit var rvNotesTab: RecyclerView
    private lateinit var rvEventsTab: RecyclerView

    private lateinit var tabTasks: Button
    private lateinit var tabNotes: Button
    private lateinit var tabEvents: Button

    private lateinit var todayAdapter: TaskAdapter
    private lateinit var taskTabAdapter: TaskAdapter
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var eventAdapter: EventAdapter

    private val REQ_ADD_TASK = 2001
    private val REQ_EDIT_TASK = 2002
    private val REQ_NOTE = 3001
    private val REQ_EVENT = 4001

    private var editingPosition = -1

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init UI
        tabTasks = findViewById(R.id.tabTasks)
        tabNotes = findViewById(R.id.tabNotes)
        tabEvents = findViewById(R.id.tabEvents)
        txtEmptyPlaceholder = findViewById(R.id.txtEmptyPlaceholder)

        // Init FAB
        btnAdd = findViewById(R.id.btnAdd)
        btnTaskWrapper = findViewById(R.id.btnTaskWrapper)
        btnNoteWrapper = findViewById(R.id.btnNoteWrapper)
        btnEventWrapper = findViewById(R.id.btnEventWrapper)
        btnTaskMini = findViewById(R.id.btnAddTaskMini)
        btnNoteMini = findViewById(R.id.btnAddNoteMini)
        btnEventMini = findViewById(R.id.btnAddEventMini)

        // Init RecyclerViews
        rvMainToday = findViewById(R.id.recyclerViewMain)
        rvTasksTab = findViewById(R.id.recyclerViewTasksTab)
        rvNotesTab = findViewById(R.id.recyclerViewNotesTab)
        rvEventsTab = findViewById(R.id.recyclerViewEventsTab)

        rvMainToday.layoutManager = LinearLayoutManager(this)
        rvTasksTab.layoutManager = LinearLayoutManager(this)
        rvNotesTab.layoutManager = LinearLayoutManager(this)
        rvEventsTab.layoutManager = LinearLayoutManager(this)

        // Init Adapters
        todayAdapter = TaskAdapter(mutableListOf(), this)
        taskTabAdapter = TaskAdapter(mutableListOf(), this)
        notesAdapter = NoteAdapter(mutableListOf(), this)
        eventAdapter = EventAdapter(mutableListOf(), this)

        rvMainToday.adapter = todayAdapter
        rvTasksTab.adapter = taskTabAdapter
        rvNotesTab.adapter = notesAdapter
        rvEventsTab.adapter = eventAdapter

        db = AppDatabase.getInstance(this)
        loadDataFromDb()

        setupListeners()
        updatePlaceholderVisibility() // Initial check
    }

    private fun loadDataFromDb() {
        lifecycleScope.launch {
            db.taskDao().getAll().forEach { taskTabAdapter.addTask(it) }
            db.noteDao().getAll().forEach { notesAdapter.addNote(it) }
            db.eventDao().getAll().forEach { eventAdapter.addEvent(it) }
            updatePlaceholderVisibility()
        }
    }

    private fun setupListeners() {
        tabTasks.setOnClickListener { showTab(rvTasksTab) }
        tabNotes.setOnClickListener { showTab(rvNotesTab) }
        tabEvents.setOnClickListener { showTab(rvEventsTab) }

        btnAdd.setOnClickListener { if (!isExpanded) expandMenu() else collapseMenu() }

        btnTaskMini.setOnClickListener {
            collapseMenu()
            editingPosition = -1
            startActivityForResult(Intent(this, TaskFormActivity::class.java), REQ_ADD_TASK)
        }

        btnNoteMini.setOnClickListener {
            collapseMenu()
            editingPosition = -1
            startActivityForResult(Intent(this, NoteFormActivity::class.java), REQ_NOTE)
        }

        btnEventMini.setOnClickListener {
            collapseMenu()
            editingPosition = -1
            startActivityForResult(Intent(this, EventFormActivity::class.java), REQ_EVENT)
        }
    }

    // --- PLACEHOLDER LOGIC ---
    private fun updatePlaceholderVisibility() {
        val isEmpty = when {
            rvTasksTab.visibility == View.VISIBLE -> taskTabAdapter.itemCount == 0
            rvNotesTab.visibility == View.VISIBLE -> notesAdapter.itemCount == 0
            rvEventsTab.visibility == View.VISIBLE -> eventAdapter.itemCount == 0
            else -> todayAdapter.itemCount == 0
        }
        txtEmptyPlaceholder.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    // --- INTERFACE CLICKS ---
    override fun onTaskClick(task: Task, position: Int) {
        editingPosition = position
        val intent = Intent(this, TaskFormActivity::class.java).apply { putExtra("taskData", task) }
        startActivityForResult(intent, REQ_EDIT_TASK)
    }

    override fun onTaskLongClick(task: Task, position: Int) {
        AlertDialog.Builder(this).setTitle("Delete Task?").setPositiveButton("Yes") { _, _ ->
            taskTabAdapter.removeTask(position)
            lifecycleScope.launch { db.taskDao().delete(task) }
            updatePlaceholderVisibility()
        }.show()
    }

    override fun onNoteClick(note: Note, position: Int) {
        editingPosition = position
        val intent = Intent(this, NoteFormActivity::class.java).apply { putExtra("noteData", note) }
        startActivityForResult(intent, REQ_NOTE)
    }

    override fun onNoteLongClick(note: Note, position: Int) {
        AlertDialog.Builder(this).setTitle("Delete Note?").setPositiveButton("Yes") { _, _ ->
            notesAdapter.removeNote(position)
            lifecycleScope.launch { db.noteDao().delete(note) }
            updatePlaceholderVisibility()
        }.show()
    }

    override fun onEventClick(event: Event, position: Int) {
        editingPosition = position
        val intent = Intent(this, EventFormActivity::class.java).apply { putExtra("eventData", event) }
        startActivityForResult(intent, REQ_EVENT)
    }

    override fun onEventLongClick(event: Event, position: Int) {
        AlertDialog.Builder(this).setTitle("Delete Event?").setPositiveButton("Yes") { _, _ ->
            eventAdapter.removeEvent(position)
            lifecycleScope.launch { db.eventDao().delete(event) }
            updatePlaceholderVisibility()
        }.show()
    }

    // --- NAVIGATION LOGIC ---
    private fun showTab(viewToShow: RecyclerView) {
        rvMainToday.visibility = View.GONE
        rvTasksTab.visibility = View.GONE
        rvNotesTab.visibility = View.GONE
        rvEventsTab.visibility = View.GONE
        viewToShow.visibility = View.VISIBLE
        updatePlaceholderVisibility() // Refresh placeholder when switching tabs
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            editingPosition = -1
            return
        }

        when (requestCode) {
            REQ_ADD_TASK, REQ_EDIT_TASK -> {
                val task = data?.getSerializableExtra("taskData") as? Task
                task?.let {
                    if (requestCode == REQ_ADD_TASK) {
                        lifecycleScope.launch {
                            val newId = db.taskDao().insert(it)
                            taskTabAdapter.addTask(it.copy(id = newId))
                        }
                    } else {
                        if (editingPosition != -1) {
                            lifecycleScope.launch { db.taskDao().update(it) }
                            taskTabAdapter.updateTask(it, editingPosition)
                        }
                    }
                    showTab(rvTasksTab)
                }
            }

            REQ_NOTE -> {
                val note = data?.getSerializableExtra("noteData") as? Note
                note?.let {
                    if (editingPosition != -1) {
                        lifecycleScope.launch { db.noteDao().update(it) }
                        notesAdapter.updateNote(it, editingPosition)
                    } else {
                        lifecycleScope.launch {
                            val newId = db.noteDao().insert(it)
                            notesAdapter.addNote(it.copy(id = newId))
                        }
                    }
                    showTab(rvNotesTab)
                }
            }

            REQ_EVENT -> {
                val event = data?.getSerializableExtra("eventData") as? Event
                event?.let {
                    if (editingPosition != -1) {
                        lifecycleScope.launch { db.eventDao().update(it) }
                        eventAdapter.updateEvent(it, editingPosition)
                    } else {
                        lifecycleScope.launch {
                            val newId = db.eventDao().insert(it)
                            eventAdapter.addEvent(it.copy(id = newId))
                        }
                    }
                    showTab(rvEventsTab)
                }
            }
        }
        editingPosition = -1
        updatePlaceholderVisibility() // Refresh after adding/editing
    }

    private fun expandMenu() {
        isExpanded = true
        val radius = 280f
        val wrappers = listOf(btnTaskWrapper, btnNoteWrapper, btnEventWrapper)
        val angles = listOf(90f, 135f, 180f)
        btnAdd.animate().rotation(45f).setDuration(300).start()
        wrappers.forEachIndexed { idx, wrapper ->
            wrapper.visibility = View.VISIBLE
            wrapper.alpha = 0f
            val rad = Math.toRadians(angles[idx].toDouble())
            wrapper.animate()
                .translationX(radius * cos(rad).toFloat())
                .translationY(-radius * sin(rad).toFloat())
                .alpha(1f).setDuration(300L).setInterpolator(OvershootInterpolator()).start()
        }
    }

    private fun collapseMenu() {
        isExpanded = false
        val wrappers = listOf(btnTaskWrapper, btnNoteWrapper, btnEventWrapper)
        btnAdd.animate().rotation(0f).setDuration(300).start()
        wrappers.forEach { wrapper ->
            wrapper.animate().translationX(0f).translationY(0f).alpha(0f).setDuration(250L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { wrapper.visibility = View.INVISIBLE }.start()
        }
    }
}