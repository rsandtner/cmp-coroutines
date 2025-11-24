package dev.rsandtner.sandbox.cmp_coroutines

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkMonitorScreen(context: Context) {
    NetworkMonitorScreenContent(context)
}

@Composable
@Preview
fun NetworkMonitorScreenContent(context: Context) {

    val networkState = with(context) {
        observeNetwork().collectAsStateWithLifecycle(NetworkState.DISCONNECTED)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (networkState.value == NetworkState.DISCONNECTED) {
                NoInternetBanner()
            }

        }
    }
}

@Composable
fun NoInternetBanner(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFFCBDBD))
        .padding(16.dp)) {
        Text(text = "No internet connection", modifier = Modifier.align(Alignment.Center))
    }
}
