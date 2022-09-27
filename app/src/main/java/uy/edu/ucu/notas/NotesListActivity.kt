package uy.edu.ucu.notas

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_notes_list.*
import kotlinx.android.synthetic.main.notes_list_item.view.*
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.random.Random


class NotesListActivity : AppCompatActivity() , NotesAdapter.onNoteItemClickListener {
    val fab by lazy { findViewById<FloatingActionButton>(R.id.fab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val db = App.db(applicationContext)
//        for (i in 1..100) {
//            val isList = Random.nextBoolean()
//            db.noteDao().insertAll(Note(
//                title = "Title $i",
//                body = if (isList) Json.encodeToString(arrayOf("a", "b", "c")) else "Content".repeat(i),
//                type = if (isList) NoteType.List else NoteType.Note,
//                createDate = System.currentTimeMillis(),
//                editDate = System.currentTimeMillis()
//            ))
//        }

            val notes = db.noteDao().getAll()
        val adapter = NotesAdapter(notes.toTypedArray(),this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        fab.setOnClickListener { view ->
            // TODO
            Snackbar.make(view, "Ir a crear nota", Snackbar.LENGTH_LONG)
                .show()
        }
        viewSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true ->  recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                false ->  recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }
        }


    }


    override  fun onItemClick(item : Note, position:Int){
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("id",item.id)

        startActivity(intent)
    }
}



class NotesAdapter(private val dataSet: Array<Note>, var clickListener: onNoteItemClickListener) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var title =  view.title
        var body = view.body

        fun initialize(item : Note,action:onNoteItemClickListener){
            title.text = item.title
            body.text = item.body
            itemView.setOnClickListener{
                action.onItemClick(item,adapterPosition)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notes_list_item, viewGroup, false)
        var viewHolder: ViewHolder = ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.notes_list_item,
            viewGroup, false))
        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.initialize(dataSet.get(position),clickListener)
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet[position].title
        viewHolder.body.text = dataSet[position].body
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size



    interface  onNoteItemClickListener{
        fun onItemClick(item: Note,position: Int)
    }
}
