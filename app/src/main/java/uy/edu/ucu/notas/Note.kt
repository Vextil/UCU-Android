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
    val title: String?,
    val body: String?,
    val type: NoteType,
    val createDate: Long,
    val editDate: Long?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

enum class NoteType {
    Note,
    List
}