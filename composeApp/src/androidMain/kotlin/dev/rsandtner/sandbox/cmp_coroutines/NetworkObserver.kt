package dev.rsandtner.sandbox.cmp_coroutines

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

enum class NetworkState {
    CONNECTED,
    DISCONNECTED
}

class NetworkObserver(
    private val context: Context,
    private val pingTimeout: Duration = 100.milliseconds
) {

    fun observeNetwork() = callbackFlow {
        val request = with(NetworkRequest.Builder()) {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            build()
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkState.CONNECTED)
            }

            override fun onLost(network: Network) {
                val status = try {
                    val reachable = network.getByName("www.google.com").isReachable(pingTimeout.inWholeMilliseconds.toInt())
                    if (reachable) NetworkState.CONNECTED else NetworkState.DISCONNECTED
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    NetworkState.DISCONNECTED
                }

                trySend(status)
            }
        }

        val connectivityManager = context.getSystemService<ConnectivityManager>()!!
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
        .flowOn(Dispatchers.IO)
}
