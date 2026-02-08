package np.ict.mad.t01_team04


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import np.ict.mad.t01_team04.ui.theme.MAD25_T01_Team04Theme
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth



class NavigationUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window,false)

        val controller = WindowCompat.getInsetsController(window, window.decorView)

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        // --- Build Room database ---
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).fallbackToDestructiveMigration().build()

        val dao = database.contentDao()

        val commentDao = database.commentDao()

        // --- Firestore ---
        val firebase = FirebaseFirestore.getInstance()

        // --- Repository ---
        val repository = ContentRepository(dao, firebase)

        val commentRepository = CommentRepository(commentDao)

        // --- ViewModel  ---
        val factory = ContentViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)
            .get(ContentViewModel::class.java)

        // --- CommentViewModel ---
        val commentFactory = CommentViewModelFactory(commentRepository)

        val commentViewModel = ViewModelProvider(this, commentFactory)
            .get(CommentViewModel::class.java)

        setContent {
            MAD25_T01_Team04Theme {
                MAD25_T01_Team04App(
                    viewModel, commentViewModel)
            }
        }
    }
}

//@PreviewScreenSizes
@Composable
fun MAD25_T01_Team04App(viewModel: ContentViewModel, commentViewModel: CommentViewModel) {

    var isLoggedIn by rememberSaveable { mutableStateOf(true) }

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Track selected content for details page
    var currentContentId by rememberSaveable { mutableStateOf<String?>(null) }


    if (!isLoggedIn) {
        // Show login screen if logged out
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {

        NavigationSuiteScaffold(
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = Color.Black,
                navigationBarContentColor = Color.White,
            ),
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    val selected = it == currentDestination

                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = it.label,
                                tint = if (selected) Color(0xFF9B4DFF) else Color.White
                            )
                        },
                        label = {
                            Text(
                                it.label,
                                color = if (selected) Color(0xFF9B4DFF) else Color.White
                            )
                        },
                        selected = selected,
                        onClick = {
                            currentDestination = it
                            currentContentId = null
                        }
                    )
                }
            }
        ) {
            // Navigation UI
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
            {
                if (currentContentId != null) {
                    ContentDetailScreen(
                        contentId = currentContentId!!,
                        viewModel = viewModel,
                        commentViewModel = commentViewModel,
                        onBack = { currentContentId = null }

                    )
                } else {
                    when (currentDestination) {
                        AppDestinations.HOME -> Home()
                        AppDestinations.MOVIES -> Movies(
                            viewModel = viewModel,
                            onItemClick = { id -> currentContentId = id }
                        )
                        AppDestinations.DAILY -> DailyMysteryScreen(
                            viewModel = viewModel,
                            commentViewModel = commentViewModel
                        )
                        AppDestinations.REVIEW -> ReviewScreen(viewModel = viewModel, commentViewModel = commentViewModel)
                        AppDestinations.PROFILE -> ProfileUI(
                            username = "GuestUser",
                            onLogout = {
                                isLoggedIn = false
                            }
                        )
                    }
                }
            }

            /*
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Greeting2(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )
            } */
        }
    }

}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),

    MOVIES("Movies", Icons.Default.PlayArrow),

    DAILY("Mystery", Icons.Default.Search),

    REVIEW("Review", Icons.Default.ThumbUp),

    PROFILE("Profile", Icons.Default.AccountBox),
}

// Layout UI logic for one comment display item
@Composable
fun CommentItem(
    comment: CommentEntity,
    onDelete: (String) -> Unit,
    onUpdate: (CommentEntity) -> Unit
) {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = comment.userId == currentUserId

    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(comment.comment) }
    var editedRating by remember { mutableStateOf(comment.rating) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comment.userName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Delete icon
            if (isOwner && !isEditing) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Comment",
                        tint = Color.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Editing Mode
        if (isEditing && isOwner) {

            StarRatingInput(
                rating = editedRating,
                onRatingChanged = { editedRating = it }
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        editedText = comment.comment
                        editedRating = comment.rating
                        isEditing = false
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        onUpdate(
                            comment.copy(
                                comment = editedText,
                                rating = editedRating,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        isEditing = false
                    },
                    enabled = editedText.isNotBlank() && editedRating > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B4DFF))
                ) {
                    Text("Save")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Delete button moves DOWN in edit mode
            TextButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete", color = Color.Red)
            }

        } else {

            //  Read-only Mode
            Column(
                modifier = Modifier
                    .clickable(enabled = isOwner) {
                        isEditing = true
                    }
            ) {
                ReadOnlyStarRating(rating = comment.rating)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.comment,
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete comment?")
            },
            text = {
                Text("This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(comment.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReadOnlyStarRating(
    rating: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < rating)
                    Color(0xFFFFC107)
                else
                    Color.Transparent,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}





@Composable
fun isInEditMode(): Boolean {
    return LocalView.current.isInEditMode
}

/*@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MAD25_T01_Team04Theme {
        Greeting2("Android2")
    }
}*/

@Composable
fun ProfileUI(
    username: String = "GuestUser",
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()

            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // --- Profile Picture ---
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0xFF9B4DFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Profile Icon",
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Username ---
            Text(
                text = username,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "cinexplorer.user@app.com",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- Settings List ---
            ProfileOption("Saved Movies")
            ProfileOption("My Watchlist")
            ProfileOption("Account Settings")
            ProfileOption("Notifications")
            ProfileOption("Privacy & Security")

            Spacer(modifier = Modifier.height(40.dp))

            // --- Logout Button ---
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B4DFF)
                )
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
fun ProfileOption(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
