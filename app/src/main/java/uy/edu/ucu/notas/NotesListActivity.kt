package uy.edu.ucu.notas

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.random.Random


class NotesListActivity : AppCompatActivity() {

    val fab by lazy { findViewById<FloatingActionButton>(R.id.fab) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        val db = App.db(applicationContext)


//        db.noteDao().deleteAll()
//
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
        val adapter = NotesAdapter(notes.toTypedArray())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        fab.setOnClickListener { view ->
            // TODO
            Snackbar.make(view, "Ir a crear nota", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_delete) {

            return true
        }
        return super.onOptionsItemSelected(item)

    }
}

class NotesAdapter(private val dataSet: Array<Note>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val body: TextView

        init {
            // Define click listener for the ViewHolder's View.
            title = view.findViewById(R.id.title)
            body = view.findViewById(R.id.body)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notes_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet[position].title
        viewHolder.body.text = dataSet[position].body
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
