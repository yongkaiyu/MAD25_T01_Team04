package np.ict.mad.t01_team04

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

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