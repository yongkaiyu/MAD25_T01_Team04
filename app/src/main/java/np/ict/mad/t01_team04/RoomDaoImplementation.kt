package np.ict.mad.t01_team04

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContentEntity::class, CommentEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun commentDao(): CommentDao
}