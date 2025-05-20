package com.swapnil.progressbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    var progress by remember { mutableStateOf(0f) }
                    var isAnimating by remember { mutableStateOf(false) }

                    // This state will trigger recomposition for days left
                    val daysRemaining = (100 - (progress * 100)).toInt().coerceAtLeast(0)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Countdown Text

                        Text(
                            text = "Days Remaining: $daysRemaining",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Button to trigger animation
                        Button(
                            onClick = {
                                if (!isAnimating) {
                                    isAnimating = true
                                    progress = 0f
                                }
                            },
                            enabled = !isAnimating,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE002A)),
                        ) {
                            Text(text = "Send Death",
                                color = if(!isAnimating)Color.White else Color.Transparent)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Your Grim Reaper Progress Bar
                        GrimReaperChaseProgressBar(progress = progress)
                    }

                    // Animate progress when triggered
                    LaunchedEffect(isAnimating) {
                        if (isAnimating) {
                            val checkpoints = listOf(0f, 0.10f, 0.25f, 0.45f, 0.75f, 1f)

                            for (i in 0 until checkpoints.lastIndex) {
                                val start = checkpoints[i]
                                val end = checkpoints[i + 1]

                                val steps = 20
                                val delayPerStep = 20L

                                for (step in 1..steps) {
                                    progress = start + (end - start) * (step / steps.toFloat())
                                    delay(delayPerStep)
                                }

                                delay(100)
                            }

                            isAnimating = false // Reset for next run
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun GrimReaperChaseProgressBar(progress: Float) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val animatedProgress = progress.coerceIn(0f, 1f)


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(150.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // Progress bar as platform
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .offset(y=-53.dp)
                .background(Color.Gray)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(15.dp)
                .offset(y=-53.dp)
                .background(Color(0xFFBE002A))
        )

        // Grim Reaper (moves with progress)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp, end = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val maxOffset = screenWidth.toPx() - 100.dp.toPx()
                            translationX = animatedProgress * maxOffset
                        }
                        .offset(x = (-20).dp)
                ) {
                    GrimReaperAttackAnimation(stop = animatedProgress == 1f)
                }

                // Running Man fixed at end
                // Running Man fixed at end (disappears at 90%+ progress)
                if (animatedProgress < 0.95f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (2).dp, y = -35.dp)
                    ) {
                        RunningManAnimation()
                    }
                }
                else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (2).dp, y = -35.dp)
                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.rip),
                            contentDescription = "RIP",
                            modifier = Modifier
                                .height(50.dp)
                                .width(30.dp)
                                .offset(x = (-2).dp, y = (-19).dp)
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun GrimReaperAttackAnimation(stop: Boolean) {
    var attacking by remember { mutableStateOf(stop) }

    // Trigger the attack
    LaunchedEffect(stop) {
        while (true) {
            attacking = true
            delay(400)
            attacking = false
            delay(1200)
            if(stop)
            {
                //attacking =false
                break
            }
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
            .offset(y=-35.dp)
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
fun RunningManAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "limbAnimations")

    val armAngleLeft by infiniteTransition.animateFloat(
        initialValue = 5f, // Opposite swing to right arm
        targetValue = -90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "armLeft"
    )

    val armAngleRight by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "arm"
    )

    val legAngle by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "leg"
    )

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        // Head
        Image(
            painter = painterResource(id = R.drawable.head),
            contentDescription = "Head",
            modifier = Modifier
                .size(10.dp)
                .offset(x = (-2).dp, y = (-59).dp)
        )

        // Torso
        Image(
            painter = painterResource(id = R.drawable.torso),
            contentDescription = "Torso",
            modifier = Modifier
                .size(15.dp, 40.dp)
                .offset(x = (-6).dp, y = -42.dp)
        )

        // Left Arm
        Image(
            painter = painterResource(id = R.drawable.left_hand),
            contentDescription = "Left Arm",
            modifier = Modifier
                .size(20.dp)
                .offset(x = (-11).dp, y = (-44).dp)
                .rotate(20f)
                .graphicsLayer {
                    rotationZ = armAngleLeft
                    transformOrigin = TransformOrigin(0.7f, 0.2f)
                }
        )

        // Right Arm
        Image(
            painter = painterResource(id = R.drawable.right_hand),
            contentDescription = "Right Arm",
            modifier = Modifier
                .size(20.dp)
                .offset(x = (4).dp, y = (-45).dp)
                .graphicsLayer {
                    rotationZ = armAngleRight
                    transformOrigin = TransformOrigin(0.0f, 0f)
                }
        )

        // Left Leg
        Image(
            painter = painterResource(id = R.drawable.left_leg),
            contentDescription = "Left Leg",
            modifier = Modifier
                .height(20.dp)
                .width(30.dp)
                .offset(x = (-15).dp, y = (-26).dp)
                .graphicsLayer {
                    rotationZ = legAngle
                    transformOrigin = TransformOrigin(0.7f, 0f)
                }
        )

        // Right Leg
        Image(
            painter = painterResource(id = R.drawable.right_leg),
            contentDescription = "Right Leg",
            modifier = Modifier
                .height(20.dp)
                .width(30.dp)
                .offset(x = (-5).dp, y = (-24).dp)
                .graphicsLayer {
                    rotationZ = -legAngle
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        )
    }
}



/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RunningManPreview() {
    RunningManAnimation()
}*/




