package com.example.campuslife.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslife.R
import com.example.campuslife.model.Note

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnNoteClickListener {
        fun onNoteClick(note: Note, position: Int)
        fun onNoteLongClick(note: Note, position: Int)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtNoteTitle)
        val txtContent: TextView = itemView.findViewById(R.id.txtNoteContent)
        val txtDate: TextView = itemView.findViewById(R.id.txtNoteDate)
        val layoutAttachment: LinearLayout = itemView.findViewById(R.id.layoutCardAttachment)
        val layoutLink: LinearLayout = itemView.findViewById(R.id.layoutCardLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        val context = holder.itemView.context

        holder.txtTitle.text = note.title
        holder.txtContent.text = note.content
        holder.txtDate.text = note.date

        holder.layoutAttachment.removeAllViews()
        holder.layoutLink.removeAllViews()


        if (note.attachmentUris.isNotEmpty()) {
            holder.layoutAttachment.visibility = View.VISIBLE
            note.attachmentUris.forEachIndexed { index, uriString ->
                val fileName = note.attachmentNames.getOrNull(index) ?: "File"
                val filePill = createMiniPill(context, "📎 $fileName", "#CDE990")

                filePill.setOnClickListener { openFile(context, uriString) }
                holder.layoutAttachment.addView(filePill)
            }
        } else {
            holder.layoutAttachment.visibility = View.GONE
        }


        if (note.links.isNotEmpty()) {
            holder.layoutLink.visibility = View.VISIBLE
            note.links.forEach { linkUrl ->
                val linkPill = createMiniPill(context, "🔗 $linkUrl", "#FFD4D4")

                linkPill.setOnClickListener { openWebLink(context, linkUrl) }
                holder.layoutLink.addView(linkPill)
            }
        } else {
            holder.layoutLink.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { listener.onNoteClick(note, position) }
        holder.itemView.setOnLongClickListener {
            listener.onNoteLongClick(note, position)
            true
        }
    }

    private fun createMiniPill(context: Context, text: String, colorHex: String): TextView {
        val tv = TextView(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        tv.layoutParams = params
        tv.text = text
        tv.textSize = 11f
        tv.maxLines = 1
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.setPadding(24, 12, 24, 12)
        tv.background = context.getDrawable(R.drawable.edit_text_pill)
        tv.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))
        tv.setTextColor(Color.parseColor("#333333"))
        return tv
    }

    private fun openFile(context: Context, uriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriString)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open file ❌", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWebLink(context: Context, url: String) {
        try {
            var finalUrl = url
            if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                finalUrl = "https://$finalUrl"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Invalid Link ❌", Toast.LENGTH_SHORT).show()
        }
    }

    fun addNote(note: Note) { notes.add(0, note); notifyItemInserted(0) }
    fun updateNote(note: Note, position: Int) { notes[position] = note; notifyItemChanged(position) }
    fun removeNote(position: Int) { notes.removeAt(position); notifyItemRemoved(position) }
    fun getNote(position: Int): Note = notes[position]
    fun setNotes(newNotes: MutableList<Note>) { notes.clear(); notes.addAll(newNotes); notifyDataSetChanged() }
}