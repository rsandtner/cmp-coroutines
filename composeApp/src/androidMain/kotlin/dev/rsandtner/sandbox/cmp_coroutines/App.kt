package dev.rsandtner.sandbox.cmp_coroutines

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        CountDownScreen(
            viewModel = viewModel { CountDownViewModel() }
        )
    }
}
