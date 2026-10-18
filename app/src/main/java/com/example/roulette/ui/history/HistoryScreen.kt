
package com.example.roulette.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Graphite = Color(0xFF242424)
private val SecondaryText = Color(0xFF858585)

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Graphite
            )
        ) {
            Text("← Назад", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "История измерений",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Graphite
        )

        Spacer(modifier = Modifier.height(8.dp))


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "История пуста",
                        fontWeight = FontWeight.Medium,
                        color = Graphite
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                }
            }
        }
    }
}
