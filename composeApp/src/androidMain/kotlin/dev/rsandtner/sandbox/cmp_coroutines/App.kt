package dev.rsandtner.sandbox.cmp_coroutines

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(context: Context) {
    MaterialTheme {
        NetworkMonitorScreen(context)
    }
}
