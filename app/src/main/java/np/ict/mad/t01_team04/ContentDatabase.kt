package np.ict.mad.t01_team04

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Entity(tableName = "content")
data class ContentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val thumbnailUrl: String,
    val tag: String,
    val createdAt: Long
)

@Dao
interface ContentDao {
    @Query("SELECT * FROM content ORDER BY createdAt DESC")
    fun getAllContent(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE id = :id LIMIT 1")
    fun getContentById(id: String): Flow<ContentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(content: List<ContentEntity>)
}

class ContentViewModelFactory(
    private val repository: ContentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            return ContentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


// Combines Firebase Helper + Room Helper + Repository logic
class ContentRepository(
    private val dao: ContentDao,
    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getContent() = dao.getAllContent()
    fun getContentDetails(id: String) = dao.getContentById(id)

    suspend fun sync() {
        try {
            val snapshot = firebase.collection("content").get().await()
            val content = snapshot.documents.map { doc ->
                ContentEntity(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    subtitle = doc.getString("subtitle") ?: "",
                    description = doc.getString("description") ?: "",
                    thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                    tag = doc.getString("tag") ?: "",
                    createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                )
            }
            dao.insertAll(content)
        } catch (e: Exception) {
            Log.e("ContentRepository", "Failed to sync content", e)
        }
    }
}

class ContentViewModel(private val repo: ContentRepository): ViewModel() {
    val contentList = repo.getContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getDetails(id: String) = repo.getContentDetails(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        viewModelScope.launch { repo.sync() } // fetch & cache
    }
}


