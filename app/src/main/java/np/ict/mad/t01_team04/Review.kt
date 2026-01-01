package np.ict.mad.t01_team04

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

@Composable
fun ReviewScreen(
    viewModel: ContentViewModel,
    commentViewModel: CommentViewModel) {

    // Load movie list from Room/Firestore
    val movieList by viewModel.contentList.collectAsState()

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    var selectedMovieTitle by remember { mutableStateOf("Select a Movie") }
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    // --- Comment input state ---
    var commentText by remember { mutableStateOf("") }

    // --- Comment display state ---
    val comments by remember(selectedMovieId) {
        selectedMovieId?.let {
            commentViewModel.commentsForMovie(it)
        }
    }?.collectAsState(initial = emptyList()) ?: remember {
        mutableStateOf(emptyList())
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

        // Additional UI later (comment textbox, submit button)
        Text(
            text = "Selected Movie ID: ${selectedMovieId ?: "None"}",
            color = Color.Gray,
            fontSize = 14.sp
        )

        // --- Show ONLY after movie selected ---
        if (selectedMovieId != null) {

            Spacer(modifier = Modifier.height(20.dp))

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

            Button(
                onClick = {
                    if (commentText.isNotBlank() && currentUser != null) {

                        val comment = CommentEntity(
                            id = UUID.randomUUID().toString(),   // local ID (Firestore uses auto ID)
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Anonymous",
                            movieId = selectedMovieId!!,
                            movieName = selectedMovieTitle,
                            comment = commentText,
                            timestamp = System.currentTimeMillis()
                        )

                        commentViewModel.submitComment(comment)
                        commentText = "" // clear after submit
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = commentText.isNotBlank()
            ) {
                Text("Submit Comment")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Comments",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment)
            }
        }

    }
}