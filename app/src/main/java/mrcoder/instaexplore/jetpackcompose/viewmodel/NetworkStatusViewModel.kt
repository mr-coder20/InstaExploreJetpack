package mrcoder.instaexplore.jetpackcompose.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkStatusViewModel(context: Context) : ViewModel() {

    private val _isConnected = MutableStateFlow(isInternetConnected(context))
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // ایجاد NetworkCallback برای دریافت تغییرات وضعیت اتصال
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // وقتی اینترنت در دسترس است
                _isConnected.value = true
                Log.d("NetworkStatus", "Internet connected")
            }

            override fun onLost(network: Network) {
                // وقتی اتصال اینترنت از دست می‌رود
                _isConnected.value = false
                Log.d("NetworkStatus", "Internet lost")
            }
        }
        // ثبت callback برای دریافت تغییرات شبکه
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    private fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}

