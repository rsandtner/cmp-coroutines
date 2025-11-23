package dev.rsandtner.sandbox.cmp_coroutines

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.rsandtner.sandbox.cmp_coroutines.birds.BirdsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        BirdsScreen()
    }
}
