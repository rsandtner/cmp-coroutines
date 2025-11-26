package dev.rsandtner.sandbox.cmp_coroutines

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App(context: Context) {
    MaterialTheme {
        val viewModel = viewModel {
            NetworkMonitorViewModel(
                networkObserver = NetworkObserver(context),
                locationObserver = LocationObserver(context)
            )
        }

        NetworkMonitorScreen(viewModel)
    }
}
