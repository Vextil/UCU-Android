package uy.edu.ucu.notas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_notes_list.*
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.random.Random


class NotesListActivity : AppCompatActivity(), NotesAdapter.onNoteItemClickListener {

    private val db by lazy { App.db(applicationContext) }
    private var showGrid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        val db = App.db(applicationContext)
        for (i in 1..20) {
            val isList = Random.nextBoolean()
            val list = mutableListOf<NoteListItem>()
            for (j in 1..i) {
                list.add(NoteListItem(value = "Item $j", checked = Random.nextBoolean()))
            }
            db.noteDao().insertAll(
                Note(
                    title = "Title $i",
                    body = if (isList) Json.encodeToString(list) else "Content".repeat(i),
                    type = if (isList) NoteType.List else NoteType.Note,
                    lastModifiedDate = System.currentTimeMillis(),
                )
            )
        }

        val notes = db.noteDao().getAll()
        val adapter = NotesAdapter(notes.toTypedArray(), this)

        recycler.adapter = adapter
        recycler.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("id", 0)
            intent.putExtra("isList", NoteType.Note == NoteType.List)

            startActivity(intent)
        }

        search_view.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val notes = db.noteDao().getByFilter(("%$query%") ?: "",("%\"value\":\"$query%") ?: "")
                (recycler.adapter as NotesAdapter).replaceData(notes.toTypedArray())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val notes = db.noteDao().getByFilter(("%$newText%") ?: "",("%\"value\":\"$newText%") ?: "")
                (recycler.adapter as NotesAdapter).replaceData(notes.toTypedArray())
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_show_list, menu)
        menu.findItem(R.id.show_list).isVisible = showGrid
        menu.findItem(R.id.show_grid).isVisible = !showGrid
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_list -> {
                showGrid = false
                invalidateOptionsMenu()
                recycler.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                true
            }
            R.id.show_grid -> {
                showGrid = true
                invalidateOptionsMenu()
                recycler.layoutManager =
                    StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                true
            }
            R.id.action_delete -> {
                lifecycleScope.launch {
                    onDeleteAll()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onItemClick(item: Note, position: Int) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("isList", item.type == NoteType.List)
        startActivity(intent)
    }

    private suspend fun onDeleteAll() {
        val count = db.noteDao().getCount()
        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(this)
            .setTitle("¿Borrar todo?")
            .setMessage("¿Estás seguro que querés borrar $count notas?")
            .setCancelable(false)
            .setPositiveButton(
                "Borrar", R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                onDeleteAllDoubleConfirm()
            }
            .setNegativeButton(
                "Cancelar", R.drawable.ic_baseline_close_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()

        mBottomSheetDialog.show()
    }

    private fun onDeleteAllDoubleConfirm() {
        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(this)
            .setTitle("¿100% seguro?")
            .setMessage("Estás a punto de borrar todas las notas. Esta acción no se puede deshacer.")
            .setCancelable(false)
            .setPositiveButton(
                "BORRAR!", R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                lifecycleScope.launch {
                    db.noteDao().deleteAll()
                    refresh()
                }
                Toast.makeText(applicationContext, "Borrado!", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                "Cancelar", R.drawable.ic_baseline_close_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()

        mBottomSheetDialog.show()
    }

    private fun refresh() {
        lifecycleScope.launch {
            val notes = db.noteDao().getAll()
            search_view.setQuery("", false)
            (recycler.adapter as NotesAdapter).replaceData(notes.toTypedArray())
        }
    }
}
