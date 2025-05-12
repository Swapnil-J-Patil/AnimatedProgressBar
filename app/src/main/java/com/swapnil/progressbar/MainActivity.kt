package com.swapnil.progressbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.swapnil.progressbar.ui.theme.ProgressBarTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProgressBarTheme {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    GrimReaperAttackAnimation()
                    RunningManCanvas(modifier = Modifier.padding(top=200.dp))
                }
            }
        }
    }
}

@Composable
fun GrimReaperAttackAnimation() {
    var attacking by remember { mutableStateOf(false) }

    // Trigger the attack
    LaunchedEffect(Unit) {
        while (true) {
            attacking = true
            delay(400)
            attacking = false
            delay(1600)
        }
    }

    val swingAngleArm by animateFloatAsState(
        targetValue = if (attacking) 15f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "ArmRotation"
    )
    val scytheRotation by animateFloatAsState(
        targetValue = if (attacking) 15f else 0f,
        animationSpec = tween(400, easing = LinearOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(start = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Body (doesn't move)
        Image(
            painter = painterResource(id = R.drawable.grim_reaper_body),
            contentDescription = "Body",
            modifier = Modifier.fillMaxSize()
        )

        // Scythe - rotate around pivot
        Image(
            painter = painterResource(id = R.drawable.grim_reaper_scythe),
            contentDescription = "Scythe",
            modifier = Modifier
                .fillMaxSize()
                .rotate(20f)
                .graphicsLayer(
                    rotationZ = scytheRotation,
                    transformOrigin = TransformOrigin(0.2f, 0.1f) // adjust for pivot
                )
        )

        // Arm - rotate with scythe
        Image(
            painter = painterResource(R.drawable.grim_reaper_hands),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = swingAngleArm,
                    transformOrigin = TransformOrigin(0.2f, 0.1f)
                )
        )
    }
}

@Composable
fun RunningManCanvas(modifier: Modifier = Modifier) {
    val duration = 700
    val transition = rememberInfiniteTransition()

    val frontArmRotation by transition.animateFloat(
        initialValue = 24f,
        targetValue = 164f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val legRotation by transition.animateFloat(
        initialValue = 10f,
        targetValue = 108f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // ✅ Precompute dp to px here (allowed in @Composable context)
    val density = LocalDensity.current
    val headRadiusPx = with(density) { 8.dp.toPx() }
    val armLengthPx = with(density) { 11.dp.toPx() }
    val legLengthPx = with(density) { 12.dp.toPx() }
    val bodyWidthPx = with(density) { 8.dp.toPx() }
    val bodyHeightPx = with(density) { 30.dp.toPx() }
    val armOffsetYPx = with(density) { 6.dp.toPx() }
    val legOffsetYPx = with(density) { 10.dp.toPx() }
    val cornerRadiusPx = with(density) { 4.dp.toPx() }
    val bodyTopOffsetPx = with(density) { 10.dp.toPx() }
    val headOffsetYPx = with(density) { 20.dp.toPx() }
    val limbHeightPx = with(density) { 4.dp.toPx() }

    Canvas(modifier = modifier.size(100.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Head
        drawCircle(
            color = Color.White,
            radius = headRadiusPx,
            center = Offset(centerX, centerY - headOffsetYPx)
        )

        // Body
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(centerX - bodyWidthPx / 2, centerY - bodyTopOffsetPx),
            size = Size(bodyWidthPx, bodyHeightPx),
            cornerRadius = CornerRadius(cornerRadiusPx)
        )

        // Front Arm
        withTransform({
            rotate(frontArmRotation, pivot = Offset(centerX, centerY - armOffsetYPx))
        }) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX, centerY - armOffsetYPx),
                size = Size(armLengthPx, limbHeightPx),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }

        // Front Leg
        withTransform({
            rotate(legRotation, pivot = Offset(centerX, centerY + legOffsetYPx))
        }) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX, centerY + legOffsetYPx),
                size = Size(legLengthPx, limbHeightPx),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    Box(modifier = Modifier.fillMaxSize()
        .background(Color.Black),
        contentAlignment = Alignment.Center) {
        GrimReaperAttackAnimation()
        RunningManCanvas(modifier = Modifier.padding(top=200.dp))
    }
}
@Composable
fun RunningMan(modifier: Modifier = Modifier) {
    val duration = 700
    val infiniteTransition = rememberInfiniteTransition(label = "runningMan")

    val outerTranslateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                0f at 0
                4f at 250
                0f at 500
                4f at 750
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "outerY"
    )

    val bodyRotation by infiniteTransition.animateFloat(
        initialValue = 32f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                32f at 0
                24f at 175
                16f at 350
                24f at 525
                32f at 700
            }
        ),
        label = "bodyRotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer { translationY = outerTranslateY }
            .size(200.dp)
    ) {
        Body(rotation = bodyRotation)
    }
}

@Composable
private fun Body(rotation: Float) {
    Box(
        modifier = Modifier
            .offset(y = 10.dp)
            .size(width = 8.dp, height = 15.dp)
            .graphicsLayer(
                rotationZ = rotation,
                transformOrigin = TransformOrigin(0.5f, 11f / 15f)
            )
            .background(
                Color.Black, shape = RoundedCornerShape(4.dp)
            )
    ) {
        // Head
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
                .size(8.dp)
                .background(
                    Color.Black, CircleShape
                )
        )

        // Arms
        Limb(
            angleFrom = 24f, angleTo = 164f, duration = 700, front = true,
            modifier = Modifier.align(Alignment.TopCenter) // center-aligned
        )

        Limb(
            angleFrom = 164f, angleTo = 24f, duration = 700, front = false,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Legs
        Leg(
            angleFrom = 10f, angleTo = 108f, duration = 700, front = true,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Leg(
            angleFrom = 108f, angleTo = 10f, duration = 700, front = false,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}

@Composable
private fun Limb(
    angleFrom: Float,
    angleTo: Float,
    duration: Int,
    front: Boolean,
    modifier: Modifier
) {
    val rotation by rememberInfiniteTransition(label = "limb").animateFloat(
        initialValue = angleFrom,
        targetValue = angleTo,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "limbRotation"
    )
    Box(
        modifier = modifier
            .zIndex(if (front) 1f else 0f)
            .size(width = 11.dp, height = 4.dp)
            .graphicsLayer(
                rotationZ = rotation,
                transformOrigin = TransformOrigin(0f, 0.5f) // pivot at shoulder
            )
            .background(Color.Black, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .offset(x = 6.dp)
                .size(width = 11.dp, height = 4.dp)
                .graphicsLayer(
                    rotationZ = if (front) -48f else -36f,
                    transformOrigin = TransformOrigin(0f, 0.5f)
                )
                .background(Color.Black, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun Leg(
    angleFrom: Float,
    angleTo: Float,
    duration: Int,
    front: Boolean,
    modifier: Modifier
) {
    val rotation by rememberInfiniteTransition(label = "leg").animateFloat(
        initialValue = angleFrom,
        targetValue = angleTo,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "legRotation"
    )
    Box(
        modifier = modifier
            .zIndex(if (front) 1f else 0f)
            .size(width = 12.dp, height = 4.dp)
            .graphicsLayer(
                rotationZ = rotation,
                transformOrigin = TransformOrigin(0f, 0.5f) // pivot at hip
            )
            .background(if (front) Color.Black else Color.Transparent, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .offset(x = 6.dp)
                .size(width = 12.dp, height = 4.dp)
                .graphicsLayer(
                    rotationZ = if (front) 18f else 76f,
                    transformOrigin = TransformOrigin(0f, 0.5f)
                )
                .background(Color.Black, RoundedCornerShape(2.dp))
        )
    }
}



