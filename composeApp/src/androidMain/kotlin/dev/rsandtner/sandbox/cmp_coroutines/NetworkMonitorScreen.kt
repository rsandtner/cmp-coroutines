@file:OptIn(ExperimentalTime::class)

package dev.rsandtner.sandbox.cmp_coroutines

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Composable
fun NetworkMonitorScreen(
    viewModel: NetworkMonitorViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NetworkMonitorScreenContent(state = state)
}

@Composable
@Preview
fun NetworkMonitorScreenContent(state: NetworkMonitorState) {

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!state.networkConnected) {
                NoInternetBanner()
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Network history",
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(state.history.reversed()) { history ->
                        HistoryItem(
                            history = history,
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun NoInternetBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFCBDBD))
            .padding(16.dp)
    ) {
        Text(text = "No internet connection", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun HistoryItem(
    history: NetworkHistory,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (history.networkState == NetworkState.CONNECTED) "ONLINE" else "OFFLINE",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = history.formattedTimestamp,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse history" else "Expand history",
                    modifier = if (expanded) Modifier.rotate(180f) else Modifier
                )
            }
        }
        if (expanded) {
            Text(
                text = "lat: ${history.location.latitude}, long: ${history.location.longitude}",
                fontStyle = FontStyle.Italic
            )
        }
    }
}

class NetworkMonitorViewModel(
    private val networkObserver: NetworkObserver,
    private val locationObserver: LocationObserver,
) : ViewModel() {

    private var initialDataLoaded = false

    private val _state = MutableStateFlow(NetworkMonitorState())
    val state = _state
        .onStart {
            if (!initialDataLoaded) {
                observeNetwork()
                initialDataLoaded = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkMonitorState()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeNetwork() {
        networkObserver.observeNetwork()
            .distinctUntilChanged()
            .onEach { _state.value = _state.value.copy(networkState = it) }
            .zip(locationObserver.observeLocation(1.seconds)) { networkState, location ->
                NetworkHistory(Clock.System.now(), networkState, location)
            }
            .runningFold(emptyList<NetworkHistory>()) { history, historyItem -> history + historyItem }
            .onEach { history -> _state.update { it.copy(history = history) } }
            .launchIn(viewModelScope)
    }
}

data class NetworkMonitorState(
    val networkState: NetworkState = NetworkState.DISCONNECTED,
    val history: List<NetworkHistory> = emptyList()
) {
    val networkConnected = networkState == NetworkState.CONNECTED
}

data class NetworkHistory(
    val timestamp: Instant,
    val networkState: NetworkState,
    val location: Location
) {
    val formattedTimestamp: String
        get() {
            return ZonedDateTime.ofInstant(timestamp.toJavaInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
        }
}
