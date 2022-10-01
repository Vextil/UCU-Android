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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val id = intent.getIntExtra("id", 0)
        val db = App.db(applicationContext)
        if (id != 0){

            val note = db.noteDao().getById(id)
            note.title?.let { Log.v("Note", it) }
            note.body?.let { Log.v("Note", it) }
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
                    note.title = note_title.text.toString()
                    note.body = note_body.text.toString()
                    note.editDate = System.currentTimeMillis()
                    db.noteDao().update(note)
                }else{
                    val note = Note(
                        title = note_title.text.toString(),
                        body = note_body.text.toString(),
                        type = NoteType.Note,
                        createDate = System.currentTimeMillis(),
                        editDate = System.currentTimeMillis()
                    )
                    db.noteDao().insertAll(note)
                }
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }
}