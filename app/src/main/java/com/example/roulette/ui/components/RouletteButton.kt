
package com.example.roulette.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Graphite = Color(0xFF242424)
private val LightSurface = Color(0xFFECECEC)
private val DisabledText = Color(0xFF999999)

// Общий компонент кнопок приложения
@Composable
fun RouletteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false,
    height: Dp = 52.dp,
    fontSize: TextUnit = 16.sp,
    icon: ImageVector? = null // Необязательная иконка кнопки
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) Graphite else LightSurface,
            contentColor = if (primary) Color.White else Graphite,
            disabledContainerColor = LightSurface,
            disabledContentColor = DisabledText
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        // Размещает иконку и текст горизонтально по центру
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Отображает иконку только если она передана
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
