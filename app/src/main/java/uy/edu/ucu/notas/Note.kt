package uy.edu.ucu.notas

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val createDate: Long,
    var editDate: Long?
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