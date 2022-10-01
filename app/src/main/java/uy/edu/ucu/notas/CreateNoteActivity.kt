package uy.edu.ucu.notas

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.note_detail_list_item.view.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class CreateNoteActivity : AppCompatActivity() {
    var initial_title = ""
    var initial_body = ""
    val isList: Boolean by lazy { intent.getBooleanExtra("isList", false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val id = intent.getIntExtra("id", 0)
        val isList = intent.getBooleanExtra("isList", false)
        val db = App.db(applicationContext)
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
            val erase: ImageView = this.findViewById(R.id.note_erase)
            erase.setOnClickListener {
                Toast.makeText(this, "Eliminada", Toast.LENGTH_LONG).show()
                db.noteDao().delete(note)
                finish()
                // retornar a la pagina ppal

            }
        }else{
            val erase: ImageView = this.findViewById(R.id.note_erase)
            erase.visibility = View.GONE
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
                val db = App.db(applicationContext)
                val id = intent.getIntExtra("id", 0)
                if (id != 0) {
                    val note = db.noteDao().getById(id)
                    val new_title = note_title.text.toString()
                    val new_body = if (note.type == NoteType.Note) {
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
                    Log.v("NoteTitle", new_title)
                    Log.v("NoteBody", new_body)
                    Log.v("NoteType", isList.toString())
                    if (initial_title == new_title && initial_body == new_body) {
                        finish()
                        return true

                    } else if ((isList && new_title == "" && new_body == "[]") || (!isList && new_title == "" && new_body == "")) {
                        db.noteDao().delete(note)
                        finish()
                        return true
                    } else {
                        note.title = new_title
                        note.body = new_body
                        note.lastModifiedDate = System.currentTimeMillis()
                        db.noteDao().update(note)
                        finish()
                        return true
                    }

                } else {

                    if ((isList && note_title.text.toString() == "" && note_body.text.toString() == "[]") || (!isList && note_title.text.toString() == "" && note_body.text.toString() == "")) {
                        val note = Note(
                            title = note_title.text.toString(),
                            body = note_body.text.toString(),
                            type = NoteType.Note,
                            lastModifiedDate = System.currentTimeMillis()
                        )
                        db.noteDao().insertAll(note)
                    }
                    finish()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}