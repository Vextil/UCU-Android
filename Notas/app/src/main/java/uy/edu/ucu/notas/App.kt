package uy.edu.ucu.notas

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class App: Application() {

    companion object {
        private var db: RoomDatabase? = null

        fun db(context: Context): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "notes-db"
                ).allowMainThreadQueries().build()
            }
            return db as AppDatabase
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}