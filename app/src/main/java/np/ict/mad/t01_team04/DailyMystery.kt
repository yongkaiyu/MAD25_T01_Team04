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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// Daily Mystery Movie Screen UI
@Composable
fun DailyMysteryScreen(
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel,
    onBack: () -> Unit
) {
    var selectedMysteryMovie by remember { mutableStateOf<ContentEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentDate by remember { mutableStateOf(Calendar.getInstance().time) }

    val allMovies by viewModel.contentList.collectAsState()

    // Load the daily mystery movie on composition
    LaunchedEffect(allMovies, currentDate) {
        if (allMovies.isNotEmpty()) {
            try {
                isLoading = true // Show loader when date changes
                commentViewModel.sync()
                val mysteryMovie = getDailyMysteryMovie(allMovies, currentDate)
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
                commentViewModel = commentViewModel,
                onBack = onBack,
                onChangeDate = { currentDate = Calendar.getInstance().apply { time = currentDate; add(Calendar.DATE, 1) }.time }
            )
        }
    }
}

// Display the daily mystery movie with all details and comments
@Composable
fun DailyMysteryContent(
    movie: ContentEntity,
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel,
    onBack: () -> Unit,
    onChangeDate: () -> Unit
) {
    val commentsFlow = remember(movie.id) {
        commentViewModel.commentsForMovie(movie.id)
    }
    val comments by commentsFlow.collectAsState(initial = emptyList())
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

        // Header with Back and Change Date buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.DarkGray.copy(alpha = 0.6f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Change Date Button
            Button(onClick = onChangeDate) {
                Text(text = "Change Date")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments of the respective movie
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Movie details
            item {
                Column {
                    // Name of movie
                    Text(
                        text = movie.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Use AsyncImagePainter explicitly
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(movie.thumbnailUrl)
                            .crossfade(true)
                            .build()
                    )

                    // Thumbnail of Movie
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray) // Background while loading
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Movie Description
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = movie.description,
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Movie Rating
                    Text(
                        text = "Your Rating",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Star Rating Selector
                    StarRatingSelector(
                        rating = rating,
                        onRatingSelected = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment Input
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a comment...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Comment Button
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
                        Text("Submit Comment")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comment Display Title
                    Text(
                        text = "Comments",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Comments List
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
fun getDailyMysteryMovie(movies: List<ContentEntity>, date: Date): ContentEntity {
    if (movies.isEmpty()) {
        throw IllegalArgumentException("No movies available")
    }

    // Get today's date in format YYYY-MM-DD
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayString = dateFormat.format(date)

    // Generate a seed based on today's date for consistency across all users
    val seed = todayString.hashCode().toLong()

    // Use the seed to select a movie deterministically
    val index = (seed % movies.size).toInt().let { if (it < 0) it + movies.size else it }

    return movies[index]
}
