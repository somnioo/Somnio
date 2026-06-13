package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DreamIndigo
import com.example.ui.theme.DreamPurple
import com.example.ui.theme.NebulaCyan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SomnioGlowingBanner(
    modifier: Modifier = Modifier,
    title: String,
    tagline: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_anim")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(DreamPurple, CyberPink, DreamIndigo),
                    start = Offset(animOffset, 0f),
                    end = Offset(animOffset + 500f, 600f),
                    tileMode = TileMode.Mirror
                )
            )
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xE607070B)) // Deep dark contrast inside
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Glow",
                        tint = CyberPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tagline,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun DreamProceduralVisualizer(
    modifier: Modifier = Modifier,
    seed: Long,
    moodTags: String,
    height: Dp = 200.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "procedural_anim")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Select dynamic colors based on our tags
    val colors = remember(moodTags) {
        val lowercase = moodTags.lowercase()
        when {
            lowercase.contains("cyberpubk") || lowercase.contains("cyberpunk") || lowercase.contains("synthetic") -> {
                listOf(CyberPink, DreamIndigo, Color.Black)
            }
            lowercase.contains("cosmic") || lowercase.contains("stellar") || lowercase.contains("space") -> {
                listOf(DreamPurple, NebulaCyan, Color.Black)
            }
            lowercase.contains("ethereal") || lowercase.contains("fluid") || lowercase.contains("ambient") -> {
                listOf(NebulaCyan, DreamIndigo, Color.Black)
            }
            else -> {
                listOf(DreamPurple, CyberPink, Color.Black)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF030307))
            .border(1.dp, Brush.linearGradient(listOf(colors[0].copy(alpha = 0.4f), colors[1].copy(alpha = 0.4f))), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val heightPix = size.height
            val center = Offset(width / 2, heightPix / 2)

            // Draw radial stardust gradient background
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(colors[0].copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = width / 1.5f
                )
            )

            // Dynamic rotation matrix
            rotate(rotationAngle, center) {
                // Orbital planetary structures
                val numStarPlates = (seed % 4 + 3).toInt()
                for (i in 0 until numStarPlates) {
                    val radius = (width / 5f) * (i + 1) * 0.7f * pulseScale
                    drawCircle(
                        color = colors[i % colors.size].copy(alpha = 0.15f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2f)
                    )

                    // Draw companion celestial node
                    val angleOffset = (seed + i * 45) * (Math.PI / 180f)
                    val nodeCenter = Offset(
                        center.x + radius * cos(angleOffset + rotationAngle * (Math.PI / 180f)).toFloat(),
                        center.y + radius * sin(angleOffset + rotationAngle * (Math.PI / 180f)).toFloat()
                    )
                    drawCircle(
                        color = colors[i % colors.size],
                        radius = 8f + (i * 2),
                        center = nodeCenter
                    )

                    // Add sparkling star flares
                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f),
                        radius = 2f + i,
                        center = nodeCenter
                    )
                }

                // Core shining dream node
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, colors[0], Color.Transparent),
                        center = center,
                        radius = 35f * pulseScale
                    ),
                    radius = 45f * pulseScale,
                    center = center
                )
            }
        }

        // Ambient overlay showing the procedural seed
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(Color(0x99000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "NEXUS SEED #${seed.toString().takeLast(6)}",
                color = Color.Gray,
                fontSize = 8.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
