package com.example.roulette.ui.measurement

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CenterReticle(
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isReady) {
            Color(0xFFFFD54F)
        } else {
            Color.White.copy(alpha = 0.45f)
        },
        animationSpec = tween(durationMillis = 180),
        label = "ReticleColor"
    )

    Box(
        modifier = modifier
            .size(6.dp)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}