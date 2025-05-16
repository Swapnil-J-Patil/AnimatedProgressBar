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
import androidx.compose.ui.unit.Dp
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
                    //GrimReaperAttackAnimation()
                   RunningManAnimation()
                    //RunningManCanvas(modifier = Modifier.padding(top=200.dp))
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
        modifier = Modifier
            .size(300.dp)
            .background(Color.White),
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
                .size(20.dp)
                .offset(x = (-15).dp, y = (-26).dp)
                .graphicsLayer {
                    rotationZ = legAngle
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        )

        // Right Leg
        Image(
            painter = painterResource(id = R.drawable.right_leg),
            contentDescription = "Right Leg",
            modifier = Modifier
                .size(20.dp)
                .offset(x = (-5).dp, y = (-24).dp)
                .graphicsLayer {
                    rotationZ = -legAngle
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        )
    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RunningManPreview() {
    RunningManAnimation()
}




