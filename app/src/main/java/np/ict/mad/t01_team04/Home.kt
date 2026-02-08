package np.ict.mad.t01_team04

import android.graphics.drawable.Icon
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import coil.compose.AsyncImage
import androidx.compose.runtime.collectAsState
import androidx.room.Delete
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(watchedViewModel: WatchedViewModel) {

    val watchedList by watchedViewModel.watched.collectAsState()

    val videos = listOf(
        "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = {videos.size})

    val currentPage = pagerState.currentPage

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect {
                page -> delay(800)
                val url = videos[page]
                watchedViewModel.addVideoWatched(
                    contentId = url,
                    title = "Video ${page + 1}",
                    thumbnailUrl = ""
                )
            }
    }

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

        Column (
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(bottom = 80.dp, top = 10.dp)
        ) {
            Text(
                text = "Watched",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchedList) {
                    item -> WatchedCard(item = item, onDelete = {watchedViewModel.remove(item.contentId)})
                }
            }
        }
    }
}

@Composable
fun WatchedCard(item: WatchedEntity, onDelete: () -> Unit) {
    Column (modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(Color.DarkGray, RoundedCornerShape(8.dp))
        ) {
            if (item.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton (
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = item.title,
            color = Color.White,
            maxLines = 1
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