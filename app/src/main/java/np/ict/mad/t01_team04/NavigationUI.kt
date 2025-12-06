package np.ict.mad.t01_team04


import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
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
import androidx.media3.common.util.UnstableApi

class NavigationUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window,false)

        val controller = WindowCompat.getInsetsController(window, window.decorView)

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        setContent {
            MAD25_T01_Team04Theme {
                MAD25_T01_Team04App()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun MAD25_T01_Team04App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

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
                        label = { Text(it.label, color = if (selected) Color(0xFF9B4DFF) else Color.White) },
                        selected = selected,
                        onClick = { currentDestination = it }
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
                when (currentDestination) {
                    AppDestinations.HOME -> Home()
                    AppDestinations.MOVIES -> GreetingPreview()
                    AppDestinations.PROFILE -> GreetingPreview()
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
fun isInEditMode(): Boolean {
    return LocalView.current.isInEditMode
}

@Composable
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
}

