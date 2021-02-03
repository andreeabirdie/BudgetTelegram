package mobile.birdie.exam1

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main)

        connectivityManager = getSystemService(android.net.ConnectivityManager::class.java)
        Properties.instance.toastMessage.observe(
            this,
            { Toast.makeText(this, it, Toast.LENGTH_LONG).show() })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Properties.instance.internetActive.postValue(true)
            runOnUiThread {
                Properties.instance.toastMessage.value = "you're online"
            }
        }

        override fun onLost(network: Network) {
            Properties.instance.internetActive.postValue(false)
            runOnUiThread {
                Properties.instance.toastMessage.value = "you're offline"
            }
        }
    }
}