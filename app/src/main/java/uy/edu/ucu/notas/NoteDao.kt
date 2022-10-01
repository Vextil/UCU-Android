package uy.edu.ucu.notas

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM note order by lastModifiedDate desc")
    fun getAll(): List<Note>

    @Query("SELECT COUNT(1) FROM note")
    suspend fun getCount(): Int

    @Query("SELECT * FROM note WHERE id = :noteId LIMIT 1")
    fun getById(noteId: Int): Note

    @Insert
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note")
    suspend fun deleteAll()

    @Update
    fun update(note: Note)
}