package uy.edu.ucu.notas

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.note_detail_list_item.view.*
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class CreateNoteActivity : AppCompatActivity() {
    private var initial_title = ""
    private var initial_body = ""
    private val db by lazy { App.db(applicationContext) }
    private val isList: Boolean by lazy { intent.getBooleanExtra("isList", false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val id = intent.getIntExtra("id", 0)


        if (id != 0){

            val note = db.noteDao().getById(id)
            note.title?.let { Log.v("Note", it) }
            note.body?.let { Log.v("Note", it) }
            initial_title = note.title.toString()
            initial_body = note.body.toString()

            note_title.setText(note.title)
            if (note.type == NoteType.Note) {
                list_scroll.visibility = View.GONE
                note_body.setText(note.body)
            } else {
                note_body.visibility = View.GONE
                val list = Json.decodeFromString<List<NoteListItem>>(note.body!!)
                for (item in list) {
                    addListItem(item.checked, item.value)
                }
            }

        }else{
            if (isList) {
                note_body.visibility = View.GONE
            } else {
                list_scroll.visibility = View.GONE

            }
        }
        add_item_to_list_button.setOnClickListener {
            addListItem()
        }

    }

    private fun addListItem(checked: Boolean = false, value: String = "") {
        val view = layoutInflater.inflate(R.layout.note_detail_list_item, null)
        view.checkbox.isChecked = checked
        view.text.setText(value)
        view.delete.setOnClickListener {
            note_list_contanier.removeView(view)
        }
        note_list_contanier.addView(view)
        list_scroll.postDelayed({
            list_scroll.fullScroll(View.FOCUS_DOWN)
        }, 300)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val id = intent.getIntExtra("id", 0)
                val new_title = note_title.text.toString()
                val new_body = if (!isList) {
                    note_body.text.toString()
                } else {
                    val list = mutableListOf<NoteListItem>()
                    for (i in 0 until note_list_contanier.childCount) {
                        val view = note_list_contanier.getChildAt(i)
                        list.add(
                            NoteListItem(
                                view.text.text.toString(),
                                view.checkbox.isChecked
                            )
                        )
                    }
                    Json.encodeToString(list)
                }
                exitCreation(new_title,new_body, if (id != 0) db.noteDao().getById(id) else null)
                finish()
                return true
            }

            R.id.action_delete -> {
                lifecycleScope.launch {
                    val id = intent.getIntExtra("id", 0)
                    if (id != 0) {
                        val note = db.noteDao().getById(id)
                        onDeleteNote(note)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_show_list, menu)
        menu.findItem(R.id.show_list).isVisible = false
        menu.findItem(R.id.show_grid).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = intent.getIntExtra("id", 0) != 0
        return true
    }

    private fun onDeleteNote(note: Note) {
        val mBottomSheetDialog = BottomSheetMaterialDialog.Builder(this)
            .setTitle("¿Borrar nota?")
            .setMessage("¿Estás seguro que querés borrar esta nota?")
            .setCancelable(false)
            .setPositiveButton(
                "Borrar", R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                db.noteDao().delete(note)
                Toast.makeText(applicationContext, "Borrado!", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton(
                "Cancelar", R.drawable.ic_baseline_close_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()

        mBottomSheetDialog.show()
    }

    private fun exitCreation(new_title: String, new_body: String, note: Note?) {
        if(note != null){
            if ((isList && new_title.isBlank() && new_body == "[]") ||
                (!isList && new_title.isBlank() && new_body.isBlank())) {
                db.noteDao().delete(note)
            } else if (new_title.trim() != initial_title.trim() || new_body.trim() != initial_body.trim()) {
                note.title = new_title
                note.body = new_body
                note.lastModifiedDate = System.currentTimeMillis()
                db.noteDao().update(note)
            }else{
                return
            }
        }else{
            if (!((isList && new_title.isBlank() && new_body == "[]") ||
                        (!isList && new_title.isBlank() && new_body.isBlank()))){
                    val note = Note(
                    title = note_title.text.toString(),
                    body = note_body.text.toString(),
                    type = NoteType.Note,
                    lastModifiedDate = System.currentTimeMillis()
                )
                db.noteDao().insertAll(note)
            }
        }
    }
}