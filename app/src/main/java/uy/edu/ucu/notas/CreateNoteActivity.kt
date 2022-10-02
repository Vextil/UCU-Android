package uy.edu.ucu.notas

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.note_detail_list_item.view.*
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json


class CreateNoteActivity : AppCompatActivity() {

    private var initialTitle = ""
    private var initialBody = ""
    private var initialType = NoteType.Note
    private var initialColor = R.color.note_yellow
    private var currentColor = R.color.note_yellow
    private val db by lazy { App.db(applicationContext) }
    private var isList = false

    private val colors = intArrayOf(
        R.color.note_yellow,
        R.color.note_orange,
        R.color.note_blue,
        R.color.note_green,
        R.color.note_pink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        createColorChooser()
        setupToolbarButtons();
        val id = intent.getIntExtra("id", 0)

        if (id != 0) {
            val note = db.noteDao().getById(id)
            initialTitle = note.title.toString()
            initialBody = note.body.toString()
            initialType = note.type
            initialColor = note.color
            currentColor = note.color
            isList = note.type == NoteType.List
            main_container.setBackgroundColor(ContextCompat.getColor(this, note.color))
            note_title.setText(note.title)
            if (note.type == NoteType.Note) {
                note_body.setText(note.body)
            } else {

                val list = Json.decodeFromString<List<NoteListItem>>(note.body!!)
                for (item in list) {
                    addListItem(item.checked, item.value)
                }
            }
        }
        refreshNoteType()
        add_item_to_list_button.setOnClickListener {
            addListItem(scroll = true)
        }
    }

    private fun refreshNoteType() {
        if (isList) {
            note_body.visibility = View.GONE
            list_scroll.visibility = View.VISIBLE
            list_button.visibility = View.GONE
            note_button.visibility = View.VISIBLE
        } else {
            note_body.visibility = View.VISIBLE
            list_scroll.visibility = View.GONE
            list_button.visibility = View.VISIBLE
            note_button.visibility = View.GONE
        }
    }

    private fun addListItem(checked: Boolean = false, value: String = "", scroll: Boolean = false) {
        val view = layoutInflater.inflate(R.layout.note_detail_list_item, null)
        view.checkbox.isChecked = checked
        view.text.setText(value)
        view.delete.setOnClickListener {
            note_list_contanier.removeView(view)
        }
        note_list_contanier.addView(view)
        if (scroll) {
            list_scroll.postDelayed({
                list_scroll.fullScroll(View.FOCUS_DOWN)
            }, 100)
        }
    }

    private fun setupToolbarButtons() {
        back_button.setOnClickListener {
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
            exitCreation(new_title, new_body, if (id != 0) db.noteDao().getById(id) else null)
            finish()
        }

        list_button.setOnClickListener {
            isList = true
            refreshNoteType()
        }

        note_button.setOnClickListener {
            isList = false
            refreshNoteType()
        }

        delete_button.setOnClickListener {
            lifecycleScope.launch {
                val id = intent.getIntExtra("id", 0)
                if (id != 0) {
                    val note = db.noteDao().getById(id)
                    onDeleteNote(note)
                }
            }
        }
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
            .setTitle(getString(R.string.delete_note_question))
            .setMessage(getString(R.string.delete_note_question_confirm))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.delete), R.drawable.ic_delete_24
            ) { dialogInterface, _ ->
                db.noteDao().delete(note)
                Toast.makeText(applicationContext, getString(R.string.deleted), Toast.LENGTH_SHORT)
                    .show()
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton(
                getString(R.string.cancel), R.drawable.ic_baseline_close_24
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()

        mBottomSheetDialog.show()
    }

    private fun exitCreation(new_title: String, new_body: String, note: Note?) {
        if (note != null) {
            if ((isList && new_title.isBlank() && listBodyIsBlank(new_body)) ||
                (!isList && new_title.isBlank() && new_body.isBlank())
            ) {
                db.noteDao().delete(note)
            } else if (new_title.trim() != initialTitle.trim()
                || new_body.trim() != initialBody.trim()
                || isList != (initialType == NoteType.List)
                || currentColor != initialColor
            ) {

                note.title = new_title
                note.body = new_body
                note.color = currentColor
                note.type = if (isList) NoteType.List else NoteType.Note
                note.lastModifiedDate = System.currentTimeMillis()
                db.noteDao().update(note)
            }
        } else {
            if (!((isList && new_title.isBlank() && listBodyIsBlank(new_body)) ||
                        (!isList && new_title.isBlank() && new_body.isBlank()))
            ) {
                val note = Note(
                    title = note_title.text.toString(),
                    body = note_body.text.toString(),
                    type = if (isList) NoteType.List else NoteType.Note,
                    lastModifiedDate = System.currentTimeMillis(),
                    color = currentColor
                )
                db.noteDao().insertAll(note)
            }
        }
    }

    private fun listBodyIsBlank(body: String): Boolean {
        Log.v("notebody", body)
        val list = Json.decodeFromString<List<NoteListItem>>(body)
        for (item in list) {
            if (item.value.isNotBlank()) {
                return false
            }
        }
        return true
    }

    private fun createColorChooser() {
        val size =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24f,
                resources.displayMetrics
            ).toInt()
        val layoutParams = LinearLayout.LayoutParams(size, size)
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            12f,
            resources.displayMetrics
        ).toInt()
        layoutParams.setMargins(0, 0, margin, 0)
        var view: View
        for (color in colors) {
            view = View(this)
            view.background = ContextCompat.getDrawable(applicationContext, R.drawable.circle)
            view.background.setTint(ContextCompat.getColor(applicationContext, color))
            view.layoutParams = layoutParams
            view.id = View.generateViewId()
            view.setOnClickListener {
                currentColor = color
                main_container.setBackgroundColor(ContextCompat.getColor(applicationContext, color))
            }
            colors_container.addView(view)
        }
    }
}