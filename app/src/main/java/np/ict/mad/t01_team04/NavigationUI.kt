package np.ict.mad.t01_team04


import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import np.ict.mad.t01_team04.ui.theme.MAD25_T01_Team04Theme
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID



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
                        onBack = { currentContentId = null }

                    )
                } else {
                    when (currentDestination) {
                        AppDestinations.HOME -> Home()
                        AppDestinations.MOVIES -> Movies(
                            viewModel = viewModel,
                            onItemClick = { id -> currentContentId = id }
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

    REVIEW("Review", Icons.Default.ThumbUp),

    PROFILE("Profile", Icons.Default.AccountBox),
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {

    val videos = listOf(
        "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = {videos.size})

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Full-screen vertical swipe feed (Reels/TikTok style) ---
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            VideoPlayer(
                uri = videos[page].toUri(),
                modifier = Modifier.fillMaxSize()
            )
        }

        // --- Top-left Header ---
        Text(
            text = "For You",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(top = 40.dp, start = 20.dp)
                .align(Alignment.TopStart)
        )
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
    //val uri = remember(url) { url.toUri() }
    val context = LocalContext.current
    val isInEditMode = LocalView.current.isInEditMode

    if (!isInEditMode) {
        val exoPlayer = remember(uri) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                playWhenReady = true
            }
        }

        var isLoading by remember { mutableStateOf(true) }

        DisposableEffect(exoPlayer) {
            val listener = object : Player.Listener {
                override fun onIsLoadingChanged(isLoadingNow: Boolean) {
                    isLoading = isLoadingNow
                }
            }
            exoPlayer.addListener(listener)
            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }

        Box(modifier = modifier)
        {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = false

                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                modifier = modifier.fillMaxSize()
            )

            // --- Loading spinner ---
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }
    } else {
        // Preview placeholder
        Box(
            modifier
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Video Preview Disabled")
        }
    }
}

@Composable
fun Movies(viewModel: ContentViewModel, onItemClick: (String) -> Unit) {
    val contentList by viewModel.contentList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Section title
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Trending",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Horizontal scroll list of content
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(contentList) { item ->
                MovieCard(item = item, onClick = {
                    // Navigate to details page
                    // Pass item.id
                    onItemClick(item.id)
                })
            }
        }
    }
}

@Composable
fun MovieCard(item: ContentEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White) // white background
        ) {
            if (item.thumbnailUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    //modifier = Modifier
                        //.height(200.dp)
                        //.fillMaxWidth()
                        //.clip(RoundedCornerShape(8.dp))

                )
            } else {
                // fallback if URL empty
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = Color.White)
                }
            }
        }


        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ContentDetailScreen(contentId: String, viewModel: ContentViewModel, onBack: () -> Unit) {
    val content by viewModel.getDetails(contentId).collectAsState()

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
        }
    }
}

@Composable
fun CommentItem(comment: CommentEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = comment.userName,
            color = Color(0xFF9B4DFF),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = comment.comment,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}


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
