package uy.edu.ucu.notas

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId LIMIT 1")
    fun getById(noteId: Int): Note

    @Insert
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()

    @Update
    fun update(note: Note)
}