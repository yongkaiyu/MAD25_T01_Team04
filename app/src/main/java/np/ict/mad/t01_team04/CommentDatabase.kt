package np.ict.mad.t01_team04

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// CommentEntity - Model (Data Structure)
@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,          // Firestore auto ID
    val userId: String,
    val userName: String,
    val movieId: String,
    val movieName: String,
    val comment: String,
    val rating: Int,
    val timestamp: Long
)

// Dao - Data Access Object - Model (Persistence logic)
@Dao
interface CommentDao {
    // Defined how data is accessed locally
    // Flow used to automatically update UI when data changes
    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE movieId = :movieId ORDER BY timestamp DESC")
    fun getCommentsForMovie(movieId: String): Flow<List<CommentEntity>>

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteById(commentId: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Update
    suspend fun updateComment(comment: CommentEntity)

}

// CommentViewModelFactory - Creates ViewModel with parameters to store constructor arguments
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

// CommentRepository - Model of the MVC architecture (Domain + Data Orchestration)
class CommentRepository(
    private val dao: CommentDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getAllComments() = dao.getAllComments()

    fun getCommentsForMovie(movieId: String) = dao.getCommentsForMovie(movieId)

    suspend fun deleteCommentAndSync(commentId: String) {
        deleteComment(commentId)
        dao.deleteById(commentId)
    }

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
                rating = doc.getLong("rating")?.toInt() ?: 0,
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
                "rating" to comment.rating,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("comment").add(map).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateComment(comment: CommentEntity): Boolean {
        return try {
            val map = hashMapOf(
                "userId" to comment.userId,
                "userName" to comment.userName,
                "movieId" to comment.movieId,
                "movieName" to comment.movieName,
                "comment" to comment.comment,
                "rating" to comment.rating,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("comment")
                .document(comment.id) // use the existing comment ID
                .set(map, SetOptions.merge()) // merge ensures only updated fields are replaced
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return try {
            firestore.collection("comment")
                .document(commentId)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}

// CommentViewModel - Serves as controller in MVC architecture, holds UI-ready state, triggers data sync
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

    fun updateComment(comment: CommentEntity) {
        viewModelScope.launch {
            repo.updateComment(comment)
            repo.sync()
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repo.deleteCommentAndSync(commentId)
        }
    }

    fun sync() {
        viewModelScope.launch { repo.sync() }
    }

    // Automatically fetches and caches data, keeps View component free of data-fetch logic
    init {
        viewModelScope.launch { repo.sync() }
    }
}
