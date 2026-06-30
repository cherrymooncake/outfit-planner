package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun OutfitPreview(
    outfit: OutfitUiModel,
    modifier: Modifier = Modifier
) {
    val safeCanvasWidth = if (outfit.canvasWidth > 0) outfit.canvasWidth.toFloat() else 800f
    val safeCanvasHeight = if (outfit.canvasHeight > 0) outfit.canvasHeight.toFloat() else 600f
    val safeAspectRatio = safeCanvasWidth / safeCanvasHeight

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(safeAspectRatio)
            .background(Color(0xFFF9FAFC))
            .clipToBounds()
    ) {
        val canvasPxW = constraints.maxWidth.toFloat()

        val scaleFactor = canvasPxW / safeCanvasWidth
        val density = LocalDensity.current.density

        val sortedItems = outfit.items.sortedBy { it.zIndex }

        Box(modifier = Modifier.fillMaxSize()) {
            sortedItems.forEach { item ->

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .size(coil.size.Size.ORIGINAL)
                        .crossfade(true)
                        .build(),
                    contentDescription = null
                ) {
                    val painterState = painter.state
                    if (painterState is AsyncImagePainter.State.Success) {
                        val intrinsicSize = painterState.painter.intrinsicSize

                        if (intrinsicSize.width > 0 && intrinsicSize.height > 0) {
                            val logicalItemWidth = intrinsicSize.width * item.scale
                            val logicalItemHeight = intrinsicSize.height * item.scale

                            val renderItemWidthPx = logicalItemWidth * scaleFactor
                            val renderItemHeightPx = logicalItemHeight * scaleFactor

                            val offsetX = (item.x * scaleFactor) - (renderItemWidthPx / 2)
                            val offsetY = (item.y * scaleFactor) - (renderItemHeightPx / 2)

                            Box(
                                modifier = Modifier
                                    .offset {
                                        IntOffset(offsetX.toInt(), offsetY.toInt())
                                    }
                                    .size(
                                        width = (renderItemWidthPx / density).dp,
                                        height = (renderItemHeightPx / density).dp
                                    )
                                    .graphicsLayer {
                                        rotationZ = item.rotation
                                        transformOrigin = TransformOrigin.Center
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterState.painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}