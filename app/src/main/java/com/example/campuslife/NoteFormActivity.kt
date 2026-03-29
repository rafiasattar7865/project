package com.example.campuslife

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslife.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteFormActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button
    private lateinit var btnAttach: ImageView
    private lateinit var btnAddLink: ImageView
    private lateinit var containerFiles: LinearLayout
    private lateinit var containerLinks: LinearLayout

    private val selectedUris = mutableListOf<String>()
    private val selectedNames = mutableListOf<String>()
    private val enteredLinks = mutableListOf<String>()
    private var existingNoteId: Long = 0

    private val PICK_FILE_RESULT_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_form)

        etTitle = findViewById(R.id.edtNoteTitle)
        etContent = findViewById(R.id.edtNoteContent)
        btnSave = findViewById(R.id.btnSaveNote)
        btnAttach = findViewById(R.id.btnAttachFile)
        btnAddLink = findViewById(R.id.btnAddLink)
        containerFiles = findViewById(R.id.containerFilePreviews)
        containerLinks = findViewById(R.id.containerLinkPreviews)

        val existingNote = intent.getSerializableExtra("noteData") as? Note
        existingNote?.let {
            existingNoteId = it.id
            etTitle.setText(it.title)
            etContent.setText(it.content)
            selectedUris.addAll(it.attachmentUris)
            selectedNames.addAll(it.attachmentNames)
            enteredLinks.addAll(it.links)
            refreshPreviews()
            btnSave.text = "Update Note ✨"
        }

        btnAttach.setOnClickListener {
            if (selectedUris.size < 3) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, PICK_FILE_RESULT_CODE)
            } else {
                Toast.makeText(this, "Max 3 files allowed!", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddLink.setOnClickListener {
            if (enteredLinks.size < 3) showLinkDialog()
            else Toast.makeText(this, "Max 3 links allowed!", Toast.LENGTH_SHORT).show()
        }

        btnSave.setOnClickListener { saveNote() }
    }

    private fun refreshPreviews() {
        containerFiles.removeAllViews()
        selectedUris.forEachIndexed { index, _ ->
            // Green for Files 🟢
            addPillToContainer(containerFiles, "📎 ${selectedNames[index]}", "#CDE990") {
                selectedUris.removeAt(index)
                selectedNames.removeAt(index)
                refreshPreviews()
            }
        }

        containerLinks.removeAllViews()
        enteredLinks.forEachIndexed { index, link ->
            // Pink for Links 🌸
            addPillToContainer(containerLinks, "🔗 $link", "#FFD4D4") {
                enteredLinks.removeAt(index)
                refreshPreviews()
            }
        }
    }

    private fun addPillToContainer(container: LinearLayout, text: String, colorHex: String, onRemove: () -> Unit) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_attachment_pill, container, false)
        val textView = view.findViewById<TextView>(R.id.txtPillText)
        textView.text = text

        // This makes the pill pink or green based on the input
        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))

        view.findViewById<ImageView>(R.id.btnRemovePill).setOnClickListener { onRemove() }
        container.addView(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                selectedUris.add(uri.toString())
                selectedNames.add(getFileName(uri))
                refreshPreviews()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "File"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name
    }

    private fun showLinkDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val input = dialogView.findViewById<EditText>(R.id.dialogInput)
        val btnAdd = dialogView.findViewById<TextView>(R.id.btnConfirm)

        input.visibility = View.VISIBLE
        btnAdd.setOnClickListener {
            val link = input.text.toString().trim()
            if (link.isNotEmpty()) {
                enteredLinks.add(link)
                refreshPreviews()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) return

        val note = Note(
            title, etContent.text.toString(),
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()),
            "#FFFAEC", selectedUris, selectedNames, enteredLinks, existingNoteId
        )

        setResult(Activity.RESULT_OK, Intent().putExtra("noteData", note))
        finish()
    }
}