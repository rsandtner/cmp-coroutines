package dev.rsandtner.sandbox.cmp_coroutines

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class NetworkState {
    CONNECTED,
    DISCONNECTED
}

fun Context.observeNetwork(): Flow<NetworkState> {
    return callbackFlow {

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
                trySend(NetworkState.DISCONNECTED)
            }
        }

        val connectivityManager = getSystemService<ConnectivityManager>()!!
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}
