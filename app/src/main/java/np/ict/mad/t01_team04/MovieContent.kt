package np.ict.mad.t01_team04

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ContentDetailScreen(contentId: String, viewModel: ContentViewModel, commentViewModel: CommentViewModel, onBack: () -> Unit) {
    // Fix flickering of white loader
    val contentFlow = remember(contentId) {
        viewModel.getDetails(contentId)
    }

    val content by contentFlow.collectAsState(initial = null)

    val commentsFlow = remember(contentId) {
        commentViewModel.commentsForMovie(contentId)
    }

    val comments by commentsFlow.collectAsState(initial = emptyList())

    var commentText by remember { mutableStateOf("") }

    var rating by remember { mutableIntStateOf(0) }

    if (content == null) {
        // Loading placeholder while content is being fetched
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        val item = content!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = item.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Use AsyncImagePainter explicitly
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.thumbnailUrl)
                    .crossfade(true)
                    .build()
            )

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

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.description,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            /*Text(
                text = "Comments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))*/

            Text(
                text = "Your Rating",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            StarRatingSelector(
                rating = rating,
                onRatingSelected = { rating = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---- Comment input ----
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

            Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser ?: return@Button

                    commentViewModel.submitComment(
                        CommentEntity(
                            id = "",
                            userId = user.uid,
                            userName = user.displayName ?: "Anonymous",
                            movieId = contentId,
                            movieName = item.title,
                            comment = commentText,
                            rating = rating,
                            timestamp = System.currentTimeMillis()
                        )
                    )

                    commentText = ""
                    rating = 0
                },
                enabled = commentText.isNotBlank(),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Post")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comments",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments, key = { it.id }) { comment ->
                    CommentItem(
                        comment = comment,
                        onDelete = { commentId ->
                            commentViewModel.deleteComment(commentId)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun StarRatingSelector(
    rating: Int,                  // 0..5
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starValue = index + 1

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star $starValue",
                tint = if (index < rating)
                    Color(0xFFFFC107)
                else
                    Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onRatingSelected(starValue) // 1..5
                    }
            )
        }
    }
}


