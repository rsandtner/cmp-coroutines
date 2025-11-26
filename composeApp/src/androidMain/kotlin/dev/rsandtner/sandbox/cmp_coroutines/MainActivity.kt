package dev.rsandtner.sandbox.cmp_coroutines

import android.Manifest.permission
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.ACCESS_NETWORK_STATE,
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION,
                permission.INTERNET
            ),
            0
        )

        setContent {
            App(this)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}
