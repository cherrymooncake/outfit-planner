package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel

@Composable
fun OutfitCard(
    outfit: OutfitUiModel,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(contentAlignment = Alignment.Center) {
                OutfitPreview(outfit = outfit, modifier = Modifier.fillMaxWidth())

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(bottomStart = 8.dp))
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Изменить", tint = Color.White)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Удалить", tint = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = outfit.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${outfit.items.size} вещей", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    outfit.categories.firstOrNull()?.let {
                        Text(it.name, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD128A1))
                    }
                }
            }
        }
    }
}