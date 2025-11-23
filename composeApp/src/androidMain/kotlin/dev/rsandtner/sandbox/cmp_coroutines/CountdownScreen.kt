package dev.rsandtner.sandbox.cmp_coroutines

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun CountDownScreen(
    viewModel: CountDownViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    CountDownScreenContent(
        state = state.value,
        onStartClick = viewModel::startCountDown,
    )
}

@Composable
private fun CountDownScreenContent(
    state: CountDownState,
    onStartClick: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (state.countDownActive) {
                Text(
                    text = "${state.countDownValue}",
                    style = MaterialTheme.typography.headlineLarge,
                )
            } else {

                TextField(
                    modifier = Modifier.width(150.dp),
                    state = state.countDownInput,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        textAlign = TextAlign.Right
                    ),
                    suffix = {
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = "sec"
                        )
                    },
                )
                Button(
                    onClick = onStartClick,
                ) {
                    Text("Start")
                }

            }
        }
    }
}

class CountDownViewModel : ViewModel() {

    private val _state = MutableStateFlow(CountDownState())
    val state = _state
        .asStateFlow()

    fun startCountDown() {
        val countDownFrom = _state.value.countDownInput.text.toString().toIntOrNull() ?: 0
        if (countDownFrom <= 0) return

        countDown(countDownFrom)
            .onStart { _state.update { it.copy(countDownActive = true) } }
            .onEach { value -> _state.update { it.copy(countDownValue = value) } }
            .onCompletion { _state.update { it.copy(countDownActive = false) } }
            .launchIn(viewModelScope)
    }

    private fun countDown(start: Int) = flow {
        (start downTo 0)
            .onEach {
                emit(it)
                delay(1.seconds)
            }
    }
}

data class CountDownState(
    val countDownActive: Boolean = false,
    val countDownInput: TextFieldState = TextFieldState(initialText = "10"),
    val countDownValue: Int = 0,
)

@Composable
@Preview
fun CountDownScreenPreview() {
    MaterialTheme {
        CountDownScreenContent(
            state = CountDownState(countDownActive = false),
            onStartClick = {},
        )
    }
}
