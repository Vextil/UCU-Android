package uy.edu.ucu.notas

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity
@Serializable
data class Note(
    var title: String?,
    var body: String?,
    val type: NoteType,
    var lastModifiedDate: Long,
    var color: Int = R.color.note_yellow
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

enum class NoteType {
    Note,
    List
}

@Serializable
data class NoteListItem (
    val value: String,
    val checked: Boolean
)