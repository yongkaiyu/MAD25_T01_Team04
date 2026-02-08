package np.ict.mad.t01_team04

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

// Daily Mystery Movie Screen UI
@Composable
fun DailyMysteryScreen(
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel
) {
    var selectedMysteryMovie by remember { mutableStateOf<ContentEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val allMovies by viewModel.contentList.collectAsState()

    // Load the daily mystery movie on composition
    LaunchedEffect(allMovies) {
        if (allMovies.isNotEmpty()) {
            try {
                val mysteryMovie = getDailyMysteryMovie(allMovies)
                selectedMysteryMovie = mysteryMovie
                isLoading = false
            } catch (e: Exception) {
                Log.e("DailyMystery", "Error getting daily mystery: ${e.message}")
                errorMessage = "Failed to load daily mystery movie"
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        errorMessage != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "Error loading mystery movie",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
        selectedMysteryMovie != null -> {
            DailyMysteryContent(
                movie = selectedMysteryMovie!!,
                viewModel = viewModel,
                commentViewModel = commentViewModel
            )
        }
    }
}

// Display the daily mystery movie with all details and comments
@Composable
fun DailyMysteryContent(
    movie: ContentEntity,
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel
) {
    val comments by commentViewModel.commentsForMovie(movie.id).collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    val averageRating = remember(comments) {
        if (comments.isNotEmpty()) {
            comments.map { it.rating }.average()
        } else {
            0.0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "Today's Mystery Movie",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9B4DFF)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Movie Thumbnail
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.thumbnailUrl)
                .crossfade(true)
                .build()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Movie Title
        Text(
            text = movie.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Movie Subtitle/Tag
        Text(
            text = movie.tag,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Average Rating Display
        Text(
            text = "Community Rating",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < averageRating.roundToInt())
                        Color(0xFFFFC107)
                    else
                        Color.Transparent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = String.format("%.1f / 5", averageRating),
                color = Color.White,
                fontSize = 12.sp
            )

            Text(
                text = " (${comments.size} reviews)",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Movie Description
        Text(
            text = movie.description,
            fontSize = 14.sp,
            color = Color.White,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Comment Section Header
        Text(
            text = "Your Rating & Comment",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Star Rating Selector
        StarRatingSelector(
            rating = rating,
            onRatingSelected = { rating = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Comment Input
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Share your thoughts...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser ?: return@Button

                commentViewModel.submitComment(
                    CommentEntity(
                        id = "",
                        userId = user.uid,
                        userName = user.displayName ?: "Anonymous",
                        movieId = movie.id,
                        movieName = movie.title,
                        comment = commentText,
                        rating = rating,
                        timestamp = System.currentTimeMillis()
                    )
                )

                commentText = ""
                rating = 0
            },
            enabled = commentText.isNotBlank() && rating > 0,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B4DFF))
        ) {
            Text("Post Comment")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments Title
        Text(
            text = "All Comments (${comments.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Comments List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(comments, key = { it.id }) { comment ->
                CommentItem(
                    comment = comment,
                    onDelete = { commentId ->
                        commentViewModel.deleteComment(commentId)
                    },
                    onUpdate = { updatedComment ->
                        commentViewModel.updateComment(updatedComment)
                    }
                )
            }
        }
    }
}

// Get the daily mystery movie for the current day
// Ensures all users see the same movie for the entire day
fun getDailyMysteryMovie(movies: List<ContentEntity>): ContentEntity {
    if (movies.isEmpty()) {
        throw IllegalArgumentException("No movies available")
    }

    // Get today's date in format YYYY-MM-DD
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val todayString = dateFormat.format(calendar.time)

    // Generate a seed based on today's date for consistency across all users
    val seed = todayString.hashCode().toLong()

    // Use the seed to select a movie deterministically
    val index = (seed % movies.size).toInt().let { if (it < 0) it + movies.size else it }

    return movies[index]
}
