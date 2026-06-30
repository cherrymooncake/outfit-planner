package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.mask

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask.MaskEditorEvent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask.MaskEditorIntent
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.viewmodel.MaskEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaskEditorScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    viewModel: MaskEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        viewModel.processIntent(MaskEditorIntent.LoadItem(itemId))
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is MaskEditorEvent.NavigateBack -> onNavigateBack()
                is MaskEditorEvent.ShowSnackbar -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    var intrinsicSize by remember { mutableStateOf(Size.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Лассо (Обводка)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Назад") }
                },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(MaskEditorIntent.ClearPoints) }) {
                        Icon(Icons.Default.Clear, "Очистить")
                    }
                    IconButton(onClick = { viewModel.processIntent(MaskEditorIntent.RestoreAuto) }) {
                        Icon(Icons.Default.Refresh, "Сбросить")
                    }
                    IconButton(
                        onClick = {
                            if (canvasSize != Size.Zero && intrinsicSize != Size.Zero) {
                                viewModel.processIntent(
                                    MaskEditorIntent.ApplyMask(
                                        canvasWidth = canvasSize.width,
                                        canvasHeight = canvasSize.height,
                                        imageIntrinsicWidth = intrinsicSize.width,
                                        imageIntrinsicHeight = intrinsicSize.height
                                    )
                                )
                            }
                        },
                        enabled = !state.isSaving
                    ) {
                        Icon(Icons.Default.Check, "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE5E5E5)),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading || state.isSaving) {
                CircularProgressIndicator()
            } else if (state.originalImageUrl.isNotEmpty()) {

                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.originalImageUrl)
                        .crossfade(true)
                        .build()
                )

                val painterState = painter.state
                if (painterState is AsyncImagePainter.State.Success) {
                    intrinsicSize = painterState.painter.intrinsicSize
                }

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val constraints = this.constraints
                    if (intrinsicSize != Size.Zero && constraints.maxHeight > 0) {
                        val imgRatio = intrinsicSize.width / intrinsicSize.height
                        val boxRatio = constraints.maxWidth.toFloat() / constraints.maxHeight.toFloat()

                        val renderWidth: Float
                        val renderHeight: Float

                        if (imgRatio > boxRatio) {
                            renderWidth = constraints.maxWidth.toFloat()
                            renderHeight = renderWidth / imgRatio
                        } else {
                            renderHeight = constraints.maxHeight.toFloat()
                            renderWidth = renderHeight * imgRatio
                        }

                        canvasSize = Size(renderWidth, renderHeight)

                        Box(
                            modifier = Modifier
                                .size(
                                    width = with(LocalDensity.current) { renderWidth.toDp() },
                                    height = with(LocalDensity.current) { renderHeight.toDp() }
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            viewModel.processIntent(MaskEditorIntent.ClearPoints)
                                            viewModel.processIntent(MaskEditorIntent.AddPoint(offset))
                                        },
                                        onDrag = { change, _ ->
                                            viewModel.processIntent(MaskEditorIntent.AddPoint(change.position))
                                        }
                                    )
                                }
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val path = Path()
                                state.points.forEachIndexed { index, point ->
                                    if (index == 0) path.moveTo(point.x, point.y)
                                    else path.lineTo(point.x, point.y)
                                }
                                if (state.points.isNotEmpty()) {
                                    drawPath(
                                        path = path,
                                        color = Color(0xFFD128A1),
                                        style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                    drawPath(
                                        path = path,
                                        color = Color(0xFFD128A1).copy(alpha = 0.3f),
                                        style = Fill
                                    )
                                }
                            }
                        }
                    } else {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}