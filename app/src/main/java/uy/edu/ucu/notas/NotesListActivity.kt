package uy.edu.ucu.notas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_notes_list.*
import kotlinx.android.synthetic.main.notes_list_item.view.*
import kotlin.random.Random
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.text.DateFormat
import java.util.*

class NotesListActivity : AppCompatActivity(), NotesAdapter.onNoteItemClickListener {
    val fab: FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val db = App.db(applicationContext)
        for (i in 1..100) {
            val isList = Random.nextBoolean()
            val list = mutableListOf<NoteListItem>()
            for (j in 1..i) {
                list.add(NoteListItem(value = "Item $j", checked = Random.nextBoolean()))
            }
            db.noteDao().insertAll(Note(
                title = "Title $i",
                body = if (isList) Json.encodeToString(list) else "Content".repeat(i),
                type = if (isList) NoteType.List else NoteType.Note,
                createDate = System.currentTimeMillis(),
                editDate = System.currentTimeMillis()
            ))
        }

        val notes = db.noteDao().getAll()
        val adapter = NotesAdapter(notes.toTypedArray(), this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("id", 0)
            startActivity(intent)
        }
        viewSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true -> recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                false -> recyclerView.layoutManager =
                    StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val db = App.db(applicationContext)
        val notes = db.noteDao().getAll()
        val adapter = NotesAdapter(notes.toTypedArray(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
    }

    override fun onItemClick(item: Note, position: Int) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }
}


class NotesAdapter(private val dataSet: Array<Note>, var clickListener: onNoteItemClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.title
        var body: TextView = view.body
        var listBody: LinearLayoutCompat = view.listBody
        var date: TextView = view.date

        fun initialize(item: Note, action: onNoteItemClickListener) {
            title.text = item.title
            body.text = item.title
            date.text = DateFormat.getDateInstance().format(item.createDate)
            listBody.removeAllViews()
            if (item.type == NoteType.List) {
                body.text = null
                val list = Json.decodeFromString<List<NoteListItem>>(item.body ?: "[]")
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
        viewHolder.initialize(dataSet.get(position), clickListener)
    }

    override fun getItemCount() = dataSet.size

    interface onNoteItemClickListener {
        fun onItemClick(item: Note, position: Int)
    }
}
