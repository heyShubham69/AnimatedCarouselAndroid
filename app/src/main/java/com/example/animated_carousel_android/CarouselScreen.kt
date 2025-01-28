package com.example.animated_carousel_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun CarouselScreen(imageList: List<String>) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // State to track the background image
    var backgroundImage by remember { mutableStateOf(imageList[0]) }

    // Monitor the visible item index and update the background image
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }.collect { index ->

            Log.d("Carousel", "Visible Index: $index")
            backgroundImage = imageList[index % imageList.size]
        }
    }

    LaunchedEffect(Unit) {
        // Auto-scroll every 3 seconds
        while (true) {
            coroutineScope.launch {
                val currentIndex = lazyListState.firstVisibleItemIndex
                val nextIndex = (currentIndex + 1) % imageList.size
                lazyListState.animateScrollToItem(nextIndex)
            }
            kotlinx.coroutines.delay(3000L)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        LazyRow(
//            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(imageList) { imageUrl ->
                CarouselItem(imageUrl)
            }
        }
    }
}

@Composable
fun CarouselItem(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .width(300.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .graphicsLayer {  // Add subtle scaling effect for animations
                val scale = 1f + (0.05f * kotlin.math.sin(System.currentTimeMillis() / 300f))
                scaleX = scale
                scaleY = scale
            },
        contentScale = ContentScale.Crop,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCarousel() {
    val sampleImages = LocalContext.current.resources.getStringArray(R.array.imageList).toList()

    CarouselScreen(imageList = sampleImages)
}
