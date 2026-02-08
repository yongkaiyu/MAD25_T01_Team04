package np.ict.mad.t01_team04

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContentEntity::class, CommentEntity::class, WatchedEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun commentDao(): CommentDao
    abstract fun watchedDao(): WatchedDao
}