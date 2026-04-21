package my.test_gramedia.database

import androidx.room.Database
import androidx.room.RoomDatabase
import my.test_gramedia.database.dao.ProductDao
import my.test_gramedia.database.entities.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}

