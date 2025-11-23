package dev.rsandtner.sandbox.cmp_coroutines.birds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class Bird(val birdName: String, val sound: String, val delay: Duration) {
    Owl("Tweety", "hoo-h’HOO-hoo", 2.seconds),
    Parrot("Zazu", "RAAAWK", 2.seconds),
    Pigeon("Woodstock", "coo coo", 3.seconds),
}

@Composable
@Preview
fun BirdsScreen(modifier: Modifier = Modifier) {

    var selectedBird by remember { mutableStateOf<Bird?>(null) }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Bird.entries.forEach { bird ->
                Button(
                    onClick = { selectedBird = bird },
                ) {
                    Text(bird.name)
                }
            }

            selectedBird?.let {
                Spacer(Modifier.height(24.dp))
                BirdComponent(it)

                Button(onClick = { selectedBird = null }) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
fun BirdComponent(
    bird: Bird,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(bird) {
        withContext(CoroutineName(bird.birdName)) {
            val name = coroutineContext[CoroutineName]?.name
            while (true) {
                println("$name: ${bird.sound}")
                delay(bird.delay)
            }
        }
    }

    Text(bird.name)
}
