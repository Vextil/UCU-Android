package uy.edu.ucu.notas

import android.os.Bundle
import android.util.Log
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

        val id = intent.getIntExtra("id", 0)

        val db = App.db(applicationContext)
        val note = db.noteDao().getById(id)
        note.title?.let { Log.v("Note", it) }
        note.body?.let { Log.v("Note", it) }
        note_title.setText(note.title)
        if (note.type == NoteType.Note) {
            note_body.setText(note.body)
        } else {
            (note_body.parent as ViewGroup).removeView(note_body)
            val list = Json.decodeFromString<List<NoteListItem>>(note.body!!)
            for (item in list) {
                val view = layoutInflater.inflate(R.layout.note_detail_list_item, null)
                view.checkbox.isChecked = item.checked
                view.text.setText(item.value)
                note_list_contanier.addView(view)
            }
        }
        val erase: ImageView = this.findViewById(R.id.note_erase)
        erase.setOnClickListener {
            Toast.makeText(this, "Eliminada", Toast.LENGTH_LONG).show()
            db.noteDao().delete(note)
            // retornar a la pagina ppal

        }

    }
}