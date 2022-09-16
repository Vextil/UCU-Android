package uy.edu.ucu.notas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = App.db(applicationContext)
        val notes = db.noteDao().getAll()

//        val note = Note(title = "test 1", body = "body 1", type = NoteType.Note, createDate = System.currentTimeMillis(), editDate = System.currentTimeMillis())
//        db.noteDao().insertAll(note)
//        Log.e("NOTE", Json.encodeToString(db.noteDao().getAll()))


        setContentView(R.layout.activity_main)
    }
}