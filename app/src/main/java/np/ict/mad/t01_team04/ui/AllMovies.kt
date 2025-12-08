package np.ict.mad.t01_team04.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun AllMovies(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Trending Title
        item {
            Text("Trending", fontSize = 26.sp)
        }

        // Horizontal LazyRow
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items((1..10).toList()) {
                    Box(
                        modifier = Modifier
                            .size(140.dp, 200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                    )
                }
            }
        }

        // Categories Title
        item {
            Text("Categories", fontSize = 26.sp)
        }

        // Horizontal categories
        item {
            val categories = listOf("Action", "Horror", "Comedy", "Fiction", "Thriller", "2025")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(cat)
                    }
                }
            }
        }

        // All Movies Title
        item {
            Text("All Movies", fontSize = 26.sp)
        }
    }
}
