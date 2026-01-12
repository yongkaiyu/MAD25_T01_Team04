package np.ict.mad.t01_team04

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ContentEntity - Model (Data Structure)
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

// Dao - Data Access Object - Model (Persistence logic)
@Dao
interface ContentDao {
    // Defined how data is accessed locally
    // Flow used to automatically update UI when data changes
    @Query("SELECT * FROM content ORDER BY createdAt DESC")
    fun getAllContent(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content WHERE id = :id LIMIT 1")
    fun getContentById(id: String): Flow<ContentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(content: List<ContentEntity>)
}

// ContentViewModelFactory - Creates ViewModel with parameters to store constructor arguments
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


// ContentRepository - Model of the MVC architecture (Domain + Data Orchestration)
class ContentRepository(
    private val dao: ContentDao,
    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getContent() = dao.getAllContent()
    fun getContentDetails(id: String) = dao.getContentById(id)

    // Sync logic between Firebase and Room
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

// ContentViewModel - Serves as controller in MVC architecture, holds UI-ready state, triggers data sync
class ContentViewModel(private val repo: ContentRepository): ViewModel() {
    // Converts Raw Flow into lifecycle-aware state, protects UI from data source changes
    val contentList = repo.getContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getDetails(id: String) = repo.getContentDetails(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    // Automatically fetches and caches data, keeps View component free of data-fetch logic
    init {
        viewModelScope.launch { repo.sync() } // fetch & cache
    }
}


