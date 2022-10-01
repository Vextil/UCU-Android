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
                list.add(NoteListItem(value = "Elemento $j", checked = Random.nextBoolean()))
            }
            val randomColor = when (Random.nextInt(0, 5)) {
                1 -> R.color.note_orange
                2 -> R.color.note_blue
                3 -> R.color.note_green
                4 -> R.color.note_pink
                else -> R.color.note_yellow
            }
            db.noteDao().insertAll(
                Note(
                    title = "Título $i",
                    body = if (isList) Json.encodeToString(list) else "Contenido ".repeat(i),
                    type = if (isList) NoteType.List else NoteType.Note,
                    lastModifiedDate = System.currentTimeMillis(),
                    color = randomColor
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
            .setTitle(getString(R.string.delete_everything_question))
            .setMessage(getString(R.string.delete_everything_question_confirm, count))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.delete), R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                onDeleteAllDoubleConfirm()
            }
            .setNegativeButton(
                getString(R.string.cancel), R.drawable.ic_baseline_close_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()

        mBottomSheetDialog.show()
    }

    private fun onDeleteAllDoubleConfirm() {
        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(this)
            .setTitle(getString(R.string.delete_everything_100_sure))
            .setMessage(getString(R.string.delete_everything_100_sure_confirm))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.delete_confirm), R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                lifecycleScope.launch {
                    db.noteDao().deleteAll()
                    refresh()
                }
                Toast.makeText(applicationContext, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                getString(R.string.cancel), R.drawable.ic_baseline_close_24
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
