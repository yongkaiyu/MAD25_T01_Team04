package np.ict.mad.t01_team04

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

// Review Page UI
@Composable
fun ReviewScreen(
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel
) {

    // Load movie list from Room/Firestore
    val movieList by viewModel.contentList.collectAsState()

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    var selectedMovieTitle by remember { mutableStateOf("Select a Movie") }
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    // Comment input state
    var commentText by remember { mutableStateOf("") }

    var rating by remember { mutableStateOf(0) }

    // Comment display state
    val comments by remember(selectedMovieId) {
        selectedMovieId?.let {
            commentViewModel.commentsForMovie(it)
        }
    }?.collectAsState(initial = emptyList()) ?: remember {
        mutableStateOf(emptyList())
    }

    val averageRating = remember(comments) {
        if (comments.isNotEmpty()) {
            comments.map { it.rating }.average()
        } else {
            0.0
        }
    }

    // Firebase user
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        // Add a Comment Title
        Text(
            text = "Add a Comment",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- Movie Selector Dropdown ---
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedMovieTitle)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                movieList.forEach { movie ->
                    DropdownMenuItem(
                        text = { Text(movie.title) },
                        onClick = {
                            selectedMovieTitle = movie.title
                            selectedMovieId = movie.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Movie Id Display (Used For Testing)
        /* Text(
            text = "Selected Movie ID: ${selectedMovieId ?: "None"}",
            color = Color.Gray,
            fontSize = 14.sp
        ) */

        // Show ONLY after movie selected
        if (selectedMovieId != null) {

            Spacer(modifier = Modifier.height(20.dp))

            // Average Rating Title Display
            Text(
                text = "Average Rating",
                color = Color.White,
                fontSize = 14.sp
            )

            // Average Rating Display for Movie
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Star Display UI
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

                Spacer(modifier = Modifier.width(8.dp))

                // Average Rating Number Display
                Text(
                    text = String.format("%.1f / 5", averageRating),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Your Rating Title
            Text(
                text = "Your Rating",
                color = Color.White,
                fontSize = 14.sp
            )

            // Star Rating Selector
            StarRatingInput(
                rating = rating,
                onRatingChanged = { rating = it }
            )

            // Comment Input
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Your comment") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF9B4DFF),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF9B4DFF),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (commentText.isNotBlank() && currentUser != null && rating > 0) {

                        val comment = CommentEntity(
                            id = "",
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Anonymous",
                            movieId = selectedMovieId!!,
                            movieName = selectedMovieTitle,
                            comment = commentText,
                            rating = rating,
                            timestamp = System.currentTimeMillis()
                        )

                        commentViewModel.submitComment(comment)
                        commentText = "" // clear after submit
                        rating = 0
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = commentText.isNotBlank() && rating > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B4DFF))
            ) {
                Text("Submit Comment")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Comments Title Display
        Text(
            text = "Comments",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Comments from the movie section
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comments, key = { it.id }) { comment ->
                Log.d("RATING_DEBUG", "rating=${comment.rating}")
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

// Function for Star Rating Input
@Composable
fun StarRatingInput(
    rating: Int,                  // 0..5
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starValue = index + 1

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rate $starValue stars",
                tint = if (index < rating)
                    Color(0xFFFFC107)
                else
                    Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onRatingChanged(starValue) // 1..5
                    }
            )
        }
    }
}
