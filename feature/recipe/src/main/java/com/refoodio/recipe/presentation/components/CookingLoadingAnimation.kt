package com.refoodio.recipe.presentation.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CookingLoadingAnimation(
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "cooking")

    // Tencere kapağı zıplama
    val lidOffset by infinite.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "lid"
    )

    // 3 buhar dalgası — farklı phase'lerde
    val steam1 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "s1"
    )
    val steam2 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "s2"
    )
    val steam3 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "s3"
    )

    // Dönen yıldız
    val starRot by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "star"
    )

    // Loading dots
    val d1 by infinite.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400), repeatMode = RepeatMode.Reverse
        ), label = "d1"
    )
    val d2 by infinite.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 133), repeatMode = RepeatMode.Reverse
        ), label = "d2"
    )
    val d3 by infinite.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 266), repeatMode = RepeatMode.Reverse
        ), label = "d3"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ana animasyon kutusu
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Arka plan dairesi
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(130.dp)
            ) {}

            // Dönen ✨ dekoratif
            Text(
                text = "✨",
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 20.dp)
                    .rotate(starRot)
            )

            // Buhar
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 18.dp)
                    .graphicsLayer { alpha = ((steam1 + steam2 + steam3) / 3f) },
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("~", fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer { alpha = steam1 }.offset(y = (-steam1 * 6).dp))
                Text("~", fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer { alpha = steam2 }.offset(y = (-steam2 * 8).dp))
                Text("~", fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer { alpha = steam3 }.offset(y = (-steam3 * 6).dp))
            }

            // Tencere emoji — zıplıyor
            Text(
                text = "🍳",
                fontSize = 56.sp,
                modifier = Modifier.offset(y = lidOffset.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Şef düşünüyor...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Malzemelerini analiz edip\nen lezzetli tarifi hazırlıyorum",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        // Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            listOf(d1, d2, d3).forEach { s ->
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(8.dp).scale(s)
                ) {}
            }
        }
    }
}
