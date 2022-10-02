package uy.edu.ucu.notas

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notes_list_item.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.DateFormat

class NotesAdapter(private var dataSet: Array<Note>, var clickListener: onNoteItemClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun initialize(item: Note, action: onNoteItemClickListener) {
            itemView.title.visibility = View.GONE
            itemView.title.text = item.title
            itemView.body.text = null
            itemView.date.text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(item.lastModifiedDate)
            itemView.container.background.setTint(
                ContextCompat.getColor(
                    itemView.context,
                    item.color
                )
            )
            if (item.title == null || item.title!!.isEmpty()) {
                itemView.title.visibility = View.GONE
            } else {
                itemView.title.visibility = View.VISIBLE
            }
            itemView.listBody.removeAllViews()
            if (item.type == NoteType.List) {
                var list: List<NoteListItem> = listOf()
                try {
                    list = Json.decodeFromString(item.body ?: "[]")
                } catch (e: Exception) {
                    Log.e("NotesAdapter", "Error parsing list", e)
                }
                for (i in list) {
                    val view = AppCompatCheckBox(itemView.listBody.context)
                    view.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.checkbox_transparent
                        )
                    )
                    view.background = null
                    view.isClickable = false
                    view.isFocusable = false
                    view.minimumHeight = 0
                    view.minimumWidth = 0
                    view.text = i.value
                    view.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_transparent
                        )
                    )
                    view.isChecked = i.checked
                    itemView.listBody.addView(view)
                }
            } else {
                itemView.body.text = item.body
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
