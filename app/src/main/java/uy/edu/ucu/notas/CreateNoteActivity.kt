package uy.edu.ucu.notas

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_note.*

class CreateNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val id = intent.getIntExtra("id",0)

        val db = App.db(applicationContext)
        val note = db.noteDao().getById(id)
        note.title?.let { Log.v("Note", it) }
        note.body?.let { Log.v("Note", it) }
        note_title.setText(note.title)
        val erase: ImageView = this.findViewById(R.id.note_erase)
        erase.setOnClickListener {
            Toast.makeText(this, "Eliminada", Toast.LENGTH_LONG).show()
            db.noteDao().delete(note)
            // retornar a la pagina ppal

        }

    }
}