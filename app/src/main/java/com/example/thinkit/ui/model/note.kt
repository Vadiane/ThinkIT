package com.example.thinkit.ui.model

import androidx.room.*import kotlinx.coroutines.flow.Flow

// 1. La Table (Entity)
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val couleur: Long // On stocke la couleur sous forme de nombre (Long)
)

// 2. Le DAO (C'est ce qui te manque !)
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: Note)

    @Delete
    suspend fun delete(note: Note)
}

// 3. La Base de données
@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}