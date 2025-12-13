package np.ict.mad.t01_team04

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.google.android.gms.common.util.CollectionUtils.mapOf
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Comment
import java.util.Date
import kotlin.collections.mapOf


@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,          // Firestore auto ID
    val userId: String,
    val userName: String,
    val movieId: String,
    val movieName: String,
    val comment: String,
    val timestamp: Long
)

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE movieId = :movieId ORDER BY timestamp DESC")
    fun getCommentsForMovie(movieId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)
}

class CommentViewModelFactory(
    private val repository: CommentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class CommentRepository(
    private val dao: CommentDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getAllComments() = dao.getAllComments()

    fun getCommentsForMovie(movieId: String) = dao.getCommentsForMovie(movieId)

    // ------------------- SYNC FROM FIRESTORE -------------------
    suspend fun sync() {
        val snapshot = firestore.collection("comment").get().await()

        val comments = snapshot.documents.map { doc ->
            CommentEntity(
                id = doc.id,
                userId = doc.getString("userId") ?: "",
                userName = doc.getString("userName") ?: "",
                movieId = doc.getString("movieId") ?: "",
                movieName = doc.getString("movieName") ?: "",
                comment = doc.getString("comment") ?: "",
                timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0
            )
        }

        dao.insertAll(comments)
    }

    // ------------------- WRITE TO FIRESTORE -------------------
    suspend fun addComment(comment: CommentEntity): Boolean {
        return try {
            val map = hashMapOf(
                "userId" to comment.userId,
                "userName" to comment.userName,
                "movieId" to comment.movieId,
                "movieName" to comment.movieName,
                "comment" to comment.comment,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("comment").add(map).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

class CommentViewModel(private val repo: CommentRepository) : ViewModel() {

    val comments = repo.getAllComments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun commentsForMovie(movieId: String) =
        repo.getCommentsForMovie(movieId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun submitComment(comment: CommentEntity) {
        viewModelScope.launch {
            repo.addComment(comment)
            repo.sync()       // refresh local cache
        }
    }

    init {
        viewModelScope.launch { repo.sync() }
    }
}
