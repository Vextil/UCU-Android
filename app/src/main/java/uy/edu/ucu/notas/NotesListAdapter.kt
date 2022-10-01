package uy.edu.ucu.notas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notes_list_item.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.DateFormat

class NotesAdapter(private var dataSet: Array<Note>, var clickListener: onNoteItemClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.title
        var body: TextView = view.body
        var listBody: LinearLayoutCompat = view.listBody
        var date: TextView = view.date

        fun initialize(item: Note, action: onNoteItemClickListener) {
            title.text = item.title
            body.text = item.title
            date.text = DateFormat.getDateInstance().format(item.lastModifiedDate)
            listBody.removeAllViews()
            if (item.type == NoteType.List) {
                body.text = null
                var list: List<NoteListItem> = listOf()
                try {
                    list = Json.decodeFromString(item.body ?: "[]")
                } catch (e: Exception) {
                    Log.e("NotesAdapter", "Error parsing list", e)
                }
                for (i in list) {
                    val view = CheckBox(listBody.context)
                    view.isClickable = false
                    view.text = i.value
                    view.isChecked = i.checked
                    listBody.addView(view)
                }
            } else {
                body.text = item.body
            }
            itemView.setOnClickListener {
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.notes_list_item,
                viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.initialize(dataSet[position], clickListener)
    }

    override fun getItemCount() = dataSet.size

    interface onNoteItemClickListener {
        fun onItemClick(item: Note, position: Int)
    }

    fun replaceData(newData: Array<Note>) {
        notifyItemRangeRemoved(0, dataSet.size);
        dataSet = newData
        notifyItemRangeInserted(0, dataSet.size);
    }
}
